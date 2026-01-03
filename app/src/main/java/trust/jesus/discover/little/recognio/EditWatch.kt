package trust.jesus.discover.little.recognio

import android.os.Handler
import android.os.Looper
import android.view.View
import trust.jesus.discover.R
import trust.jesus.discover.fragis.Speech2

/****  Sequence
1  handleTextChanged -> runEditChange
    -> doRecordedText  first “processing” seperates doneText
    2.  checkText(newText)  compares last, make a List of words,
        recoKeyCheckWort() proofs it  over
        checkWort() -> doFoundWords()  + endCheck()
 */
class EditWatch(speechFrag: Speech2): View.OnClickListener {
    private val mFragSpeech2=speechFrag
    private val binding=mFragSpeech2.binding
    private val valuesKks=mFragSpeech2.valsKs
    private val wordlist: MutableList<String> = ArrayList()
    private val missedWords: MutableList<String> = ArrayList()
    private val missedExactWordlist: MutableList<String> = ArrayList()
    private val timerHandler = Handler(Looper.getMainLooper())
    private var wordIndex = 0
    private var okiPos = 0
    private var ediCnt = 0
    private var helpersCnt = 0
    private var xLevel = 0
    private var someFuncS: SpeechEx? = null

    //checkword() mit endCheck()-with change focus
    fun startWatch() { //focuschanged
        if (someFuncS==null) {
            if (mFragSpeech2.gc.mSpeechEx==null) {//should never happen
                mFragSpeech2.gc.globDlg().messageBox("Intern Bug 35", mFragSpeech2.requireContext())
                return
            }
            someFuncS = mFragSpeech2.gc.mSpeechEx
        }
        mFragSpeech2.getSpeakText()
        if (mFragSpeech2.valsKs.curTtsSpeakText.isEmpty()) {
            //no text for speach recognitian !!
            mFragSpeech2.gc.globDlg().messageBox(mFragSpeech2.getString(R.string.no_text_to_speak), mFragSpeech2.requireContext())
            //Toast.makeText(this.requireContext(), mSpeechFrag.getString(R.string.please_enter_some_text), Toast.LENGTH_SHORT)                    .show()
            return
        }
        mFragSpeech2.buildWordList()
        doReset(true)
        mFragSpeech2.doLearnLevel()
        mFragSpeech2.binding.cscroliFlow.visibility = View.GONE
        xLevel = valuesKks.difficultyLevel
        someFuncS?.valBase = valuesKks as VallsBase
        someFuncS?.valBase?.logTv = binding.tvAllPartText

        val mScrollView = binding.svRejected
        mScrollView.scrollTo(0, 0)

    }

    fun endCheck(moveFocus: Boolean) {
        if (valuesKks.withEndCheck) {
            val text = binding.keypadText.text.toString()
            if (text.isEmpty()) { //mSpeechFrag.getString(R.string.no_text_to_speak)
                mFragSpeech2.gc.globDlg().messageBox("no text!!", mFragSpeech2.requireContext())
                return
            }
            val line = mFragSpeech2.gc.mSpeechEx!!.suerByList(text).uppercase().trim()
            val list = line.split(' ').toMutableList()
            wordlist.clear()
            wordlist.addAll(mFragSpeech2.wordlist)
            val wordcnt = wordlist.size

            var cnt = wordcnt
            while (cnt > 0 && listCheck(list, wordlist)) cnt--

            var txt = ""
            if (list.isNotEmpty()) txt = "not found: " + list.joinToString(", ") + "\n"
            if (wordlist.isNotEmpty()) txt += "not used: " + wordlist.joinToString(", ")

            helpersCnt = list.size + wordlist.size
            txt = valuesKks.curTtsSpeakText + "\n" + txt
            binding.tvkeyreci.text = txt
            //if (helpersCnt > 0) {
            //helpersCnt += abs(ediCnt - wordlist.size)
            val level = 100 - (helpersCnt * 100 / wordcnt)
            txt = "accuracy $level% err=${helpersCnt}  " +
                    mFragSpeech2.gc.mSpeechEx!!.getEndDoneText(level)
            //}
            binding.tvkeyreciNextWord.text = txt
            txt = binding.keypadText.text.toString() + "\n" + txt
            binding.keypadText.setText(txt)
            binding.tvAllPartText.append("\n" + txt)
        } else {
            binding.tvkeyreciNextWord.text = ""
            binding.keypadText.setText("")
        }
        binding.cscroliFlow.visibility = View.VISIBLE
        if (moveFocus) {
            binding.cscroliFlow.requestFocus()
            //binding.cbKeyRecoAutoCheck.requestFocus()
            //binding.cbKeyRecoAutoCheck.isChecked = false
        }
    }

    private fun recoKeyCheckWort(aWordi: String): Boolean {
        if (aWordi.isEmpty() || wordIndex >= wordlist.size) return false
        val aWord = aWordi//suerByList(aWordi)
        binding.tvAllPartText.append("recoCheckWort $aWord ?=? ${wordlist[wordIndex]} \n")
        var oki=false


        if ( someFuncS?.sameWord(wordlist[wordIndex], aWord) == true
        ) {
            textDone += aWordi
            checkWort(wordlist[wordIndex])
            oki = true
        }
        if (oki) return true

        if (valuesKks.ignoreWords > 0) {
            //binding.tvAllPartText.append(aWord + " recow??\n")
            var cnt = 1
            if (wordIndex + cnt == wordlist.size) {
                //vallsTts.helpersCnt++
                //wa = WordArt.NextFound
                binding.tvAllPartText.append("\nErr? ignoreWords last word  $aWord =? ${wordlist[wordIndex]}  Errs = ${valuesKks.helpersCnt}\n")
                checkWort(wordlist[wordIndex])
                return true
            }
            while (cnt <= valuesKks.ignoreWords && wordIndex + cnt < wordlist.size) {
                if (someFuncS?.sameWord(wordlist[wordIndex + cnt], aWord)==true) {
                //if (sameWord(wordlist[wordIndex + cnt], aWord)) {
                    binding.tvAllPartText.append("\nErr? ignoreWords found  $aWord =? ${wordlist[wordIndex + cnt]}  Errs = ${valuesKks.helpersCnt}\n")
                    //wa = WordArt.NextFound
                    checkWort(wordlist[wordIndex])
                    if (!frommissedWordlist) {
                        addToMissedWordlist(aWord)
                        binding.tvAllPartText.append("ignoreWords add:  $aWord  to missedWordlist\n")
                    } //neeed for next word
                    //vallsTts.helpersCnt++ no error..
                    return true
                }
                cnt++
            }
        }





        if (!frommissedWordlist) addToMissedWordlist(aWord)

        return false
    }

    private var bussi = false
     fun checkText(newText: String) {//from doRecordedText()
         if (bussi) {
             binding.tvAllPartText.append("\n\nwoidx $wordIndex size: ${wordlist.size} bussi: \n$newText \n\n")
             return
         }
         bussi = true
        var crashcnt = 0
        try {
            if (newText.isEmpty()) {
                bussi = false
                return
            }
            if (helpersCnt > ediCnt+11)
                throw Exception("too many Errors")
            // doReset()
            crashcnt = 2
            val line = mFragSpeech2.gc.mSpeechEx!!.suerByList(newText).uppercase().trim()
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
        } catch (e: Exception) {//no real crash here.. like throw Exception("too many Errors")
            val txt = "$ediCnt words crashcnt: $crashcnt  \n" + e.message.toString()
            binding.tvkeyreci.text = txt //record stops now..
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

    private fun doFoundWords() {
        if (wordIndex >= wordlist.size) return
        val txt = binding.tvkeyreci.text as String + wordlist[wordIndex] + " "
        binding.tvkeyreci.text = txt
        //binding.tvDedacText.text = txt
        wordIndex++;
        if (showNextCnt < 1)
            timerHandler.postDelayed(runDoShowNextWords, valuesKks.waitTimeShowNextWord.toLong()*100)
        if (showNextCnt<2)
            showNextCnt++
    }
    private val runDoShowNextWords: Runnable = Runnable {
        showNextCnt--
        if (showNextCnt > 0) {
            timerHandler.postDelayed(runDoShowNextWords, valuesKks.waitTimeShowNextWord.toLong()*100)
            return@Runnable
        }
        showNextWords()
        //timeTextChanged = 0L
    }//Timhandl.postDelayed(runDoNextWord, vallsTts.waitTime.toLong()*100)
    private var showNextCnt = 0
    private fun showNextWords() {
        if (valuesKks.showNextWords < 1) return
        var words = ""
        var cnt = wordIndex
        var nw = 0
        while (cnt < wordlist.size && nw < valuesKks.showNextWords ) {
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
                doFoundWords()
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
            timeTextChanged = 0L;            textDone = ""
            missedWords.clear()
            if (valuesKks.curTtsSpeakText.length > 11) {
                binding.tvkeyreciNextWord.text = valuesKks.curTtsSpeakText.substring(0, 10)

            }
        }
        binding.tvkeyreci.text = ""
        wordlist.clear()
        wordlist.addAll(mFragSpeech2.wordlist)
        wordIndex = 0
        okiPos = 0
    }
    fun doEmptyClick() {
        //binding.edSearchR.setFocusable(true) todo?
        if (binding.keypadText.toString().isEmpty())
            endCheck(false)
        doReset(true)
        binding.keypadText.setFocusable(true)
    }
    private val runEditChange: Runnable = object : Runnable {
        override fun run() {//Timhandl.postDelayed(runEditChange, 100)
            val timi = System.currentTimeMillis() - timeTextChanged
            if (timi < 300L) { //vallsTts.waitTime.toLong()*
                timerHandler.postDelayed(runEditChange, 100)
                return
            }
            timeTextChanged = 0L
            if (wordIndex >= wordlist.size) return
            doRecordedText(binding.keypadText.text.toString())

            if (xLevel != valuesKks.difficultyLevel) {
                mFragSpeech2.doLearnLevel()
                xLevel = valuesKks.difficultyLevel
            }
            //timeTextChanged = 0L
        }
    }

    // #############    missedExactWordlist     missedWordlist  things  #######################
    private fun addToMissedWordlist(aWord: String) {
        if (!valuesKks.useMissedList ) return
        for (i in wordIndex..< wordlist.size) {
            if (wordlist[i] == aWord) {
                missedExactWordlist.add(wordlist[i])
                return
            }
        }
        missedWords.add(aWord)
    }
    private fun isInExactMissedWordlist(): Boolean {
        for (word in missedExactWordlist) {
            if (word == wordlist[wordIndex]) {
                missedExactWordlist.remove(word)
                //wa = WordArt.RightSpoken
                checkWort(wordlist[wordIndex])
                return true
            }
        }
        return false
    }
    private var frommissedWordlist = false;

    private fun checkmissedWordlist() {
        if (!valuesKks.useMissedList) return
/*
        frommissedWordlist = true
        if (isInExactMissedWordlist()) {
            timhandl.postDelayed(runXxCheck, 111)
        } else {
            for (word in missedWordlist) {
                if (wordidx >= wordlist.size) break
                if (sameWord(wordlist[wordidx], word)) {
                    wa = WordArt.InMissedList
                    checkWort(wordlist[wordidx])
                    val idx = missedWordlist.indexOf(word)
                    if (idx > -1) missedWordlist.removeAt(idx)
                    //missedWordlist.remove(word) //removes only first occurence
                    binding.tvAllPartText.append("\n####### done missedWord  $word \n")
                    break
                }
            }
        }
        frommissedWordlist = false

 */
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            //R.id.cbKeyRecoAutoCheck -> startWatch()
            R.id.keypadClear -> doEmptyClick()
            R.id.keypadCheck -> endCheck(false) //= prüfen..
        }
    }
}