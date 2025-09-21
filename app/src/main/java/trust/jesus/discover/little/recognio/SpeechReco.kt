package trust.jesus.discover.little.recognio

import android.app.AlertDialog.Builder
import android.os.Bundle
import android.view.View
import trust.jesus.discover.R
import trust.jesus.discover.dlg_data.SuErItem
import trust.jesus.discover.fragis.SpeechFrag

class SpeechReco (speechFrag: SpeechFrag) : RecognitionCallback {
    private val mSpeechFrag=speechFrag
    private var wordidx = 0
    private val binding=mSpeechFrag.binding
    private val ttsvals=mSpeechFrag.ttsvals
    val wordlist: MutableList<String> = ArrayList()
    private val recognitionManager: SpToTx by lazy {
        SpToTx(mSpeechFrag.gc.applicationContext,  callback = this)
    }

    private fun startRecognition() {
        binding.progressBar.isIndeterminate = false
        recognitionManager.contiousRecording = true
        binding.textView.text = ""
        binding.tvNextWord.text = ""
        binding.tvParttext.text = ""
        binding.tvDedacText.text = ""
        binding.tvAllPartText.text = ""
        ttsvals.helpersCnt = 0

        if (ttsvals.getUserCheckboxVal(binding.cbautoHideTex)) {
            binding.cscroliFlow.visibility = View.INVISIBLE  //asfLText
            binding.cscroliDedac.visibility = View.VISIBLE
        }

        if (ttsvals.getUserCheckboxVal(binding.teilText))
            binding.textView.visibility = View.VISIBLE
        recognitionManager.startRecognition()
    }

    private fun stopRecognition() {
        mSpeechFrag.binding.progressBar.isIndeterminate = true
        mSpeechFrag.binding.progressBar.visibility = View.INVISIBLE
        binding.cscroliDedac.visibility = View.INVISIBLE
        recognitionManager.contiousRecording = false
        recognitionManager.stopRecognition()
        mSpeechFrag.binding.textView.text = ""
        mSpeechFrag.binding.tvParttext.text = ""
        mSpeechFrag.binding.tvStatus.text = "record stopped"
        binding.cscroliFlow.visibility = View.VISIBLE
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
    fun doRecordClick() {
        if (recognitionManager.isActivated) {
            stopRecognition()
            mSpeechFrag.binding.textView.text = mSpeechFrag.getString(R.string.stop)
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

    private fun doFoundWords() {
        val txt = binding.textView.text as String + wordlist[wordidx] + " "
        binding.textView.text = txt
        binding.tvDedacText.text = txt
        wordidx++
        if (ttsvals.nextWords < 1) return
        var words = ""
        var cnt = wordidx
        var nw = 0
        while (cnt < wordlist.size && nw < ttsvals.nextWords ) {
            words += wordlist[cnt] + " "
            cnt++
            nw++
        }
        binding.tvNextWord.text = words

    }

    private fun checkWort(aWord: String): Boolean {
        val guwo = wordlist[wordidx]
           // binding.textView.text = aWord + " =? " + guwo  5 100
        if (guwo == aWord) {
            if (wordidx < wordlist.size-1)
                doFoundWords() //erhöht wordidx
            else {//all okay
                binding.textView.text = ttsvals.curTTS_SpeakText
                val txt = "accuracy ${ 100 - (ttsvals.helpersCnt * 100 / wordlist.size) }%"
                binding.tvParttext.text = txt
                binding.tvNextWord.text = txt
                binding.tvAllPartText.append("Errs = ${ttsvals.helpersCnt}\n\n")
                stopRecognition()
                binding.tvStatus.text = mSpeechFrag.getString(R.string.okay_verry_nice)
                //doPopUpTxtClick()
            }
        } else {
            return false
        }
        return true
    }

    fun sameWord(word1: String, word2: String): Boolean {
        if (word1.isEmpty() || word2.isEmpty()) return false
        if (word1 == word2) return true //ttsvals.getUserCheckboxVal(binding.cbPartWord) sometimes wrong?
        if ( !ttsvals.usePartWord ) return false
        if ( word1.indexOf(word2) > -1 ||  word2.indexOf(word1) > -1) {
            ttsvals.helpersCnt++
            binding.tvAllPartText.append("cbPartWord $word1 =? $word2  Errs = ${ttsvals.helpersCnt}\n")
            return true
        }
                  //  ||  word2.indexOf(word1) > -1)

        return false
    }
    private fun recoCheckWort(aWordi: String): Boolean {
        if (aWordi.isEmpty()) return false
        val aWord = aWordi//suerByList(aWordi)
        //binding.tvAllPartText.append("recoCheckWort $aWord ?=? ${wordlist[wordidx]} \n")
        if (sameWord(wordlist[wordidx], aWord)) {
            checkWort(wordlist[wordidx])
            return true
        }
        if (ttsvals.ignoreWords == 0) return false
        //binding.tvAllPartText.append(aWord + " recow??\n")
        var cnt = 1
        if (wordidx+cnt == wordlist.size) {
            binding.tvAllPartText.append("ignoreWords last word  $aWord =? ${wordlist[wordidx]}  Errs = ${ttsvals.helpersCnt}\n")
            checkWort(wordlist[wordidx])
            ttsvals.helpersCnt++
            return true
        }
        while (cnt <= ttsvals.ignoreWords && wordidx+cnt < wordlist.size) {
            if (sameWord(wordlist[wordidx+cnt], aWord)) {
                binding.tvAllPartText.append("ignoreWords  $aWord =? ${wordlist[wordidx+cnt]}  Errs = ${ttsvals.helpersCnt}\n")
                checkWort(wordlist[wordidx])
                ttsvals.helpersCnt++
                return true
            }
            cnt++
        }
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

            RecognitionStatus.Err_RECORD_AUDIO_Permission -> binding.textView.text = "Err_RECORD_AUDIO_Permission press again"
        }
    }

    override fun onBeginningOfSpeech() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onReadyForSpeech(params: Bundle) {
        binding.tvStatus.text = "do speak now !!"
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onRmsChanged(rmsdB: Float) {
        binding.progressBar.progress = rmsdB.toInt()
    }

    override fun onPartialResults(results: List<String>) {
        val text = results.joinToString(separator = "\n")
        binding.tvParttext.text = text
        checkMatch(results)
    }
    /*
        val text = results.joinToString(separator = "\n")
        mSpeechFrag.binding.tvParttext.text = text
        checkMatch(results)

     */

    override fun onResults(  results: List<String>,  scores: FloatArray?  ) {
        val text = results.joinToString(separator = "\n")
        mSpeechFrag.binding.tvParttext.text = text //Mehrfacherkennung
        checkMatch(results)
    }

    override fun onError(errorCode: Int) {
        val errorMessage = recognitionManager.getErrorText(errorCode)
        //Log.i("Recognition","onError: $errorMessage")
        // gc.Logl(errorMessage, false)
        binding.tvStatus.text = errorMessage
        binding.progressBar.visibility = View.INVISIBLE
    }

}