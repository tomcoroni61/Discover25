package trust.jesus.discover.little

import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.Voice
import java.util.Locale

class TTSgl() {
    var ttobj: TextToSpeech? = null
    private val gc = Globus.getAppContext() as Globus
    private var initOk = false
    private var doreinit = false

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
        gc.toast("TTS restart done")
        doreinit = false
    }

    private fun startTTS() {
        if (!doreinit) initOk = false
        try {
            ttobj = TextToSpeech(gc) { status: Int ->  //mContext
                if (status == TextToSpeech.SUCCESS) {
                    //ttobj.setLanguage(Locale.GERMANY);
                    initOk = true // gc.Logl("Press again, Speech ok", true);
                    gc.toast("TTS ok")
                } else gc.Logl("speech failed: " + status, true)
            }
        } catch (e: Exception) {
            gc.errReport(e, "startTTS", true)
        }
    }

    fun andoSetttings() {
        val intent = Intent()
        intent.action = "com.android.settings.TTS_SETTINGS"
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        gc.mainActivity!!.startActivityForResult(intent, 55)
        doreinit = true
    }

    fun cleanSpeak(txt: String) {
        if (txt.isEmpty()) return
        speak(gc.doSpeaktext(txt))
    }
    fun speak(txt: String) {
        if (doreinit) restart()
        if (txt.isEmpty()) return
        if (!initOk || ttobj == null) {
            gc.toast("Init TTS failed!!")
            return
        }
        try {
            if (ttobj!!.isSpeaking) ttobj!!.stop() else ttobj!!.speak(
                txt,
                TextToSpeech.QUEUE_FLUSH, null, null
            )
        } catch (e: Exception) {
            gc.errReport(e, "TTSgl.speak", true)
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
        if (doreinit) restart()
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
