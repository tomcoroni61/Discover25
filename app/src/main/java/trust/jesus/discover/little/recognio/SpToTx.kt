package trust.jesus.discover.little.recognio

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import trust.jesus.discover.little.Globus
import java.util.Locale

//@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class SpToTx (
    private val context: Context,
    private val callback: RecognitionCallback? = null
) : RecognitionListener {

    var isActivated: Boolean = false
    var shouldMute: Boolean = true
    var contiousRecording = false

    private var firstInit: Boolean = false

    private val gc: Globus = Globus.Companion.getAppContext() as Globus
    private var speech: SpeechRecognizer? = null  // by lazy { SpeechRecognizer.createSpeechRecognizer(context) }
    private val audioManager: AudioManager? = context.getSystemService()
    private val timhandl = Handler(Looper.getMainLooper())
    var langModel = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
    //private var maxResults = 3
    var speechLang: Locale = Locale.getDefault()


    private val runStartRecognition: Runnable = object : Runnable {
        //geht nur wenn handy eingeschalten ist.
        override fun run() {
            if (!isActivated) return
            /*if (System.currentTimeMillis() - recognitionStarttime < -6000) {
                stopRecognition()
                destroyRecognizer()
                createRecognizer()
            } */

            if (contiousRecording) continueRecognition()

        }
    }// timhandl.postDelayed(runStartRecognition, 1111)
    val errSpeachTimeout = 88
    private var lastRecognition = 0L
    private var recognitionStarttime = 0L
    private val runTimeOutCheck: Runnable = object : Runnable {
        //geht nur wenn handy eingeschalten ist.
        override fun run() {
            if (!isActivated) return
            if (System.currentTimeMillis() - lastRecognition > 36000) {
                stopRecognition()
                callback?.onError(errSpeachTimeout)
            } else
                timhandl.postDelayed(runTimeOutCheck, 1111)
        }
    }
    fun createRecognizer() {//startRecognition() runStartRecognition
        destroyRecognizer()
        speech = SpeechRecognizer.createSpeechRecognizer(context)
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speech?.setRecognitionListener(this)
            callback?.onPrepared(RecognitionStatus.SUCCESS)
        } else {
            callback?.onPrepared(RecognitionStatus.UNAVAILABLE)
        }
    }

    fun destroyRecognizer() {
        if (speech == null) return
        //muteRecognition(false)
        speech?.destroy()
        speech = null
    }

    fun canRecordAudio(): Boolean {
        if (!firstInit) {
            if (ContextCompat.checkSelfPermission(gc,
                    Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(gc.mainActivity!!, arrayOf(Manifest.permission.RECORD_AUDIO),
                    123)
                callback?.onPrepared(RecognitionStatus.Err_RECORD_AUDIO_Permission)
                return false
            }
            createRecognizer()
            firstInit = true
        }
        return true
        //return context.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    } //context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == 0

    private fun continueRecognition() {
        stopContinuesRecognition()
        createRecognizer()  //with destroyRecognizer
        val maxResults = gc.appVals().valueReadInt("srMaxResults", 1)
        if (gc.appVals().valueReadString("srLocale", "local") == "local")
            speechLang = Locale.GERMAN else speechLang = Locale.ENGLISH
        val lang = gc.appVals().valueReadInt("srLanguage", 0)
        when (lang) {
            0 -> speechLang = Locale.getDefault()
            1 -> speechLang = Locale.GERMANY
            2 -> speechLang = Locale.ENGLISH
            3 -> speechLang = Locale.FRANCE
            4 -> speechLang = Locale.ITALY
            5 -> speechLang = Locale.CHINESE
        }
        langModel = gc.appVals().valueReadString("srLangModel", RecognizerIntent.LANGUAGE_MODEL_FREE_FORM).toString()
        val recognizerIntent =
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    langModel)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, speechLang)
                //putExtra(RecognizerIntent.EXTRA_HIDE_PARTIAL_TRAILING_PUNCTUATION, true)
                //@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                //putExtra(RecognizerIntent.EXTRA_ENABLE_LANGUAGE_DETECTION, false)
                //putExtra(RecognizerIntent.EXTRA_ORIGIN, true)
            }
        speech?.startListening(recognizerIntent)
        timhandl.postDelayed(runTimeOutCheck, 17111)
        muteRecognition(shouldMute)
        recognitionStarttime = System.currentTimeMillis()
    }
    fun startRecognition() {
        if (isActivated) stopRecognition()
        if (!canRecordAudio()) return
        isActivated = true
        continueRecognition()
    }
    fun stopContinuesRecognition() {
        recognitionStarttime = System.currentTimeMillis()
        if (speech == null) return
        speech?.stopListening()
    }
    fun stopRecognition() {
        isActivated = false
        stopContinuesRecognition()
        muteRecognition(false)
    }

    fun cancelRecognition() {
        speech?.cancel()
        recognitionStarttime = System.currentTimeMillis()
    }

    fun getErrorText(errorCode: Int): String = when (errorCode) {
        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
        SpeechRecognizer.ERROR_CLIENT -> "Client side error"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
        SpeechRecognizer.ERROR_NETWORK -> "Network error"
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
        SpeechRecognizer.ERROR_NO_MATCH -> "No match"
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
        SpeechRecognizer.ERROR_SERVER -> "Error from server"
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT, errSpeachTimeout -> "No speech input"
        else -> "Didn't understand, please try again."
    }
    //@Suppress("DEPRECATION")
    private fun muteRecognition(mute: Boolean) {
        val zm =Settings.Global.getInt(gc.contentResolver, "zen_mode")
        if (zm > 0) return
        audioManager?.let { //setStreamVolume
            val flag = if (mute) AudioManager.ADJUST_MUTE else AudioManager.ADJUST_UNMUTE
            //it.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, flag, 0)
            //it.adjustStreamVolume(AudioManager.STREAM_ALARM, flag, 0)
            it.adjustStreamVolume(AudioManager.STREAM_MUSIC, flag, 0)
            //it.adjustStreamVolume(AudioManager.STREAM_RING, flag, 0)
            it.adjustStreamVolume(AudioManager.STREAM_SYSTEM, flag, 0)
        }
    }

    override fun onBeginningOfSpeech() {
        if (!isActivated) return
        callback?.onBeginningOfSpeech()
    }

    override fun onReadyForSpeech(params: Bundle) {
        muteRecognition(shouldMute || !isActivated)
        callback?.onReadyForSpeech(params)
        //gc.Logl("onReadyForSpeech", true)
    }

    override fun onBufferReceived(buffer: ByteArray) {
        //callback?.onBufferReceived(buffer)
    }

    override fun onRmsChanged(rmsdB: Float) {
        callback?.onRmsChanged(rmsdB)
    }

    override fun onEndOfSpeech() {
        //callback?.onEndOfSpeech()
        //gc.Logl("onEndOfSpeech", true)
    }

    override fun onError(errorCode: Int) {
        if (!isActivated) return
        callback?.onError(errorCode)
        //isActivated = false
        //gc.Logl("onError", true)
        when (errorCode) {
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> cancelRecognition()
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                stopRecognition()
                //destroyRecognizer()
                //createRecognizer()
            }
            SpeechRecognizer.ERROR_NO_MATCH -> lastRecognition = System.currentTimeMillis()

        }

        if (contiousRecording) //startRecognition()
            timhandl.postDelayed(runStartRecognition, 1351)
    }

    override fun onEvent(eventType: Int, params: Bundle) {
        //callback?.onEvent(eventType, params)
    }

    override fun onPartialResults(partialResults: Bundle) {
        if (!isActivated) return
        val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null) {//isActivated &&
            callback?.onPartialResults(matches)
        }
        lastRecognition = System.currentTimeMillis()
        //gc.Logl("onPartialResults", true)
    }

    override fun onResults(results: Bundle) {
        if (!isActivated) return
        //gc.Logl("onResults", true)
        val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val scores = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)
        if (matches != null) {//isActivated &&
            callback?.onResults(matches, scores)
        }
        //gc.Logl("onResults", false)
        lastRecognition = System.currentTimeMillis()
        if (contiousRecording) //startRecognition()
            timhandl.postDelayed(runStartRecognition, 261)
        /*
        if (matches != null) {
            if (isActivated) {
                isActivated = false
                callback?.onResults(matches, scores)
                stopRecognition()
            } else {
                matches.firstOrNull { it.contains(other = activationKeyword, ignoreCase = true) }
                    ?.let {
                        isActivated = true
                        callback?.onKeywordDetected()
                    }
                startRecognition()
            }
        }
        */
    }

}