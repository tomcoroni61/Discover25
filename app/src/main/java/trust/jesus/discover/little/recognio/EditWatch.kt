package trust.jesus.discover.little.recognio

import android.os.Handler
import android.os.Looper
import android.view.View
import trust.jesus.discover.R
import trust.jesus.discover.fragis.SpeechFrag

class EditWatch(speechFrag: SpeechFrag): View.OnClickListener {
    private val mSpeechFrag=speechFrag
    private val binding=mSpeechFrag.binding
    private val ttsValues=mSpeechFrag.ttsvals
    val wordlist: MutableList<String> = ArrayList()
    private val timerHandler = Handler(Looper.getMainLooper())
    private var wordIndex = 0
    private var okiPos = 0
    private var ediCnt = 0
    private var helpersCnt = 0
    private var xLevel = 0

    fun startWatch() {
        mSpeechFrag.getSpeakText()
        if (mSpeechFrag.ttsvals.curTTS_SpeakText.isEmpty()) {
            mSpeechFrag.gc.globDlg().messageBox(mSpeechFrag.getString(R.string.no_text_to_speak))
            //Toast.makeText(this.requireContext(), mSpeechFrag.getString(R.string.please_enter_some_text), Toast.LENGTH_SHORT)                    .show()
            return
        }
        mSpeechFrag.speechReco?.buildWordList()
        doReset(true)
        mSpeechFrag.speechReco?.doLearnLevel()
        xLevel = ttsValues.difficultyLevel
    }

    fun sameWord(word1: String, word2: String): Boolean {
        if (word1.isEmpty() || word2.isEmpty()) return false
        if (word1 == word2) return true //ttsvals.getUserCheckboxVal(binding.cbPartWord) sometimes wrong?
        if ( !ttsValues.usePartWord ) return false
        if ( word1.indexOf(word2) > -1 ||  word2.indexOf(word1) > -1) {
            helpersCnt++
            binding.tvAllPartText.append("cbPartWord $word1 =? $word2  Errs = ${helpersCnt}\n")
            return true
        }
        //  ||  word2.indexOf(word1) > -1)

        return false
    }
    private fun recoKeyCheckWort(aWordi: String): Boolean {
        if (aWordi.isEmpty() || wordIndex >= wordlist.size) return false
        val aWord = aWordi//suerByList(aWordi)
        //binding.tvAllPartText.append("recoCheckWort $aWord ?=? ${wordlist[wordidx]} \n")
        if (sameWord(wordlist[wordIndex], aWord)) {
            textDone += aWordi
            checkWort(wordlist[wordIndex])
            return true
        }
        if (ttsValues.ignoreWords == 0) return false
        //binding.tvAllPartText.append(aWord + " recow??\n")
        var cnt = 1
        if (wordIndex+cnt == wordlist.size) {
            binding.tvAllPartText.append("ignoreWords last word  $aWord =? ${wordlist[wordIndex]}  Errs = ${helpersCnt}\n")
            wordIndex++
            checkWort(wordlist[wordIndex-1])
            helpersCnt++
            wordIndex=9999+wordlist.size
            return false
        }
        while (cnt <= ttsValues.ignoreWords && wordIndex+cnt < wordlist.size) {
            if (sameWord(wordlist[wordIndex+cnt], aWord)) {
                binding.tvAllPartText.append("ignoreWords  $aWord =? ${wordlist[wordIndex+cnt]}  Errs = ${helpersCnt}\n")
                checkWort(wordlist[wordIndex])
                textDone += aWord
                helpersCnt++
                return true
            }
            cnt++
        }
        return false
    }

    private var bussi = false
     fun checkText(text: String) {//= checkMatch by speechReco
         if (bussi) {
             binding.tvAllPartText.append("\n\nwoidx $wordIndex size: ${wordlist.size} bussi: \n$text \n\n")
             return
         }
         bussi = true
        var crashcnt = 0
        try {
            if (text.isEmpty()) {
                bussi = false
                return
            }
            if (helpersCnt > ediCnt+11)
                throw Exception("too many Errors")
            // doReset()
            crashcnt = 2
            val line = mSpeechFrag.speechReco!!.suerByList(text).uppercase().trim()
            val list = line.split(' ')
            crashcnt = 100
            val lasthelpersCnt = helpersCnt
            ediCnt = list.size
            binding.tvAllPartText.append("\n\n\ncheckText start errs: ${helpersCnt}\n wordIdx: $wordIndex  size: ${wordlist.size} \n")
            binding.tvAllPartText.append("$line \n\n")
            for (ls in list) {
                crashcnt ++
                //recoKeyCheckWort(ls.trim())
                if (!recoKeyCheckWort(ls.trim())) {
                    if (helpersCnt == lasthelpersCnt-1) {
                        helpersCnt++
                        binding.tvAllPartText.append("---checkText wrong: $ls  Errs = ${helpersCnt}\n")
                    }

                }
                //if (wordidx >= wordlist.size) break
                //if (!recoKeyCheckWort(ls.trim())) break
            }

            /* if (okiText.isEmpty()) return helpersCnt
            binding.tvkeyreci.text = okiText
            crashcnt =3000
            showNextWords() */
        } catch (e: Exception) {
            val txt = "$ediCnt words crashcnt: $crashcnt  \n" + e.message.toString()
            binding.tvkeyreci.text = txt
            binding.keypadText.setText(txt)
        }
         bussi = false
    }

    fun listCheck(list1: MutableList<String>, list2: MutableList<String>): Boolean {
        var cnt = 0
        while (cnt < list1.size) {
            val ls = list1[cnt]
            if (list2.contains(ls)) {
                list2.remove(ls)
                list1.remove(ls)  //geht..list -= ls
            } else
                cnt++
        }
        return list2.isEmpty() || list1.isEmpty()
    }
    fun endCheck(moveFocus: Boolean) {
        val text = binding.keypadText.text.toString()
        val line = mSpeechFrag.speechReco!!.suerByList(text)?.uppercase()?.trim()
        val list = line?.split(' ')?.toMutableList()
        wordlist.clear()
        wordlist.addAll(mSpeechFrag.speechReco!!.wordlist)
        val wordcnt = wordlist.size

        var cnt = wordcnt
        while (cnt > 0 && listCheck(list!!, wordlist)) cnt--

        var txt = ""
        if (list!!.size > 0) txt = "not found: " + list.joinToString(", ") + "\n"
        if (wordlist.size > 0) txt += "not used: " + wordlist.joinToString(", ")

        helpersCnt = list.size + wordlist.size
        txt = ttsValues.curTTS_SpeakText + "\n" + txt
        binding.tvkeyreci.text = txt
        //if (helpersCnt > 0) {
            //helpersCnt += abs(ediCnt - wordlist.size)
        val level = 100 - (helpersCnt * 100 / wordcnt)
        txt = "accuracy $level% err=${helpersCnt}  " +
               mSpeechFrag.speechReco!!.getEndDoneText(level)
    //}
        binding.tvkeyreciNextWord.text = txt
        txt = binding.keypadText.text.toString() + "\n" + txt
        //binding.keypadText.setText(txt)
        binding.tvAllPartText.append("\n"+txt)
        if (moveFocus) {
            binding.cbKeyRecoAutoCheck.requestFocus()
            binding.cbKeyRecoAutoCheck.isChecked = false
        }
    }

    private fun doFoundWords() {
        if (wordIndex >= wordlist.size) return
        val txt = binding.tvkeyreci.text as String + wordlist[wordIndex] + " "
        binding.tvkeyreci.text = txt
        //binding.tvDedacText.text = txt
        wordIndex++;    showNextCnt++
        timerHandler.postDelayed(runDoShowNextWords, ttsValues.waitTime.toLong()*100)
    }
    private val runDoShowNextWords: Runnable = Runnable {
        showNextCnt--
        if (showNextCnt > 0) return@Runnable
        showNextWords()
        //timeTextChanged = 0L
    }//Timhandl.postDelayed(runDoNextWord, ttsvals.waitTime.toLong()*100)
    private var showNextCnt = 0
    private fun showNextWords() {
        if (ttsValues.showNextWords < 1) return
        var words = ""
        var cnt = wordIndex
        var nw = 0
        while (cnt < wordlist.size && nw < ttsValues.showNextWords ) {
            words += wordlist[cnt] + " "
            cnt++
            nw++
        }
        binding.tvkeyreciNextWord.text = words
    }

    private fun checkWort(aWord: String): Boolean {
        if ( wordIndex >= wordlist.size) wordIndex = wordlist.size-1
        val guwo = wordlist[wordIndex]
        // binding.textView.text = aWord + " =? " + checkText
        if (guwo == aWord) {
            if (wordIndex < wordlist.size-1)
                doFoundWords() //erhöht wordidx
            else {//all okay
                wordIndex=9999+wordlist.size;       showNextCnt=99
                endCheck(true)

            }
        } else {
            return false
        }
        return true
    }
    private var timeTextChanged = 0L
    fun handleTextChanged() {
        if (timeTextChanged==0L) timerHandler.postDelayed(runEditChange, 100L)
        timeTextChanged = System.currentTimeMillis()
    }
    private var textDone = ""
    private fun doRecordedText(text: String) {
        if (text == textDone || text.isEmpty()) return
        textDone = text
        if (wordIndex > 1 && wordIndex < wordlist.size) {
            val pos = text.indexOf(wordlist[wordIndex], okiPos, true)
            if (pos > -1) {
                okiPos = pos

            }
        }
        var newText = text
        if (okiPos < text.length)
            newText = text.substring(okiPos)
        checkText(newText)

    }
    fun doReset(doAll: Boolean = false){
        if (doAll) {
            binding.keypadText.setText("")
            binding.tvAllPartText.text = ""
            binding.tvkeyreciNextWord.text = ""
            helpersCnt = 0;             showNextCnt = 0
            timeTextChanged = 0L
            textDone = ""
            if (ttsValues.curTTS_SpeakText.length > 11) {
                binding.tvkeyreciNextWord.text = ttsValues.curTTS_SpeakText.substring(0, 10)

            }
        }
        binding.tvkeyreci.text = ""
        wordlist.clear()
        wordlist.addAll(mSpeechFrag.speechReco!!.wordlist)
        wordIndex = 0
        okiPos = 0
    }
    fun doEmptyClick() {
        binding.edSearchR.setFocusable(true)
        doReset(true)
        binding.keypadText.setFocusable(true)
    }
    private val runEditChange: Runnable = object : Runnable {
        override fun run() {//Timhandl.postDelayed(runEditChange, 100)
            val timi = System.currentTimeMillis() - timeTextChanged
            if (timi < 300L) { //ttsvals.waitTime.toLong()*
                timerHandler.postDelayed(runEditChange, 100)
                return
            }
            timeTextChanged = 0L
            if (wordIndex >= wordlist.size) return
            doRecordedText(binding.keypadText.text.toString())

            if (xLevel != ttsValues.difficultyLevel) {
                mSpeechFrag.speechReco?.doLearnLevel()
                xLevel = ttsValues.difficultyLevel
            }
            //timeTextChanged = 0L
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.cbKeyRecoAutoCheck -> startWatch()
            R.id.keypadClear -> doEmptyClick()
            R.id.keypadCheck -> endCheck(false)
        }
    }
}