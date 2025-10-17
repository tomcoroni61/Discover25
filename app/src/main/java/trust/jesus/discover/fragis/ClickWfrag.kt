package trust.jesus.discover.fragis

import android.app.AlertDialog.Builder
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.FOCUS_DOWN
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.size
import com.google.android.material.bottomsheet.BottomSheetDialog
import trust.jesus.discover.R
import trust.jesus.discover.databinding.FragmentClickWfragBinding
import trust.jesus.discover.databinding.SheetWordclkBinding
import trust.jesus.discover.little.recognio.RecognitionCallback
import trust.jesus.discover.little.recognio.RecognitionStatus
import trust.jesus.discover.little.recognio.SpToTx
import java.util.Locale


class ClickWfrag : BaseFragment(), View.OnClickListener, RecognitionCallback {

    private lateinit var binding: FragmentClickWfragBinding
    private var wordidx = 0
    private var clickCnt: Int = 0
    private var curText: String? = null
    private val wordlist: MutableList<String> = ArrayList()


    private val recognitionManager: SpToTx by lazy {
        SpToTx(gc,  callback = this)
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle? ): View? {
        // Inflate the binding.acflowLayout for this fragment ybtspeak
        binding = FragmentClickWfragBinding.inflate(layoutInflater)
        // Inflate the binding.acflowLayout for this fragment acheaderTextView
        binding.wtvSpeak.setOnClickListener(this)
        binding.ycbtspeakObt.setOnClickListener(this)
        binding.ybtspeak.setOnClickListener(this)
        binding.actxTextView.setOnClickListener(this)
        binding.tvMischen.setOnClickListener(this)
        binding.acheaderTextView.setOnClickListener(this)
        binding.ybtrecord.setOnClickListener (this)


        binding.progressBar.visibility = View.INVISIBLE
        binding.progressBar.max = 10
        xIgnoreWords = gc.appVals().valueReadInt("ClkWords_NextWords", 2)
        xAutoNext = gc.appVals().valueReadInt("ClkWords_xAutoNext", 5)
        allowpartWord = gc.appVals().valueReadBool("ClkWords_partWord", true)
        //recognitionManager.createRecognizer()
        loadText()


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadText()
    }
    override fun onDestroy() {
        recognitionManager.destroyRecognizer()
        super.onDestroy()
    }

    override fun onPause() {
        stopRecognition()
        super.onPause()
    }

    private fun startRecognition() {
        binding.progressBar.isIndeterminate = false
        binding.progressBar.visibility = View.VISIBLE
        binding.textView.text = "iniziere"
        recognitionManager.contiousRecording = true
        recognitionManager.startRecognition()
    }

    private fun stopRecognition() {
        binding.progressBar.isIndeterminate = true
        binding.progressBar.visibility = View.INVISIBLE
        recognitionManager.contiousRecording = false
        recognitionManager.stopRecognition()
        binding.textView.text = ""
        binding.tvStatus.text = ""
    }
    private fun doRecordClick() {
        if (recognitionManager.isActivated) {
            stopRecognition()
            binding.textView.text = "stopped"
        }
            else {
                if (wordlist.isNotEmpty() && wordidx > wordlist.size - 3) {
                    curVers = ""
                    loadText()
                }
                startRecognition()
            }
    }

    fun doAgainClick() {
        curVers = ""
        loadText()
    }

    fun doPopUpTxtClick() {
        gc.globDlg().showPopupWin(gc.lernItem.text)
        //gc.showPopupWin(this.binding.actvText, , { this.loadText() })
    }

    fun tvSpeackClick() {
        if (wordidx > 4) {
            val startIndex = wordidx - 4
            var cnt = startIndex + 8
            if (cnt > wordlist.size) cnt = wordlist.size
            val txt = wordlist.subList(startIndex, cnt).joinToString(separator = " ")
            gc.ttSgl()?.speak(txt.lowercase())
        } else
            gc.ttSgl()!!.speak(gc.lernItem.text)
    }

    fun ycbtttsSettingsClick() {
        gc.ttSgl()!!.andoSetttings()
    }

    fun ycbtdoSpeakClick() {
        var tvTxt = binding.actvText.text.toString()
        if (tvTxt.length < 5) return
        val spTxt = gc.lernItem.text
        if (wordidx > wordlist.size-1) return
        val aWord = wordlist[wordidx - 1] //toUpperCase(Locale.GERMANY).
        val wLen = aWord.length
        val pos = spTxt.uppercase(Locale.getDefault()).indexOf(aWord, tvTxt.length - wLen - 2)
        //gc.Logl(tvTxt.length()-wLen-2 + " "+aWord+ "|"+pos + " "+spTxt.toUpperCase(Locale.GERMANY), true);
        if (pos < 5) return
        tvTxt = spTxt.substring(0, pos + wLen)
        //gc.Logl(tvTxt, true);
        gc.ttSgl()!!.speak(tvTxt)
    }

    private var curVers = ""
    private fun loadText() {
        gc.setVersTitel(curVers)
        if (curVers == gc.lernItem.vers) return
        curVers = gc.lernItem.vers
        gc.setVersTitel(curVers)
        var text = gc.lernItem.text
        if (gc.lernItem.partText.length>9)
            text = gc.lernItem.partText
        binding.actvText.text = ""
        wordlist.clear()
        clickCnt = 0
        curText = gc.lernItem.text
        wordidx = -1
        binding.acheaderTextView.text = gc.lernItem.vers
        //binding.vers.setText(gc.LernItem.Vers)
        text = gc.formatTextUpper(text)
        var wort = StringBuilder()
        for (i in 0..<text.length) {
            val c = text[i]
            if (c == ' ') {
                wordlist.add(wort.toString())
                //addWort(" ");
                wort = StringBuilder()
            } else wort.append(c)
        }
        if (wort.isNotEmpty()) {
            wordlist.add(wort.toString()) //addWort(" ");
        }
        //gc.Logl("do gues now", true);
        doGuessWords()
    }

    private fun checkAdd(idi: Int) {
        val aWord = wordlist[idi]
        if (!isInGuesList(aWord)) {
            addWort(aWord)
            //if (!woadi && idx==wordidx) wo
        }
    }

    private fun doGuessWords() {
        if (wordlist.size < 2) return
        binding.acflowLayout.removeAllViews()
        wordidx++
        //boolean woadi=false;
        //addWort(wordlist.get(wordidx));
        var cnt = 99
        while (binding.acflowLayout.size < 3 + random.nextInt(5) && cnt > 0) {
            checkAdd(random.nextInt(wordlist.size - 1))
            cnt--
        }
        checkAdd(wordidx)
    }

    private fun isInGuesList(aWord: String): Boolean {
        for (i in 0..<binding.acflowLayout.size) {
            val v = binding.acflowLayout.getChildAt(i) as TextView
            if (aWord == v.text.toString()) return true
        }
        return false
    }

    private fun addWort(aWord: String?) {
        val textView = LayoutInflater.from(binding.acflowLayout.context)
            .inflate(R.layout.cliword, binding.acflowLayout, false) as TextView
        textView.text = aWord
        textView.gravity = Gravity.CENTER
        // textView.setTag(idx);
        textView.setBackgroundResource(R.drawable.rounded_corner)
        textView.setOnClickListener { view1: View? -> this.wordClick(view1) }
        var id = 0
        val cnt: Int = binding.acflowLayout.size
        if (cnt > 1) id = random.nextInt(cnt - 1)
        binding.acflowLayout.addView(textView, id)
    }

    private fun wordClick(view1: View?) {
        val tv = view1 as TextView

        clickCnt++
        if (!checkWort(tv.text.toString()))
            tv.setBackgroundResource(R.drawable.wrong)
        //binding.vers.setText(gc.LernItem.Vers)
        val guwo = clickCnt.toString() + " - " + wordlist.size //+ "   " + gc.LernItem.Vers;
        binding.acheaderTextView.text = guwo
    }
    private fun checkWort(aWord: String): Boolean {
        val guwo = wordlist[wordidx]
        if (guwo == aWord) {
            xAutoNextCount = 0
            var ct = binding.actvText.text.toString()
            ct = "$ct$guwo "
            binding.actvText.text = ct
            if (wordidx < wordlist.size - 1)
                doGuessWords() //erhöht wordidx
            else {//all okay
                binding.acflowLayout.removeAllViews()
                binding.actvText.text = curText
                stopRecognition()
                //doPopUpTxtClick()
            }
            binding.svRejected.fullScroll( FOCUS_DOWN)
        } else {
            return false
        }
        return true
    }
    private fun recoCheckWort(aWord: String) {
        if (aWord.isEmpty()) return
        if (wordlist[wordidx] == aWord) {
            checkWort(aWord)
            return
        }
        if (allowpartWord ) {
            val word = wordlist[wordidx]
            if (aWord.indexOf(word) >0|| word.indexOf(aWord)>0) {
                checkWort(wordlist[wordidx])
                val txt =  " $aWord <--> $word\n"
                //binding.tvGucks.text = txt
                return
            }
        }

        if (xIgnoreWords > 0) {
            var cnt = 1
            while (cnt <= xIgnoreWords && wordidx + cnt < wordlist.size) {
                if (wordlist[wordidx + cnt] == aWord) {
                    checkWort(wordlist[wordidx])
                    return
                }
                cnt++
            }
        }
        //Color.parseColor("#521B87")
        var txt =  "xAutoNextCount $xAutoNextCount  $aWord \n  xAutoNext $xAutoNext\n"
        //binding.tvGucks.text = txt
        if (xAutoNext == 0) return
        xAutoNextCount++
        if (xAutoNextCount > xAutoNext) {
            xAutoNextCount = 0
            checkWort(wordlist[wordidx])
            txt =  "xAutoNext  $aWord xAutoNext\n"
            //binding.tvGucks.text = txt
            return
        }

    }
    private var xIgnoreWords = 0
    private var xAutoNext = 0
    private var xAutoNextCount = 0
    private var allowpartWord = true
    fun doNewVersClick() {
        gc.csvList()?.getRandomText()
        loadText()
    }



    override fun onPrepared(status: RecognitionStatus) {
        when (status) {
            RecognitionStatus.SUCCESS -> {
                //Log.i("Recognition","onPrepared: Success")
                binding.tvStatus.text = "Recognition ready"
            }
            RecognitionStatus.UNAVAILABLE -> {
                //Log.i("Recognition", "onPrepared: Failure or unavailable")
                binding.tvStatus.text = "onPrepared: Failure or unavailable"
                Builder(this.requireContext())
                    .setTitle("Speech Recognizer unavailable")
                    .setMessage("Your device does not support Speech Recognition. Sorry!")
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }

            RecognitionStatus.Err_RECORD_AUDIO_Permission -> binding.textView.text = "Err_RECORD_AUDIO_Permission press again"
        }
    }

    override fun onBeginningOfSpeech() {
        //binding.textView.text = "onBeginningOfSpeech"
    }


    override fun onReadyForSpeech(params: Bundle) {
        binding.tvStatus.text = "speak now"
    }
/*

*/

    override fun onRmsChanged(rmsdB: Float) {  binding.progressBar.progress = rmsdB.toInt()    }
    private fun checkMatch(matches: List<String>) {
        for (match in matches) {
            val line = match.uppercase().trim()
            val list = line.split(' ')
            for (ls in list)
                recoCheckWort(ls.trim())
        }
    }
    override fun onPartialResults(results: List<String>) {
        if (!allowpartWord) return
        val text = results.joinToString(separator = "\n")
        binding.textView.text = text
        checkMatch(results)
        //gc.Logl(results.toString(), true)
    }

    override fun onResults( results: List<String>,  scores: FloatArray?  ) {
        val text = results.joinToString(separator = "\n")
        //Log.i("Recognition","onResults : $text")
        binding.textView.text = text
        checkMatch(results)
        //gc.Logl(text, true)
    }

    override fun onError(errorCode: Int) {
        val errorMessage = recognitionManager.getErrorText(errorCode)
        //Log.i("Recognition","onError: $errorMessage")
        // gc.Logl(errorMessage, false)
       binding.tvStatus.text = errorMessage
        if (errorCode == recognitionManager.errSpeachTimeout) {
            stopRecognition()
        }
    }

    private lateinit var dlgBinding: SheetWordclkBinding
    fun doButtonSheet() {//binding = FragHomeBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        dlgBinding = SheetWordclkBinding.inflate(inflater)
        dialog.setContentView(dlgBinding.root)
        var ari = arrayOf("0","1", "2", "3", "4", "5")
        var adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,ari
        )
        dlgBinding.srIgnoreWords.adapter = adapter
        dlgBinding.srIgnoreWords.setSelection(xIgnoreWords)
        dlgBinding.srIgnoreWords.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (xIgnoreWords != position)
                    gc.appVals().valueWriteInt("ClkWords_NextWords", position)
                //if (xIgnoreWords != position) dialog.dismiss()

                xIgnoreWords = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {  }
        }

        ari = arrayOf("0","1", "2", "3", "4", "5", "6", "7", "8", "9")
        adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,ari
        )
        dlgBinding.srAutoNext.adapter = adapter
        dlgBinding.srAutoNext.setSelection(xAutoNext)
        dlgBinding.srAutoNext.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (xAutoNext != position)
                    gc.appVals().valueWriteInt("ClkWords_xAutoNext", position)
                //if (xIgnoreWords != position) dialog.dismiss()

                xAutoNext = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {  }
        }


        dlgBinding.partWordSwitch.isChecked = allowpartWord
        dlgBinding.partWordSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (allowpartWord != isChecked)
                gc.appVals().valueWriteBool("ClkWords_partWord", isChecked)
            allowpartWord = isChecked
        }
        dialog.show()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.wtvSpeak -> tvSpeackClick()
            R.id.ycbtspeakObt -> ycbtttsSettingsClick()
            R.id.actxTextView -> doPopUpTxtClick()
            R.id.ybtspeak -> ycbtdoSpeakClick()
            R.id.tvMischen -> doAgainClick()
            R.id.acheaderTextView -> doNewVersClick()
            R.id.ybtrecord -> doRecordClick()

        }
    }

}