package trust.jesus.discover.fragis

import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import trust.jesus.discover.R
import trust.jesus.discover.databinding.FragmentSpeechBinding
import trust.jesus.discover.dlg_data.SuErItem
import trust.jesus.discover.little.recognio.EditWatch
import trust.jesus.discover.little.recognio.Erkenne
import trust.jesus.discover.little.recognio.SuerAdapter
import trust.jesus.discover.little.recognio.Ttsvals
import java.util.Locale


class SpeechFrag : BaseFragment(), View.OnClickListener {

    lateinit var binding: FragmentSpeechBinding // RecognitionCallback
    //private var ttsvals: Ttsvals? = null
    val ttsvals: Ttsvals by lazy { Ttsvals(this) }
    var sueradapter: SuerAdapter? = null
    private var itemClickidx = -1
    private var itemNormalColor = Color.MAGENTA //Black
    private var itemChooseColor = Color.BLUE
    //get() {            TODO()        }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSpeechBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        binding.ybtspeakObt.setOnClickListener(this)
        binding.sbtSpeak.setOnClickListener(this)
        //binding.teilText.setOnClickListener(this)
        //binding.cbautoHideTex.setOnClickListener(this)
        binding.sbtprevdat.setOnClickListener(this)
        binding.sbtrandidat.setOnClickListener(this)
        binding.sbtnextdat.setOnClickListener(this)
        binding.sbtSearch.setOnClickListener(this)
        binding.cbPartWord.setOnClickListener(this)
        binding.cbPartReco.setOnClickListener(this)
        // keypadCheck btsrAdd btsrDelete btsrUp btsrDown
        binding.btsrAdd.setOnClickListener(this)
        binding.btsrDelete.setOnClickListener(this)
        binding.btsrUp.setOnClickListener(this)
        binding.btsrDown.setOnClickListener(this)
        binding.btnRecoObst.setOnClickListener(this)
        binding.cbKeepScreenOn.setOnClickListener(this)


        doListAdapters()
        readUserVals()
        handleItemClick()

        doEdiWatch()
        doSpeechReco()

        setFields()
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        //gc.toast("onResumre")
        setFields()
    }

    private fun doListAdapters() {

        var ari = arrayOf("0","1", "2", "3", "4", "5")
        var adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,ari
        )
        binding.srNextWords.adapter = adapter
        binding.srNextWords.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                gc.appVals().valueWriteInt("srNextWords", position)
                ttsvals.showNextWords = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {  }
        }

        binding.srIgnoreWords.adapter = adapter
        binding.srIgnoreWords.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                gc.appVals().valueWriteInt("srIgnoreWords", position)
                ttsvals.ignoreWords = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {  }
        }

        ari = arrayOf("super easy","1", "2", "3", "4", "5", "6", "7", "8", "9", "hardest")
        adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,ari
        )
        binding.srdifficultyLevel.adapter = adapter
        binding.srdifficultyLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (ttsvals.difficultyLevel != position)
                    gc.appVals().valueWriteInt("SpeechFrag_difficultyLevel", position)

                ttsvals.difficultyLevel = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {  }
        }

        ari = arrayOf("0","1", "2", "3", "4", "5", "6", "7", "8", "9")
        adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,ari
        )
        binding.srAutoNext.adapter = adapter
        binding.srAutoNext.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (ttsvals.xAutoNext != position)
                    gc.appVals().valueWriteInt("SpeechFrag_xAutoNext", position)
                //if (xIgnoreWords != position) dialog.dismiss()

                ttsvals.xAutoNext = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {  }
        }

        //gc.toast("onCreateView....")
        sueradapter = SuerAdapter(requireContext()) //sbtSpeakAndWordInc  binding.ed_SearchR
        binding.lvSeekReplace.adapter = sueradapter

        binding.lvSeekReplace.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                itemClickidx = position
                handleItemClick()
            }
        val typedValue = TypedValue()
        gc.mainActivity!!.theme.resolveAttribute(R.attr.tabSelected_colour, typedValue, true)
        val color = ContextCompat.getColor(requireContext(), typedValue.resourceId)
        itemNormalColor = binding.lvSeekReplace.solidColor
        itemChooseColor = color
        sueradapter?.loadFromFile()
        var i = 0
        val suer = arrayOf<String?>("dass", "das", "daß", "das", " {2}", " ")
        if (sueradapter!!.isEmpty) {
            while (i < 6) {
                val item = SuErItem()
                item.suche = suer[i]
                i++
                item.ersetze = suer[i]
                i++
                sueradapter!!.add(item)
            }
            sueradapter?.savetofile()
            sueradapter!!.notifyDataSetChanged()
        }

    }
    private fun readUserVals() {
        ttsvals.showNextWords = gc.appVals().valueReadInt("srNextWords", 0)
        binding.srNextWords.setSelection(ttsvals.showNextWords)
        ttsvals.ignoreWords = gc.appVals().valueReadInt("srIgnoreWords", 0)
        binding.srIgnoreWords.setSelection(ttsvals.ignoreWords)
        ttsvals.xAutoNext = gc.appVals().valueReadInt("SpeechFrag_xAutoNext", 0)
        binding.srAutoNext.setSelection(ttsvals.xAutoNext)
        ttsvals.difficultyLevel = gc.appVals().valueReadInt("SpeechFrag_difficultyLevel", 0)
        binding.srdifficultyLevel.setSelection(ttsvals.difficultyLevel)


        //checkboxes:
        ttsvals.usePartWord = ttsvals.readAndSetCheckboxVal(binding.cbPartWord)
        ttsvals.usePartReco = ttsvals.readAndSetCheckboxVal(binding.cbPartReco)
        ttsvals.keepScreenOn = ttsvals.readAndSetCheckboxVal(binding.cbKeepScreenOn)


        ttsvals.waitTime = gc.appVals().valueReadInt("srWaitTime", ttsvals.waitTime)
        binding.edWait.setText(ttsvals.waitTime.toString())
        ttsvals.goOnWord = gc.appVals().valueReadString("goOnWord", "further").toString().uppercase( Locale.getDefault())
        binding.edComWeiter.setText(ttsvals.goOnWord)
        ttsvals.retryWord = gc.appVals().valueReadString("retryWord", "retry").toString().uppercase( Locale.getDefault())
        binding.edComRetrey.setText(ttsvals.retryWord)
        ttsvals.doSpeakWord = gc.appVals().valueReadString("doSpeakWord", "speak").toString().uppercase( Locale.getDefault())
        binding.edComdoSpeak.setText(ttsvals.doSpeakWord)

    }

    var speechReco: Erkenne? = null  //by lazy { SpeechReco(this) }SpeechReco

    private fun doSpeechReco() {
        speechReco = Erkenne(this)
        binding.ybtrecord.setOnClickListener(speechReco)
        binding.tvVersIdx.setOnClickListener(speechReco)
        binding.btReciteAgain.setOnClickListener(speechReco)
        binding.btReciteWeiter.setOnClickListener(speechReco)

        binding.edComWeiter.doOnTextChanged { text, start, before, count ->
            if (count > 0) {
                ttsvals.goOnWord = text.toString().uppercase( Locale.getDefault())
                gc.appVals().valueWriteString("goOnWord", ttsvals.goOnWord)
            }
        }

        binding.edComRetrey.doOnTextChanged { text, start, before, count ->
            if (count > 0) {
                ttsvals.retryWord = text.toString().uppercase( Locale.getDefault())
                gc.appVals().valueWriteString("retryWord", ttsvals.retryWord)
            }
        }

        binding.edComdoSpeak.doOnTextChanged { text, start, before, count ->
            if (count > 0) {
                ttsvals.doSpeakWord = text.toString().uppercase( Locale.getDefault())
                gc.appVals().valueWriteString("doSpeakWord", ttsvals.doSpeakWord)
            }
        }
    }

    fun asttsSettingsClick() {
        gc.ttSgl()!!.andoSetttings()

        /* binding.cscroliDedac.fullScroll( FOCUS_DOWN)  //scrollTo(0, binding.flDedacText.height+11) binding.cscroliDedac.scrollBy(0, textView.height)  //scrollTo(0, binding.flDedacText.height+11)
        // binding.cscroliDedac.scrollBy(0, 44) binding.svMain.
        binding.progressBar.visibility = View.GONE //must Gone, not INVISIBLE may stay invisible
        binding.cscroliDedac.visibility = View.GONE //must Gone, not INVISIBLE may stay invisible
//binding.cscroliFlow.visibility = View.INVISIBLE
        //binding.cscroliFlow.layoutParams.height =  1452
        //binding.cscroliDedac.visibility = View.VISIBLE
        //gc.toast("ttsSettings")
        //setFields()
        //binding.cscroliDedac.fullScroll(FOCUS_DOWN)
        //getFragmentManager()?.beginTransaction()?.detach(this)?.attach(this)?.commit()
    //this.requireParentFragment().in */
    }
    private fun speakClick() {
        if (speechReco?.doSpeack() == true) return
        //speechReco ?: {}
        ttsvals.doSpeak = true
        startSpeak()
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
        binding.edWait.doOnTextChanged { text, start, before, count ->
            if (count > 0) {
                ttsvals.waitTime = text.toString().toInt()
                gc.appVals().valueWriteInt("srWaitTime", ttsvals.waitTime)
            }
        }

    }
    private fun btnRandiClick() {
        gc.csvList()!!.getRandomText()
        setFields()
    }

    fun sbtSearchClick() {
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

    private var newLB = 0
    fun setFields() {
        binding.asfLText.removeAllViews()
        gc.setVersTitel(gc.lernItem.vers)
        val txt = gc.lernItem.text
        var wort = ""
        var now = ""
        newLB = 0
        for (i in 0..<txt.length) {
            val c = txt[i]
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) { //(c >= '0' && c <= '9') ||
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
        binding.tvVersIdx.text = gc.lernItem.vers
    }

    private fun addNewLine() {
        if (newLB < 1) return
        val lafl = LayoutInflater.from(binding.asfLText.context).
            inflate(R.layout.tv_speech_nl, binding.asfLText, false) as TextView
        lafl.text = ""
        binding.asfLText.addView(lafl)
        newLB--
        addNewLine()
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
                gc.Logl("c= " + cnt + "  i= " + i + " capi " + v.text, false)
                if (cnt == 1) startidx = i
                if (cnt == 2) {
                    endidx = i
                    cnt = 3
                }
            }
        }
        //ttsvals.toLearnText  saw_cnt=Anzahl wörter for curTTS_SpeakText
        if (endidx > 0) gcc = endidx - startidx + 1 else endidx = gcc
        ttsvals.toLearnText = ""
        ttsvals.toLearnWordCnt = 0
        for (i in startidx..<endidx) {
            val v = binding.asfLText.getChildAt(i) as TextView
            ttsvals.toLearnText += v.text.toString()
            ttsvals.toLearnWordCnt++
        }

        wIdx = startidx + gcc

        ttsvals.saw_cnt.let { if (it > 4) wIdx = startidx + ttsvals.saw_cnt + 1 }
        //if (wIdx>gcc) wIdx=gcc;
        if (wIdx > binding.asfLText.size) wIdx = binding.asfLText.size

        //gc.Logl("saw_cnt "+saw_cnt + "  startidx "+startidx + "  endidx "+endidx + "  wIdx "+wIdx + "  gcc "+gcc , false);
        ttsvals?.toSpeakWordCnt = 0
        ttsvals.curTTS_SpeakText = ""
        for (i in startidx..<wIdx) {
            val v = binding.asfLText.getChildAt(i) as TextView
            ttsvals.curTTS_SpeakText += v.text.toString()
            ttsvals.toSpeakWordCnt++
        }
        // binding.teilText.setChecked(!curTTS_SpeakText.isEmpty());
        if (ttsvals.curTTS_SpeakText.isEmpty()) ttsvals.curTTS_SpeakText =
            gc.lernItem.text else gc.lernItem.partText = ttsvals.curTTS_SpeakText //for later use

        //gc.Logl(curTTS_SpeakText, false);
        ttsvals.toSpeakLen = ttsvals.curTTS_SpeakText.length
    }

    private fun startSpeak(): Boolean {
        getSpeakText()
        if (ttsvals.curTTS_SpeakText.isEmpty()) {
            //Note that Snackbars are preferred for brief messages while the app is in the foreground.
            Toast.makeText(this.requireContext(), getString(R.string.please_enter_some_text), Toast.LENGTH_SHORT)
                .show()
            return false
        } else {
            if (ttsvals.doSpeak) gc.ttSgl()!!.speak(ttsvals.curTTS_SpeakText)
        }
        return true
    }

    fun btDownClick(view: View?) {
        val Clickitem: SuErItem? = sueradapter!!.getItem(itemClickidx)
        val aitem: SuErItem? = sueradapter!!.getItem(itemClickidx + 1)
        sueradapter!!.set(itemClickidx, aitem)
        sueradapter!!.set(itemClickidx + 1, Clickitem)
        itemClickidx++
        ArrowButs()
        sueradapter!!.savetofile()
    }

    fun btUpClick(view: View?) {
        val clickitem: SuErItem? = sueradapter!!.getItem(itemClickidx)
        val aitem: SuErItem? = sueradapter!!.getItem(itemClickidx - 1)
        sueradapter!!.set(itemClickidx, aitem)
        sueradapter!!.set(itemClickidx - 1, clickitem)
        itemClickidx--
        ArrowButs()
        sueradapter!!.savetofile()
    }

    fun btAddClick(view: View?) {
        val item = SuErItem()
        item.suche = binding.edSearchR.text.toString()
        item.ersetze = binding.edReplace.text.toString()
        sueradapter!!.add(item)
        sueradapter!!.savetofile()
    }

    fun btDeleteClick(view: View?) {
        if (itemClickidx < 0 || itemClickidx > sueradapter!!.count - 1) return
        val item: SuErItem? = sueradapter!!.getItem(itemClickidx)
        itemClickidx = -1
        sueradapter!!.remove(item)
        sueradapter!!.notifyDataSetChanged()
        ArrowButs()
    }

    private fun handleItemClick() {
        for (i in 0..<sueradapter!!.count) {
            val item: SuErItem? = sueradapter!!.getItem(i)
            if (item == null) continue
            item.ItemBakColor = itemNormalColor
            if (i == itemClickidx) {
                item.ItemBakColor = itemChooseColor
                binding.edSearchR.setText(item.suche)
                binding.edReplace.setText(item.ersetze)
            }
            //if (!item.ItemBakColor.equals(itemChooseColor))
        }

        sueradapter!!.notifyDataSetChanged()
        ArrowButs()
    }

    private fun ArrowButs() {
        if (itemClickidx < 0) return
        val item: SuErItem? = sueradapter!!.getItem(itemClickidx)
        if (item == null) return
        if (item.ItemBakColor != itemChooseColor) {
            binding.btsrUp.visibility = View.INVISIBLE
            binding.btsrDown.visibility = View.INVISIBLE
            return
        }
        val count = sueradapter!!.count
        if (itemClickidx < count - 1 && itemClickidx > -1) binding.btsrDown.visibility = View.VISIBLE else binding.btsrDown.visibility =
            View.INVISIBLE
        if (itemClickidx > 0) binding.btsrUp.visibility = View.VISIBLE else binding.btsrUp.visibility =
            View.INVISIBLE
    }


    fun doButtonSheet() {
        val dialog = BottomSheetDialog(requireContext())

        val view = layoutInflater.inflate(R.layout.sheet_speech, null)

        val rbFreeForm = view.findViewById<RadioButton>(R.id.rbFreeForm)
        val rbWebSearch = view.findViewById<RadioButton>(R.id.rbwebSearch)
        val srLanguage = view.findViewById<Spinner>(R.id.srLanguage)

        rbFreeForm.isChecked =
         (gc.appVals().valueReadString("srLangModel", RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                 == RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        rbWebSearch.isChecked = !rbFreeForm.isChecked

        var ari = arrayOf("1", "2", "3", "4", "5", "6", "7")

        ari = arrayOf("System", "Deutsch", "Englisch", "French", "Italian", "CHINESE")
        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,ari
        )
        srLanguage.adapter = adapter
        srLanguage.setSelection(gc.appVals().valueReadInt("srLanguage", 0))


        val btChange = view.findViewById<Button>(R.id.btnChange)
        btChange.setOnClickListener {

            if (rbFreeForm.isChecked)
                gc.appVals().valueWriteString("srLangModel", RecognizerIntent.LANGUAGE_MODEL_FREE_FORM) else
                    gc.appVals().valueWriteString("srLangModel", RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
            //RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH else ttsvals.langModel = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM

            gc.appVals().valueWriteInt("srLanguage", srLanguage.selectedItemPosition)

            dialog.dismiss()
        }
        // dialog.setCancelable(false)

        // set content view to our view.
        dialog.setContentView(view)
        dialog.show()
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ybtspeakObt -> asttsSettingsClick()
            R.id.sbtSpeak -> speakClick()
            R.id.sbtprevdat -> sbtPrevDataClick()
            R.id.sbtrandidat -> btnRandiClick()
            R.id.sbtnextdat -> sbtNextDataClick()
            R.id.sbtSearch -> sbtSearchClick()

            R.id.cbPartWord ->      ttsvals.usePartWord = ttsvals.writeAndGetCheckboxVal(p0 as CheckBox)
            R.id.cbPartReco ->      ttsvals.usePartReco = ttsvals.writeAndGetCheckboxVal(p0 as CheckBox)
            R.id.cbKeepScreenOn ->  ttsvals.keepScreenOn =ttsvals.writeAndGetCheckboxVal(p0 as CheckBox)

            R.id.btsrAdd -> btAddClick(p0)
            R.id.btsrDelete -> btDeleteClick(p0)
            R.id.btsrUp -> btUpClick(p0)
            R.id.btsrDown -> btDownClick(p0)

            R.id.btnRecoObst -> doButtonSheet()

        }
    }



}