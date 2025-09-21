package trust.jesus.discover.little.recognio

import android.widget.CheckBox
import trust.jesus.discover.fragis.SpeechFrag

class Ttsvals(speechFrag: SpeechFrag) {

    private val mSpeechFrag=speechFrag
    var usePartWord = false
    var doSpeak = false
    var curTTS_SpeakText = ""
    var retryWord: String? = null
    var toLearnText: String? = null
    var waitTime = 12
    var nextWords = 0
    var ignoreWords = 0
    var saw_cnt = 0
    var toSpeakLen = 0
    var toSpeakWordCnt: Int = 0
    var toLearnWordCnt: Int = 0
    var helpersCnt: Int = 0

    fun setUserCheckboxVal(cb: CheckBox): Boolean {
        val id: String? = mSpeechFrag.resources.getResourceEntryName(cb.id)
        mSpeechFrag.gc.appVals().valueWriteBool(id, cb.isChecked)
        return cb.isChecked
    }

    fun getUserCheckboxVal(cb: CheckBox): Boolean {
        val id: String? = mSpeechFrag.resources.getResourceEntryName(cb.id)
        val ret: Boolean = mSpeechFrag.gc.appVals().valueReadBool(id, cb.isChecked)
        cb.isChecked = ret
        return ret
    }

}