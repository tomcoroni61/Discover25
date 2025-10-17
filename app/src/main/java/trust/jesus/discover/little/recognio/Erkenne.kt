package trust.jesus.discover.little.recognio

import android.app.AlertDialog.Builder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.FOCUS_DOWN
import android.view.View.VISIBLE
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import trust.jesus.discover.R
import trust.jesus.discover.dlg_data.SuErItem
import trust.jesus.discover.fragis.SpeechFrag

class Erkenne (speechFrag: SpeechFrag) : RecognitionCallback, View.OnClickListener {


    private val mSpeechFrag=speechFrag
    private var wordidx = 0
    private var resultsWordIdx = 0
    private val binding=mSpeechFrag.binding
    private val ttsvals=mSpeechFrag.ttsvals
    val wordlist: MutableList<String> = ArrayList()
    private val missedWordlist: MutableList<String> = ArrayList()
    private val missedExactWordlist: MutableList<String> = ArrayList()
    private val recognitionManager: SpToTx by lazy {
        SpToTx(mSpeechFrag.gc.applicationContext,  callback = this)
    }
    private val timhandl = Handler(Looper.getMainLooper())

    private enum class WordArt {RightSpoken, WrongSpoken, PartSpoken, AutoAdd, NextFound, InMissedList}
    private var wa: WordArt = WordArt.RightSpoken


    fun getEndDoneText(level: Int): String {
        var txt = mSpeechFrag.getString(R.string.bad)
        if (level==100) txt = mSpeechFrag.getString(R.string.okay_verry_nice) else
            if (level >= 90) txt = mSpeechFrag.getString(R.string.okay_nice) else
                if (level >= 60) txt = mSpeechFrag.getString(R.string.nice)
        return txt
    }
    // #############    missedExactWordlist     missedWordlist  things  #######################
    private fun addToMissedWordlist(aWord: String) {
        if (!useMissedList ) return
        for (i in wordidx..< wordlist.size) {
            if (wordlist[i] == aWord) {
                missedExactWordlist.add(wordlist[i])
                return
            }
        }
        missedWordlist.add(aWord)
    }
    private fun isInExactMissedWordlist(): Boolean {
        for (word in missedExactWordlist) {
            if (word == wordlist[wordidx]) {
                missedExactWordlist.remove(word)
                wa = WordArt.RightSpoken
                checkWort(wordlist[wordidx])
                return true
            }
        }
        return false
    }
    private var frommissedWordlist = false;     private var useMissedList = false
    private fun checkmissedWordlist() {
        if (!useMissedList) return
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
    }
    private var timeCheckMissedWords = 1000L
    private val runXxCheck: Runnable = Runnable {//DauerCheck alle 7sec
        if (!doingCheckMatch) checkmissedWordlist()
        //if (missedWordlist.count() > 0)
        if (recognitionManager.contiousRecording)
            timhandl.postDelayed(runXxCheck, timeCheckMissedWords)
    }// timhandl.postDelayed(runStartRecognition, 1111)

    // #############  END  missedExactWordlist     missedWordlist  things  END  #######################

    private val runNextWord: Runnable = Runnable {
        val tim = System.currentTimeMillis() - lastFoundsWordTime
        if (tim < waitShowNextWord) {
            timhandl.postDelayed(runNextWord, 1111)
            return@Runnable
        }
        showNextWords()
    }// timhandl.postDelayed(runNextWord, 1111)

    //with all okey
    private fun checkWort(aWord: String): Boolean {
        val guwo = wordlist[wordidx]
        // binding.textView.text = aWord + " =? " + guwo  5 100
        if (guwo == aWord) {
            if (wordidx < wordlist.size-1)
                doFoundWords() //erhöht wordidx
            else {//all okay
                if (wordlist.size > 8)
                    addWort(wordlist[wordlist.size-1])
                val level = 100 - (ttsvals.helpersCnt * 100 / wordlist.size)
                val txt = "accuracy $level %"
                binding.tvParttext.text = txt
                binding.tvNextWord.text = txt
                binding.tvAllPartText.append("Errs = ${ttsvals.helpersCnt}\n\n")
                stopRecognition(7)
                binding.tvStatus.text = getEndDoneText(level)
                //doPopUpTxtClick()
            }
        } else {
            return false
        }
        return true
    }
    private fun doFoundWords() {
        if (wordidx >= wordlist.size) return
        addWort(wordlist[wordidx])
        wordidx++
        lastFoundsWordTime = System.currentTimeMillis()
        timhandl.postDelayed(runNextWord, 1111)
    }
    private var lastFoundsWordTime: Long = 0L;  private var waitShowNextWord = 10000L

    private fun showNextWords() {
        if (ttsvals.showNextWords < 1 || !recognitionManager.contiousRecording
            || wordidx >= wordlist.size) return
        var words = ""
        var cnt = wordidx
        var nw = 0
        while (cnt < wordlist.size && nw < ttsvals.showNextWords ) {
            words += wordlist[cnt] + " "
            cnt++
            nw++
        }
        binding.tvNextWord.text = words
    }
    private fun addWort(wd1: String) {//toColorInt()
        if (!recognitionManager.contiousRecording) return
        var wd = wd1
        var wArt = "sr_wrong_spoken"
        val infla = LayoutInflater.from(binding.flDedacText.context)
        var resId = R.layout.sr_rightspoken
        when (wa)  {
            WordArt.WrongSpoken -> {
                resId = R.layout.sr_wrong_spoken
            }

            WordArt.RightSpoken -> R.layout.sr_rightspoken
            WordArt.InMissedList -> {
                resId = R.layout.sr_inmissedlist
                wArt = "sr_inmissedlist"
            }
            WordArt.PartSpoken -> {
                resId = R.layout.sr_partspoken
                wArt = "sr_partspoken"
            }
            WordArt.NextFound -> {
                resId = R.layout.sr_nextfound
                wArt = "sr_nextfound"
            }

            WordArt.AutoAdd -> {
                resId = R.layout.sr_auto_add
                wArt = "sr_auto_add"
            }

        }
        val textView: TextView =  infla.inflate(resId, binding.flDedacText, false) as TextView
        if (wa != WordArt.RightSpoken)
            binding.tvAllPartText.append("addWord:  $wd  as $wArt\n")
        wd = "$wd "
        textView.text = wd
        //textView.setTextColor(txtColor.value.toInt())
        //setTextSizes(txtSize)
        textView.textSize = 16.toFloat() //appVals().valueReadInt("txt.size", txtSize).toFloat()
        binding.flDedacText.addView(textView)
        //binding.cscroliDedac.scrollBy(0, textView.height)
        Handler(Looper.getMainLooper()).postDelayed(
            {//needs delay after addView
                binding.cscroliDedac.fullScroll( FOCUS_DOWN)  //scrollTo(0, binding.flDedacText.height+11) binding.cscroliDedac.scrollBy(0, textView.height)  //scrollTo(0, binding.flDedacText.height+11)
            },
            135
        )
    //okList.add(wd)
    }
    fun buildWordList() {
        var txt = mSpeechFrag.ttsvals.curTTS_SpeakText
        txt = suerByList(txt)
        txt = mSpeechFrag.gc.formatTextUpper(txt)
        wordlist.clear()
        wordidx = 0
        wordlist.addAll( txt.split(' '))
        for (item in wordlist) {
            if (item.isEmpty()) wordlist.remove(item)
        }
        wordlist.remove("")
    }
    fun suerByList(text: String): String {
        var text = text
        for (i in 0..< mSpeechFrag.sueradapter!!.count) {
            val item: SuErItem? = mSpeechFrag.sueradapter!!.getItem(i)
            if (item == null) continue
            text = text.replace(item.suche!!.toRegex(), item.ersetze!!)
        }
        return text
    }
    private fun doVersIdxClick() {
        if (!binding.cscroliDedac.isVisible) {
            binding.cscroliFlow.visibility = View.INVISIBLE //here can be Gone or INVISIBLE
            binding.rlSearchBar.visibility = View.GONE //must Gone, not INVISIBLE may stay invisible
            binding.cscroliDedac.visibility = VISIBLE
        } else {
            binding.rlSearchBar.visibility = VISIBLE
            binding.cscroliFlow.visibility = VISIBLE
            binding.cscroliDedac.visibility = View.INVISIBLE //not Gone, else other views go upwards
        }
    }

    infix fun View.below(view: View) {
        (this.layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.BELOW, view.id)
    }

    fun doRecordClick() {
        if (recognitionManager.isActivated) {
            mSpeechFrag.binding.textViewi.text = mSpeechFrag.getString(R.string.stop)
            stopRecognition(3)
        }
        else {
            mSpeechFrag.getSpeakText()
            if (mSpeechFrag.ttsvals.curTTS_SpeakText.isEmpty()) {
                mSpeechFrag.gc.globDlg().messageBox(mSpeechFrag.getString(R.string.no_text_to_speak))
                //Toast.makeText(this.requireContext(), mSpeechFrag.getString(R.string.please_enter_some_text), Toast.LENGTH_SHORT)                    .show()
                return
            }
            buildWordList()
            doLearnLevel()
            startRecognition()

        }
    }
    private fun startRecognition() {
        binding.progressBar.isIndeterminate = false
        recognitionManager.contiousRecording = true
        binding.textViewi.text = ""
        binding.tvNextWord.text = ""
        binding.tvParttext.text = ""
        binding.flDedacText.removeAllViews()
        binding.tvAllPartText.text = ""
        ttsvals.helpersCnt = 0
        ttsvals.xAutoNextCount = 0
        missedWordlist.clear();         missedExactWordlist.clear()
        wordidx = 0
        //binding.cscroliFlow.visibility = View.INVISIBLE //here INVISIBLE may stay invisible
        binding.cscroliDedac.visibility = VISIBLE
        binding.textViewi.visibility = VISIBLE
        //binding.cscroliFlow.below()
        //mSpeechFrag.setFields()
        binding.rlSearchBar.visibility = View.GONE //must Gone, not INVISIBLE may stay invisible
        binding.cscroliFlow.visibility = View.INVISIBLE //here INVISIBLE may stay invisible
        binding.progressBar.visibility = VISIBLE //must Gone, not INVISIBLE may stay invisible
        binding.llCommands.visibility = VISIBLE //must Gone, not INVISIBLE may stay invisible
        binding.llDifficulty.visibility = View.GONE


        recognitionManager.startRecognition()
        mSpeechFrag.gc.doKeepScreenOn(binding.cbKeepScreenOn.isChecked)
        timhandl.postDelayed(runNextWord, 1111)
        timhandl.postDelayed(runXxCheck, 11111)
    }

    private fun stopRecognition(delaySec: Long) {
        recognitionManager.contiousRecording = false
        recognitionManager.stopRecognition()
        //binding.rlSearchBar.visibility = VISIBLE
        mSpeechFrag.binding.progressBar.isIndeterminate = true
        binding.progressBar.visibility = View.GONE //must Gone, not INVISIBLE may stay invisible
        //binding.cscroliDedac.visibility = View.INVISIBLE //must INVISIBLE may stay invisible
        mSpeechFrag.gc.doKeepScreenOn(false)

        mSpeechFrag.binding.tvParttext.text = ""
        mSpeechFrag.binding.tvStatus.text = "record stopped"
        //binding.cscroliFlow.visibility = VISIBLE
        missedWordlist.clear()
        binding.llDifficulty.visibility = VISIBLE

        Handler(Looper.getMainLooper()).postDelayed(
            {
                binding.rlSearchBar.visibility = VISIBLE
                binding.cscroliFlow.visibility = VISIBLE
                binding.cscroliDedac.visibility = View.INVISIBLE //must INVISIBLE may stay invisible
                binding.progressBar.visibility = View.GONE //must Gone, not INVISIBLE may stay invisible
                binding.llCommands.visibility = View.GONE //must Gone, not INVISIBLE may stay invisible
            },
            delaySec * 1000
        )

    }
    fun doLearnLevel() {
        mSpeechFrag.gc.appVals().valueWriteInt("srMaxResults", 1)
        ttsvals.usePartWord = false;            ttsvals.usePartReco = false
        ttsvals.showNextWords = 0;          ttsvals.ignoreWords = 0
        useMissedList = true;               timeCheckMissedWords = 5000
        waitShowNextWord = 11000

        when (ttsvals.difficultyLevel) {
            0 -> { //easiest Level
                ttsvals.showNextWords = 5;  waitShowNextWord = 500L
                ttsvals.ignoreWords = 7;    timeCheckMissedWords = 600
                ttsvals.xAutoNext = 1
                ttsvals.usePartWord = true
                ttsvals.partWordProzent = 25
                ttsvals.partWordFoundProzent = 25
                //mSpeechFrag.gc.appVals().valueWriteInt("srMaxResults", 3)
            }
            1 -> {
                ttsvals.showNextWords = 4;  waitShowNextWord = 3000
                ttsvals.ignoreWords = 6;    timeCheckMissedWords = 800
                ttsvals.xAutoNext = 1
                ttsvals.usePartWord = true
                ttsvals.partWordProzent = 35
                ttsvals.partWordFoundProzent = 35
                //mSpeechFrag.gc.appVals().valueWriteInt("srMaxResults", 2)
            }
            2 -> {
                ttsvals.showNextWords = 3;  waitShowNextWord = 6000
                ttsvals.ignoreWords = 3;    timeCheckMissedWords = 1300
                ttsvals.xAutoNext = 2
                ttsvals.usePartWord = true
                ttsvals.partWordProzent = 60
                ttsvals.partWordFoundProzent = 65
                //mSpeechFrag.gc.appVals().valueWriteInt("srMaxResults", 2)
            }
            3 -> {
                ttsvals.showNextWords = 3;  waitShowNextWord = 9000
                ttsvals.ignoreWords = 2;    timeCheckMissedWords = 2000
                ttsvals.xAutoNext = 3
                ttsvals.usePartWord = true
                ttsvals.partWordProzent = 65
                ttsvals.partWordFoundProzent = 60
                //mSpeechFrag.gc.appVals().valueWriteInt("srMaxResults", 2)
            }
            4 -> {
                ttsvals.showNextWords = 2;  timeCheckMissedWords = 6000
                ttsvals.ignoreWords = 2
                ttsvals.xAutoNext = 4
                ttsvals.usePartWord = true
                ttsvals.partWordProzent = 75
                ttsvals.partWordFoundProzent = 75
                //mSpeechFrag.gc.appVals().valueWriteInt("srMaxResults", 2)
            }
            5 -> {
                ttsvals.showNextWords = 1
                ttsvals.ignoreWords = 0
                ttsvals.xAutoNext = 5
                ttsvals.usePartWord = true
                ttsvals.partWordProzent = 80
                ttsvals.partWordFoundProzent = 85

            }
            6 -> {
                ttsvals.showNextWords = 0
                ttsvals.ignoreWords = 0
                ttsvals.xAutoNext = 7
                ttsvals.usePartWord = true
                ttsvals.partWordProzent = 85
                ttsvals.partWordFoundProzent = 88

            }
            7 -> {
                ttsvals.xAutoNext = 9

            }
            8 -> {
                ttsvals.xAutoNext = 15
            }
            9 -> {
                ttsvals.xAutoNext = 0
            }
            10 -> {
                ttsvals.xAutoNext = 0
                useMissedList = false

            }
        }
    }

    private val runSpeakCheck: Runnable = Runnable {
        if ( mSpeechFrag.gc.ttSgl()?.ttobj?.isSpeaking == true )
            timhandl.postDelayed(runSpeakCheck, 2111) else
            recognitionManager.startRecognition()
    }// timhandl.postDelayed(runStartRecognition, 1111)

    fun doSpeack(): Boolean {
        //mSpeechFrag.gc.log("doSpeack")

        if (recognitionManager.isActivated) {
            recognitionManager.stopRecognition()
            var startidx = wordidx - 4
            if (startidx < 0) startidx = 0
            var cnt = startidx + 8
            if (cnt > wordlist.size) cnt = wordlist.size
            val txt = wordlist.subList(startidx, cnt).joinToString(separator = " ")
            mSpeechFrag.gc.ttSgl()?.speak(txt.lowercase())
            timhandl.postDelayed(runSpeakCheck, 3111)

            return true
        } else {
            return false
        }

    }

    fun sameWord(word1: String, word2: String): Boolean {
        if (word1.isEmpty() || word2.isEmpty()) return false
        if (word1 == word2) {
            wa = WordArt.RightSpoken
            return true
        }//math.abs
        //mSpeechFrag.gc.log( "sameWord: $word1  $word2  ${ttsvals.usePartWord}\n\n" )
        if ( !ttsvals.usePartWord ) return false

        var sword = ""
        var lword = ""
        if (word1.length < word2.length) {
            sword = word1//dl = word2.length-word1.length
            lword = word2
        }
        else {
            lword = word1
            sword = word2
        }
        val wpa =  (sword.length * 100 / lword.length)
        if (wpa > ttsvals.partWordProzent) {
            var okicnt = 0
            for (char in sword) {
                val idx = lword.indexOf(char)
                if (idx > -1) {
                    lword = lword.removeRange(idx, idx + 1)
                    okicnt++
                    //binding.tvAllPartText.append("\npartWord: $sword  in $lword  oki: $okicnt\n\n")
                }
            }
            val level = (okicnt * 100 / sword.length)
            //mSpeechFrag.gc.log("partWord: $sword  in $lword  level: $level\n\n")
            /*var cnt = 0
            var cnt2 = 0
            while (cnt < dl) {
                if (word1[cnt] == word2[cnt2]) {
                    okicnt++
                    cnt2++
                } else {
                    cnt2++
                }
                cnt++
            }*/

            if (level > ttsvals.partWordFoundProzent) {
                wa = WordArt.PartSpoken
                binding.tvAllPartText.append("\npartWord: $sword  in $word2 ($word1)  level: $level\n\n")
                return true
            }
        }
        return false
    }


    override fun onPrepared(status: RecognitionStatus) {
        binding.progressBar.max = 10
        when (status) {
            RecognitionStatus.SUCCESS -> {
                //Log.i("Recognition","onPrepared: Success")
                binding.tvStatus.text = "Recognition ready"
            }
            RecognitionStatus.UNAVAILABLE -> {
                //Log.i("Recognition", "onPrepared: Failure or unavailable")
                binding.tvStatus.text = "onPrepared: Failure or unavailable"
                Builder(this.mSpeechFrag.requireContext())
                    .setTitle("Speech Recognizer unavailable")
                    .setMessage("Your device does not support Speech Recognition. Sorry!")
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }

            RecognitionStatus.Err_RECORD_AUDIO_Permission -> binding.textViewi.text = "Err_RECORD_AUDIO_Permission press again"
        }
    }
    override fun onBeginningOfSpeech() {
        binding.progressBar.visibility = View.VISIBLE
    }
    override fun onReadyForSpeech(params: Bundle) {
        binding.tvStatus.text = mSpeechFrag.context?.getString(R.string.do_speak_now)
        binding.progressBar.visibility = View.VISIBLE
    }
    override fun onError(errorCode: Int) {
        val errorMessage = recognitionManager.getErrorText(errorCode)
        binding.tvStatus.text = errorMessage
        binding.progressBar.visibility = View.GONE //must Gone, not INVISIBLE may stay invisible
        if (errorCode == recognitionManager.errSpeachTimeout) {
            stopRecognition(1)
        }
    }
    override fun onRmsChanged(rmsdB: Float) {
        binding.progressBar.progress = rmsdB.toInt()
    }

    override fun onPartialResults(results: List<String>) {
        if (!ttsvals.usePartReco) return
        resultsWordIdx = wordidx
        val text ="PR: " + results.joinToString(separator = "\n")
        binding.tvParttext.text = text
        checkMatch(results)
    }
    override fun onResults(  results: List<String>,  scores: FloatArray?  ) {
        binding.tvStatus.text = "wait!!"
        binding.progressBar.visibility = View.GONE //must Gone, not INVISIBLE may stay invisible
        resultsWordIdx = wordidx
        val text = results.joinToString(separator = "\n")
        mSpeechFrag.binding.tvParttext.text = text //Mehrfacherkennung
        checkMatch(results)
    }


    private var doingCheckMatch = false
    private fun checkMatch(matches: List<String>) {
        doingCheckMatch = true
        for (match in matches) {
            val line = suerByList(match ).uppercase() + " "
            binding.tvAllPartText.append("\n~~~~~~~~\n")
            binding.tvAllPartText.append("checkMatchWords: $line\n")
            val list = line.split(' ')
            for (ls in list)
                recoCheckWort(ls.trim())
        }
        doingCheckMatch = false
    }

    private fun recoCheckWort(aWord: String): Boolean {//return bool not used
        if (aWord.isEmpty()) return false
        //val aWord = aWordi//suerByList(aWordi)
        binding.tvAllPartText.append("recoCheckOneWort $aWord with ${wordlist[wordidx]} \n")
        wa = WordArt.WrongSpoken
        var oki=false
        if (sameWord(wordlist[wordidx], aWord)) {
            //wa = WordArt.RightSpoken
            checkWort(wordlist[wordidx])
            oki = true
        }
        /*if (!oki && useMissedList && isInmissedWordlist(wordlist[wordidx])) {
            wa = WordArt.InMissedList
            checkWort(wordlist[wordidx])
            oki = true
        }*/
        if (oki) return true

        if (ttsvals.ignoreWords > 0) {
            //binding.tvAllPartText.append(aWord + " recow??\n")
            var cnt = 1
            if (wordidx + cnt == wordlist.size) {
                //ttsvals.helpersCnt++
                wa = WordArt.NextFound
                binding.tvAllPartText.append("\nErr? ignoreWords last word  $aWord =? ${wordlist[wordidx]}  Errs = ${ttsvals.helpersCnt}\n")
                checkWort(wordlist[wordidx])
                return true
            }
            while (cnt <= ttsvals.ignoreWords && wordidx + cnt < wordlist.size) {
                if (sameWord(wordlist[wordidx + cnt], aWord)) {
                    binding.tvAllPartText.append("\nErr? ignoreWords found  $aWord =? ${wordlist[wordidx + cnt]}  Errs = ${ttsvals.helpersCnt}\n")
                    wa = WordArt.NextFound
                    checkWort(wordlist[wordidx])
                    if (!frommissedWordlist) {
                        addToMissedWordlist(aWord)
                        binding.tvAllPartText.append("ignoreWords add:  $aWord  to missedWordlist\n")
                    } //neeed for next word
                    //ttsvals.helpersCnt++ no error..
                    return true
                }
                cnt++
            }
        }

        if (ttsvals.xAutoNext > 0 && resultsWordIdx == wordidx) {
            ttsvals.xAutoNextCount++
            if (ttsvals.xAutoNextCount > ttsvals.xAutoNext) {
                ttsvals.xAutoNextCount = 0
                wa = WordArt.AutoAdd
                ttsvals.helpersCnt++
                checkWort(wordlist[wordidx])
                //txt =  "xAutoNext  $aWord xAutoNext\n"
                //binding.tvGucks.text = txt
                return true
            }
        }

        if (hasCommand(aWord)) return true

        if (!frommissedWordlist) addToMissedWordlist(aWord)
        //done on start timhandl.postDelayed(runXxCheck, 1111)
        return false
    }

    fun goNextWord() {
        wa = WordArt.WrongSpoken
        ttsvals.helpersCnt++
        binding.tvAllPartText.append("\nErr goNextWord:  ${wordlist[wordidx]}  \n")
        checkWort(wordlist[wordidx])
    }
    fun reciteAgain() {
        doRecordClick()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                doRecordClick()
            },
            5635
        )
    }
    fun hasCommand(command: String): Boolean {
        when (command) {//
            ttsvals.goOnWord -> {
                //binding.tvAllPartText.append("\nhasCommand = $command\n\n")
                goNextWord()
                return true
            }
            ttsvals.retryWord -> {
                reciteAgain()
                return true
            }
            ttsvals.doSpeakWord -> {
                doSpeack()
            }
        }
        return false
    }
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ybtrecord -> doRecordClick()
            R.id.tvVersIdx -> doVersIdxClick()
            R.id.btReciteAgain -> reciteAgain()
            R.id.btReciteWeiter -> goNextWord()

        }
    }

}