package trust.jesus.discover.little.recognio

import android.widget.CheckBox
import trust.jesus.discover.fragis.SpeechFrag

class VallsTts(speechFrag: SpeechFrag) {

    private val mSpeechFrag=speechFrag
    var keepScreenOn = false
    var doSpeak = false
    var curTTS_SpeakText = ""
    var goOnWord: String = "--"
    var retryWord: String = "--"
    var doSpeakWord: String = "--"

    var toLearnText: String? = null
    var waitTime = 12
    var difficultyLevel = 0
    var showNextWords = 0
    var xAutoNext = 0
    var ignoreWords = 0
    var usePartWord = false
    var usePartReco = false
    var partWordProzent = 65
    var partWordFoundProzent = 65
    var saw_cnt = 0
    var toSpeakLen = 0
    var toSpeakWordCnt: Int = 0
    var toLearnWordCnt: Int = 0
    var helpersCnt = 0
    var xAutoNextCount = 0

    fun writeAndGetCheckboxVal(cb: CheckBox): Boolean {
        val id: String? = mSpeechFrag.resources.getResourceEntryName(cb.id)
        mSpeechFrag.gc.appVals().valueWriteBool(id, cb.isChecked)
        return cb.isChecked
    }

    fun readAndSetCheckboxVal(cb: CheckBox): Boolean {
        val id: String? = mSpeechFrag.resources.getResourceEntryName(cb.id)
        val ret: Boolean = mSpeechFrag.gc.appVals().valueReadBool(id, cb.isChecked)
        cb.isChecked = ret
        return ret
    }

}