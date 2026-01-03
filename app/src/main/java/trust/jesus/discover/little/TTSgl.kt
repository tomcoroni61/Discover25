package trust.jesus.discover.little

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.Voice
import trust.jesus.discover.bible.dataclasses.VersItem
import java.util.Locale


class TTSgl() {
    var ttobj: TextToSpeech? = null
    private val gc = Globus.getAppContext() as Globus
    private var initOk = false;    private var doReInit = false
    private var curEngine = ""

    init {
        startTTS()
    }

    //if new engine is selected
    fun restart() {
        try {
            if (ttobj != null) {
                ttobj!!.stop()
                ttobj!!.shutdown()
                ttobj = null
            }
        } catch (e: Exception) {
            // gc.errReport(e, "TTSgl.restart", true);
        }
        startTTS()
        //gc.toast("TTS restart done")
        doReInit = false
        if (spkTxt.length > 3)
            Looper.myLooper()?.let { Handler(it).postDelayed({
                speak(spkTxt); spkTxt="" }, 1100) }
    }

    var defEngine = ""; private var lastDefEngine = ""
    private fun startTTS() {
        if (!doReInit) initOk = false
        try {
            lastDefEngine = defEngine
            //!!needs null check!!
            if (defEngine!=null // = fake news .. 01.26
                && defEngine.length >3)
                ttobj = TextToSpeech(gc, onInitListener, defEngine) else
                    ttobj = TextToSpeech(gc, onInitListener)
        } catch (e: Exception) {
            gc.crashLog("startTTS  ex: " + e.message, 50)//errReport(e, "", true)

        }
        if (ttobj == null) return
        //ttobj!!.setSpeechRate(1.0f)

        curEngine = ttobj?.defaultEngine.toString()
        gc.log("curEngine: $curEngine")
        val engList = ttobj?.engines
        for (engine in engList!!) {
            gc.log( "engine: ${engine.name}  ${engine.label}  ${engine.icon}")
        }
    }

    private val onInitListener: OnInitListener = OnInitListener { status ->
        if (status == TextToSpeech.SUCCESS) {
            //ttobj.setLanguage(Locale.GERMANY);
            initOk = true // gc.Logl("Press again, Speech ok", true);
            //gc.toast("TTS ok")
        } else gc.logl("speech failed: $status", true)
    }
    fun andoSettings() {
        val intent = Intent()
        intent.action = "com.android.settings.TTS_SETTINGS"
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); todo
        //gc.mainActivity!!.startActivityForResult(intent, 55)
        gc.mainActivity!!.startActivity(intent)
        //doReInit = true
    }

    fun cleanSpeak(txt: String) {
        if (txt.isEmpty()) return
        speak(gc.doSpeaktext(txt))
    }
    fun currentEngine(): String {
        if (defEngine.length>3) return defEngine
        return ttobj?.defaultEngine.toString()
    }
    private var spkTxt = ""
    fun speak(txt: String) {
        val engine  = ttobj?.defaultEngine.toString()
        if (defEngine.length>3 && defEngine != lastDefEngine) {
            val bb = gc.appVals().valueReadBool(defEngine+"_restart", false)
            if (bb) doReInit = true
        }
        if (engine != curEngine) {
            gc.log("speak: engine != curEngine")
            doReInit = true
        }
        if (doReInit) {
            spkTxt = txt
            restart()
            return
        }
        if (txt.isEmpty()) return
        if (!initOk || ttobj == null) {
            if (txt.length > 5) gc.toast("Init TTS failed!!")
            return
        }
        lastDefEngine = ""
        try {
            if (ttobj!!.isSpeaking) ttobj!!.stop() else ttobj!!.speak(
                txt,
                TextToSpeech.QUEUE_FLUSH, null, null  )
        } catch (e: Exception) {
            gc.crashLog("TTSgl.speak ex: " + e.message, 352) //errReport(e, "", true)
        }
    }

    fun speakVersList( list: MutableList<VersItem>) {
        if (doReInit) restart()
        if (list.isEmpty()) return
        if (!initOk || ttobj == null) {
            gc.toast("Init TTS failed!!")
            return
        }
        val myHash = HashMap<String?, String?>()
        myHash[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "done"
        val speakVersPause = gc.appVals().valueReadInt("speakVersPause", 0)
        val repeatVerses = gc.appVals().valueReadInt("repeatVerses", 0)

        for (item in list) {
            if (item.positon == 0)
                ttobj!!.speak( gc.doSpeaktext(item.vers),TextToSpeech.QUEUE_FLUSH,null, myHash.toString() ) else
                    ttobj!!.speak( gc.doSpeaktext(item.vers),TextToSpeech.QUEUE_ADD, null,myHash.toString() )

            ttobj!!.playSilentUtterance((speakVersPause * 1000).toLong(), TextToSpeech.QUEUE_ADD, null)

            if (repeatVerses > 0) {
                for (i in 1..<repeatVerses) {
                    ttobj!!.speak( gc.doSpeaktext(item.vers),TextToSpeech.QUEUE_ADD, null,myHash.toString() )
                    ttobj!!.playSilentUtterance((speakVersPause * 1000).toLong(), TextToSpeech.QUEUE_ADD, null)
                }
            }
        }
    }
    fun stop() {
        if (ttobj == null) return
        if (ttobj!!.isSpeaking) ttobj!!.stop()
    }
    fun isSpeaking(): Boolean {
        if (ttobj == null) return false
        return ttobj!!.isSpeaking
    }
    fun setLanguageAndVoice(locale: Locale, voice: Int) {
        //val desiredLocale: Locale? = Locale.US // Change to the desired language/locale
        if (doReInit) restart()
        ttobj!!.language = locale //if (voiceIdx > voiceList.size-1) voiceIdx = 0;

        val voices: MutableSet<Voice?> = ttobj!!.voices
        val voiceList: MutableList<Voice?> = ArrayList(voices)
        if (voiceList.isEmpty()) return
        var voiceIdx = voice
        if (voiceIdx > voiceList.size - 1) voiceIdx = 0
        val selectedVoice = voiceList[voiceIdx] // Change to the desired voice index
        ttobj!!.voice = selectedVoice
    }
}
