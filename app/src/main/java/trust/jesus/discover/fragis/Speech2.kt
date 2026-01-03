package trust.jesus.discover.fragis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.size
import androidx.core.widget.doOnTextChanged
import trust.jesus.discover.R
import trust.jesus.discover.databinding.FragSpeech2Binding
import trust.jesus.discover.little.recognio.EditWatch
import trust.jesus.discover.little.recognio.ValsKs
import trust.jesus.discover.little.recognio.SpeechEx
import kotlin.plus

class Speech2: BaseFragment(), View.OnClickListener {

    lateinit var binding: FragSpeech2Binding
    val valsKs: ValsKs by lazy { ValsKs() }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle? ): View {
        
        binding = FragSpeech2Binding.inflate(inflater, container, false)
        binding.cbKeyRecoAutoCheck.setOnClickListener(this)

        binding.sbtprevdat.setOnClickListener(this)
        binding.sbtrandidat.setOnClickListener(this)
        binding.sbtnextdat.setOnClickListener(this)
        binding.sbtSearch.setOnClickListener(this)

        if (gc.mSpeechEx==null)
            gc.mSpeechEx = SpeechEx(this)

        doListAdapters()
        readUserVals()
        doEdiWatch()
        setFields()

        return binding.root

    }

    override fun onResume() {
        super.onResume()
        //gc.toast("onResumre")
        setFields()
    }


    private fun readUserVals() {
        valsKs.showNextWords = gc.appVals().valueReadInt("skNextWords", 0)
        //binding.srNextWords.setSelection(vallsTts.showNextWords)
        valsKs.ignoreWords = gc.appVals().valueReadInt("skIgnoreWords", 0)
        //binding.srIgnoreWords.setSelection(vallsTts.ignoreWords)
        valsKs.xAutoNext = gc.appVals().valueReadInt("SpeechFrag_xAutoNext", 0)
        //binding.srAutoNext.setSelection(vallsTts.xAutoNext)
        valsKs.difficultyLevel = gc.appVals().valueReadInt("SpeechKd_difficultyLevel", 0)
        binding.srdifficultyLevel.setSelection(valsKs.difficultyLevel)

    }
    private fun doListAdapters() {


        /*
var ari = arrayOf("0","1", "2", "3", "4", "5")
        var adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,ari
        )
        binding.srNextWords.adapter = adapter
        binding.srNextWords.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                gc.appVals().valueWriteInt("srNextWords", position)
                vallsTts.showNextWords = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {  }
        }

        binding.srIgnoreWords.adapter = adapter
        binding.srIgnoreWords.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                gc.appVals().valueWriteInt("srIgnoreWords", position)
                vallsTts.ignoreWords = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {  }
        }

 */
        val ari = arrayOf("super easy", "1", "2", "3", "4", "5", "6", "7", "8", "9", "hardest")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item, ari
        )
        binding.srdifficultyLevel.adapter = adapter
        binding.srdifficultyLevel.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (valsKs.difficultyLevel != position)
                        gc.appVals().valueWriteInt("SpeechKd_difficultyLevel", position)

                    valsKs.difficultyLevel = position
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }
        private var newLB = 0
    private fun setFields() {
        binding.asfLText.removeAllViews()
        gc.lernItem.setVersTitel()
        val txt = gc.lernItem.text
        var wort = ""
        var now = ""
        newLB = 0
        for (i in 0..<txt.length) {
            val c = txt[i]
            if ((c in 'A'..'Z') || (c in 'a'..'z')) { //(c >= '0' && c <= '9') ||
                if (!now.isEmpty()) {
                    addTxt(wort + now)
                    now = ""
                    wort = ""
                }
                wort += c
            } else when (c) {
                'ö', 'Ö', 'ä', 'Ä', 'ü', 'Ü', 'ß' -> {
                    if (!now.isEmpty()) {
                        addTxt(wort + now)
                        now = ""
                        wort = ""
                    }
                    wort += c
                }
                '\n' -> newLB++
                else -> now += c

            }
        }
        addTxt(wort + now)
        //binding.tvVersIdx.text = gc.lernItem.vers
    }

    private fun addTxt(tx: String) {
        if (tx.isEmpty()) return
        tx.indexOf('\n')
        var txt = tx
        val lafl = LayoutInflater.from(binding.asfLText.context)
        val textView: TextView = lafl.inflate(R.layout.tv_speech, binding.asfLText, false) as TextView

        textView.text = txt
        //textView.setBackgroundResource(R.drawable.rounded_corner);
        textView.setOnClickListener { view: View? -> this.wordClick(view) }
        binding.asfLText.addView(textView)
        addNewLine()
    } // extends SpeechRecognizer f:layout_newLine="true"

    private fun addNewLine() {
        if (newLB < 1) return
        val lafl = LayoutInflater.from(binding.asfLText.context).
        inflate(R.layout.tv_speech_nl, binding.asfLText, false) as TextView
        lafl.text = ""
        binding.asfLText.addView(lafl)
        newLB--
        addNewLine()
    }

    private fun wordClick(view: View?) {
        val textView = view as TextView
        if (textView.background == null) {
            var cnt = 0
            for (i in 0..<binding.asfLText.size) {
                val v = binding.asfLText.getChildAt(i)
                if (v.background != null) {
                    cnt++
                    if (cnt > 1) v.background = null
                }
            }
            textView.setBackgroundResource(R.drawable.speech_sel)
        } else textView.background = null
    }


    private var editWatch: EditWatch? = null
    
    private fun doEdiWatch() {
        editWatch = EditWatch(this)
        binding.cbKeyRecoAutoCheck.setOnClickListener(editWatch)
        binding.keypadClear.setOnClickListener(editWatch)
        binding.keypadCheck.setOnClickListener(editWatch)
        //binding.keypadText.addTextChangedListener(editWatch!!.textWatcher) doLearnLevel()
        binding.keypadText.doOnTextChanged { text, start, before, count ->
            editWatch!!.handleTextChanged()
        }
        binding.keypadText.onFocusChangeListener = OnFocusChangeListener { view: View?, b: Boolean ->
            if (binding.keypadText.isFocused) {
                editWatch!!.startWatch()
                binding.cbKeyRecoAutoCheck.isChecked = true
            } else binding.cbKeyRecoAutoCheck.isChecked = false
        }


    }
    val wordlist: MutableList<String> = ArrayList()
    private var wordidx = 0 //todo?
    fun buildWordList() {
        var txt = valsKs.curTtsSpeakText
        txt = gc.mSpeechEx!!.suerByList(txt)
        txt = gc.formatTextUpper(txt)
        wordlist.clear()
        wordidx = 0
        wordlist.addAll( txt.split(' '))
        for (item in wordlist) {
            if (item.isEmpty()) wordlist.remove(item)
        }
        wordlist.remove("")
    }
    fun getSpeakText() {
        var cnt = 0
        var gcc = binding.asfLText.size
        var startidx = 0
        var endidx = 0
        var wIdx = 0
        //see if user has text marked
        for (i in 0..<gcc) {
            val v = binding.asfLText.getChildAt(i) as TextView

            if (v.background != null) {
                cnt++
                gc.logl("c= " + cnt + "  i= " + i + " capi " + v.text, false)
                if (cnt == 1) startidx = i
                if (cnt == 2) {
                    endidx = i
                    cnt = 3
                }
            }
        }
        //valsKs.toLearnText  saw_cnt=Anzahl wörter for curTTS_SpeakText
        if (endidx > 0) gcc = endidx - startidx + 1 else endidx = gcc
        valsKs.toLearnText = ""
        valsKs.toLearnWordCnt = 0
        for (i in startidx..<endidx) {
            val v = binding.asfLText.getChildAt(i) as TextView
            valsKs.toLearnText += v.text.toString()
            valsKs.toLearnWordCnt++
        }

        wIdx = startidx + gcc

        valsKs.saw_cnt.let { if (it > 4) wIdx = startidx + valsKs.saw_cnt + 1 }
        //if (wIdx>gcc) wIdx=gcc;
        if (wIdx > binding.asfLText.size) wIdx = binding.asfLText.size

        //gc.Logl("saw_cnt "+saw_cnt + "  startidx "+startidx + "  endidx "+endidx + "  wIdx "+wIdx + "  gcc "+gcc , false);
        valsKs.toSpeakWordCnt = 0
        valsKs.curTtsSpeakText = ""
        for (i in startidx..<wIdx) {
            val v = binding.asfLText.getChildAt(i) as TextView
            valsKs.curTtsSpeakText += v.text.toString()
            valsKs.toSpeakWordCnt++
        }
        // binding.teilText.setChecked(!curTTS_SpeakText.isEmpty());
        if (valsKs.curTtsSpeakText.isEmpty()) valsKs.curTtsSpeakText =
            gc.lernItem.text else gc.lernItem.partText = valsKs.curTtsSpeakText //for later use

        //gc.Logl(curTTS_SpeakText, false);
        valsKs.toSpeakLen = valsKs.curTtsSpeakText.length
    }

    private fun btnRandiClick() {
        gc.csvList()!!.getRandomText()
        setFields()
    }

    private fun sbtSearchClick() {
        val idx = gc.csvList()!!.findText(binding.sedSeek.text.toString(), gc.lernDataIdx + 1)
        if (idx < 0) return
        gc.csvList()!!.getLernData(idx)
        setFields()
    }

    fun sbtPrevDataClick() {
        doCurDataIdx(false)
    }

    fun sbtNextDataClick() {
        doCurDataIdx(true)
    }

    private fun doCurDataIdx(minus: Boolean) {
        gc.csvList()!!.doLearnDataIdx(minus)
        setFields()
    }

    fun doLearnLevel() {
        valsKs.usePartWord = false;        valsKs.xAutoNext = 0
        valsKs.showNextWords = 0;          valsKs.ignoreWords = 0
        valsKs.withEndCheck = false;         valsKs.waitTimeShowNextWord = 12 //= 1,2 sec only when showNextWords > 0

        when (valsKs.difficultyLevel) {
            0 -> { //easiest Level
                valsKs.showNextWords = 5;  //waitShowNextWord = 500L
                valsKs.ignoreWords = 4;    //timeCheckMissedWords = 60
                valsKs.xAutoNext = 1;   valsKs.waitTimeShowNextWord = 8
                    valsKs.usePartWord = true
                valsKs.partWordProzent = 25
                valsKs.partWordFoundProzent = 25
                //mSpeechFrag.gc.appVals().valueWriteInt("srMaxResults", 3)
            }
            1 -> {
                valsKs.showNextWords = 4;  //waitShowNextWord = 3000
                valsKs.ignoreWords = 4;    //timeCheckMissedWords = 80
                valsKs.xAutoNext = 1
                valsKs.usePartWord = true
                valsKs.partWordProzent = 35;    valsKs.waitTimeShowNextWord = 13
                    valsKs.partWordFoundProzent = 35
                //mSpeechFrag.gc.appVals().valueWriteInt("srMaxResults", 2)
            }
            2 -> {
                valsKs.showNextWords = 3;  //waitShowNextWord = 6000
                valsKs.ignoreWords = 3;    //timeCheckMissedWords = 130
                valsKs.xAutoNext = 2
                valsKs.usePartWord = true
                valsKs.partWordProzent = 60;    valsKs.waitTimeShowNextWord = 17
                    valsKs.partWordFoundProzent = 65
                //mSpeechFrag.gc.appVals().valueWriteInt("srMaxResults", 2)
            }
            3 -> {
                valsKs.showNextWords = 3;  //waitShowNextWord = 9000
                valsKs.ignoreWords = 2;    //timeCheckMissedWords = 200
                valsKs.xAutoNext = 4
                valsKs.usePartWord = true;  valsKs.waitTimeShowNextWord = 21
                valsKs.partWordProzent = 65
                valsKs.partWordFoundProzent = 60
                //mSpeechFrag.gc.appVals().valueWriteInt("srMaxResults", 2)
            }
            4 -> {
                valsKs.showNextWords = 2;  //timeCheckMissedWords = 600
                valsKs.ignoreWords = 2
                valsKs.xAutoNext = 6
                valsKs.usePartWord = true;  valsKs.waitTimeShowNextWord = 25
                valsKs.partWordProzent = 75
                valsKs.partWordFoundProzent = 75
                //mSpeechFrag.gc.appVals().valueWriteInt("srMaxResults", 2)
            }
            5 -> {
                valsKs.showNextWords = 2;   valsKs.waitTimeShowNextWord = 30
                valsKs.ignoreWords = 1
                valsKs.xAutoNext = 7
                valsKs.usePartWord = true
                valsKs.partWordProzent = 80
                valsKs.partWordFoundProzent = 85

            }
            6 -> {
                valsKs.showNextWords = 1;   valsKs.waitTimeShowNextWord = 35
                valsKs.ignoreWords = 0
                valsKs.xAutoNext = 8;
                valsKs.usePartWord = true
                valsKs.partWordProzent = 85
                valsKs.partWordFoundProzent = 88
                //valsKs.withEndCheck = true
            }
            7 -> {
                valsKs.xAutoNext = 9
                valsKs.withEndCheck = true
            }
            8 -> {
                valsKs.xAutoNext = 11
                valsKs.withEndCheck = true
            }
            9 -> {
                valsKs.xAutoNext = 15
                valsKs.withEndCheck = true
            }
            10 -> {
                valsKs.xAutoNext = 0
                valsKs.withEndCheck = true //useMissedList = false

            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            //R.id.ybtspeakObt -> asttsSettingsClick()
            //R.id.sbtSpeak -> speakClick()
            R.id.sbtprevdat -> sbtPrevDataClick()
            R.id.sbtrandidat -> btnRandiClick()
            R.id.sbtnextdat -> sbtNextDataClick()
            R.id.sbtSearch -> sbtSearchClick()
        }
    }

}