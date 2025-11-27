package trust.jesus.discover.little.recognio

import android.app.AlertDialog.Builder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.widget.RelativeLayout
import android.widget.TextView
import trust.jesus.discover.R
import trust.jesus.discover.dlg_data.SuErItem
import trust.jesus.discover.fragis.SpeechFrag
import androidx.core.view.isVisible

class SpeechReco (speechFrag: SpeechFrag) : RecognitionCallback, View.OnClickListener {
    private val mSpeechFrag=speechFrag
    private var wordidx = 0
    private var resultsWordIdx = 0
    private val binding=mSpeechFrag.binding
    private val ttsvals=mSpeechFrag.ttsvals
    val wordlist: MutableList<String> = ArrayList()
    private val missedWordlist: MutableList<String> = ArrayList()
    private val recognitionManager: SpToTx by lazy {
        SpToTx(mSpeechFrag.gc.applicationContext,  callback = this)
    }
    private val timhandl = Handler(Looper.getMainLooper())

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
        missedWordlist.clear()
        binding.cscroliFlow.visibility = View.INVISIBLE //here INVISIBLE may stay invisible
        binding.cscroliDedac.visibility = VISIBLE
        //seams that Gone sets height to 0
        binding.rlSearchBar.visibility = View.GONE //must Gone, not INVISIBLE may stay invisible
        //binding.rlSearchBar.layoutParams.height = 0

        //if (ttsvals.getUserCheckboxVal(binding.teilText))
        binding.textViewi.visibility = VISIBLE
        recognitionManager.startRecognition()
    }

    private fun stopRecognition() {
        binding.rlSearchBar.visibility = VISIBLE
        mSpeechFrag.binding.progressBar.isIndeterminate = true
        mSpeechFrag.binding.progressBar.visibility = View.GONE //must Gone, not INVISIBLE may stay invisible
        binding.cscroliDedac.visibility = View.GONE //must Gone, not INVISIBLE may stay invisible
        recognitionManager.contiousRecording = false
        recognitionManager.stopRecognition()

        missedWordlist.sort()
        //val text ="\noki Text\n"+ binding.tvDedacText.text + "\n\nmissedWordlist: \n" + missedWordlist.joinToString(separator = "\n")
        //binding.tvAllPartText.append(text)
        //mSpeechFrag.binding.textView.text = ""
        mSpeechFrag.binding.tvParttext.text = ""
        mSpeechFrag.binding.tvStatus.text = "record stopped"
        binding.cscroliFlow.visibility = VISIBLE
        missedWordlist.clear()
        binding.rlSearchBar.visibility = VISIBLE
        binding.rlSearchBar.layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT

    }

    private fun buildWordList() {
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
    private fun suerByList(text: String): String {
        var text = text
        for (i in 0..< mSpeechFrag.sueradapter!!.count) {
            val item: SuErItem? = mSpeechFrag.sueradapter!!.getItem(i)
            if (item == null) continue
            text = text.replace(item.suche!!.toRegex(), item.ersetze!!)
        }
        return text
    }
    fun doRecordClick() {
        if (recognitionManager.isActivated) {

            mSpeechFrag.binding.textViewi.text = mSpeechFrag.getString(R.string.stop)
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    stopRecognition()
                },
                35
            )
        }
        else {
            mSpeechFrag.getSpeakText()
            if (mSpeechFrag.ttsvals.curTTS_SpeakText.isEmpty()) {
                mSpeechFrag.gc.globDlg().messageBox(mSpeechFrag.getString(R.string.no_text_to_speak))
                //Toast.makeText(this.requireContext(), mSpeechFrag.getString(R.string.please_enter_some_text), Toast.LENGTH_SHORT)                    .show()
                return
            }
            buildWordList()
            startRecognition()

        }
    }


    private enum class WordArt {RightSpoken, WrongSpoken, PartSpoken, AutoAdd, NextFound, InMissedList}
    private var wa: WordArt = WordArt.RightSpoken
    private fun addWort(wd1: String) {//toColorInt()
        var wd = wd1
        val infla = LayoutInflater.from(binding.flDedacText.context)

        //var txtColor: Color = Color.Green

        val resId = when (wa)  {
            WordArt.WrongSpoken -> R.layout.sr_wrong_spoken

            WordArt.RightSpoken -> R.layout.sr_rightspoken
            WordArt.InMissedList -> R.layout.sr_inmissedlist
            WordArt.PartSpoken -> R.layout.sr_partspoken
            WordArt.NextFound -> R.layout.sr_nextfound

            WordArt.AutoAdd -> R.layout.sr_auto_add

        }
        val textView: TextView =  infla.inflate(resId, binding.flDedacText, false) as TextView

        wd = "$wd "
        textView.text = wd
        //textView.setTextColor(txtColor.value.toInt())
        //setTextSizes(txtSize)
        textView.textSize = 18.toFloat() //appVals().valueReadInt("txt.size", txtSize).toFloat()
        binding.flDedacText.addView(textView)
        //okList.add(wd)
    }
    private fun doFoundWords() {
        //val txt = binding.tvDedacText.text as String + wordlist[wordidx] + " "
        //binding.textView.text = txt
        //binding.tvDedacText.text = txt
        addWort(wordlist[wordidx])
        wordidx++
        if (ttsvals.showNextWords < 1) return
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

    fun getEndDoneText(level: Int): String {
        var txt = mSpeechFrag.getString(R.string.bad)
        if (level==100) txt = mSpeechFrag.getString(R.string.okay_verry_nice) else
            if (level >= 90) txt = mSpeechFrag.getString(R.string.okay_nice) else
                if (level >= 60) txt = mSpeechFrag.getString(R.string.nice)
        return txt
    }

    //with all okey
    private fun checkWort(aWord: String): Boolean {
        val guwo = wordlist[wordidx]
           // binding.textView.text = aWord + " =? " + guwo  5 100
        if (guwo == aWord) {
            if (wordidx < wordlist.size-1)
                doFoundWords() //erhöht wordidx
            else {//all okay
                //binding.textView.text = ttsvals.curTTS_SpeakText
                val level = 100 - (ttsvals.helpersCnt * 100 / wordlist.size)
                val txt = "accuracy $level %"
                binding.tvParttext.text = txt
                binding.tvNextWord.text = txt
                binding.tvAllPartText.append("Errs = ${ttsvals.helpersCnt}\n\n")
                stopRecognition()
                binding.tvStatus.text = getEndDoneText(level)
                //doPopUpTxtClick()
            }
        } else {
            return false
        }
        return true
    }

    fun sameWord(word1: String, word2: String): Boolean {
        if (word1.isEmpty() || word2.isEmpty()) return false
        if (word1 == word2) {
            wa = WordArt.RightSpoken
            return true
        }//ttsvals.getUserCheckboxVal(binding.cbPartWord) sometimes wrong?
        if ( !ttsvals.usePartWord ) return false
        if ( word1.indexOf(word2) > -1 ||  word2.indexOf(word1) > -1) {
            ttsvals.helpersCnt++
            binding.tvAllPartText.append("cbPartWord $word1 =? $word2  Errs = ${ttsvals.helpersCnt}\n")
            wa = WordArt.PartSpoken
            return true
        }
                  //  ||  word2.indexOf(word1) > -1)

        return false
    }
    private fun isInmissedWordlist(aWord: String): Boolean {
        for (word in missedWordlist) {
            if (word == aWord) {
                missedWordlist.remove(word)
                return true
            }
        }
        return false
    }
    private val runXxCheck: Runnable = object : Runnable {
        //geht nur wenn handy eingeschalten ist.
        override fun run() {
            checkmissedWordlist()
            if (missedWordlist.count() > 0)
                timhandl.postDelayed(runXxCheck, 1111)
        }
    }// timhandl.postDelayed(runStartRecognition, 1111)
        private fun checkmissedWordlist() {
            for (word in missedWordlist) {
                if (wordidx >= wordlist.size) return
                if (sameWord(wordlist[wordidx], word)) {
                    wa = WordArt.NextFound
                    checkWort(wordlist[wordidx])
                    val idx =missedWordlist.indexOf(word)
                    if (idx > -1) missedWordlist.removeAt(idx)
                    //missedWordlist.remove(word) //removes only first occurence
                    binding.tvAllPartText.append("\n####### done missedWord  $word \n")
                    return
                }
            }
        }
        private fun recoCheckWort(aWord: String): Boolean {
        if (aWord.isEmpty()) return false
        //val aWord = aWordi//suerByList(aWordi)
        //binding.tvAllPartText.append("recoCheckWort $aWord ?=? ${wordlist[wordidx]} \n")
        wa = WordArt.WrongSpoken
        var oki=false
        if (sameWord(wordlist[wordidx], aWord)) {
            //wa = WordArt.RightSpoken
            checkWort(wordlist[wordidx])
            oki = true
        }
        if (isInmissedWordlist(wordlist[wordidx])) {
            wa = WordArt.InMissedList
            checkWort(wordlist[wordidx])
            oki = true
        }
        if (oki) return true

        if (ttsvals.ignoreWords > 0) {
            //binding.tvAllPartText.append(aWord + " recow??\n")
            var cnt = 1
            if (wordidx + cnt == wordlist.size) {
                binding.tvAllPartText.append("ignoreWords last word  $aWord =? ${wordlist[wordidx]}  Errs = ${ttsvals.helpersCnt}\n")
                checkWort(wordlist[wordidx])
                ttsvals.helpersCnt++
                return true
            }
            while (cnt <= ttsvals.ignoreWords && wordidx + cnt < wordlist.size) {
                if (sameWord(wordlist[wordidx + cnt], aWord)) {
                    binding.tvAllPartText.append("ignoreWords  $aWord =? ${wordlist[wordidx + cnt]}  Errs = ${ttsvals.helpersCnt}\n")
                    wa = WordArt.NextFound
                    checkWort(wordlist[wordidx])
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
        missedWordlist.add(aWord)
        timhandl.postDelayed(runXxCheck, 1111)
        return false
    }

    private fun checkMatch(matches: List<String>) {
        for (match in matches) {
            val line = suerByList(match ).uppercase().trim()
            binding.tvAllPartText.append("\n$line\n")
            val list = line.split(' ')
            for (ls in list)
                recoCheckWort(ls.trim())
                //if (recoCheckWort(ls.trim())) return
        }
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
        binding.progressBar.visibility = VISIBLE
    }

    override fun onReadyForSpeech(params: Bundle) {
        binding.tvStatus.text = mSpeechFrag.context?.getString(R.string.do_speak_now)
        binding.progressBar.visibility = VISIBLE
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
    /*
        val text = results.joinToString(separator = "\n")
        mSpeechFrag.binding.tvParttext.text = text
        checkMatch(results)

     */

    override fun onResults(  results: List<String>,  scores: FloatArray?  ) {
        binding.tvStatus.text = "wait!!"
        binding.progressBar.visibility = View.GONE //must Gone, not INVISIBLE may stay invisible
        resultsWordIdx = wordidx
        val text = results.joinToString(separator = "\n")
        mSpeechFrag.binding.tvParttext.text = text //Mehrfacherkennung
        checkMatch(results)
    }

    override fun onError(errorCode: Int) {
        val errorMessage = recognitionManager.getErrorText(errorCode)
        //Log.i("Recognition","onError: $errorMessage")
        // gc.Logl(errorMessage, false) errSpeachTimeout
        binding.tvStatus.text = errorMessage
        binding.progressBar.visibility = View.GONE //must Gone, not INVISIBLE may stay invisible
        if (errorCode == recognitionManager.errSpeachTimeout) {
            stopRecognition()
        }
    }

    private fun doVersIdxClick() {
        if (!binding.cscroliDedac.isVisible) {
            binding.cscroliFlow.visibility = View.INVISIBLE //here can be Gone or INVISIBLE
            binding.cscroliDedac.visibility = VISIBLE
        } else {
            binding.cscroliFlow.visibility = VISIBLE
            binding.cscroliDedac.visibility = View.INVISIBLE //not Gone, else other views go upwards
        }
    }
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ybtrecord -> doRecordClick()
            R.id.tvVersIdx -> doVersIdxClick()

        }
    }

}