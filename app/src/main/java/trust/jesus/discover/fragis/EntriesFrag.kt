package trust.jesus.discover.fragis

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import trust.jesus.discover.R
import trust.jesus.discover.databinding.FragEntrieBinding
import trust.jesus.discover.dlg_data.BibleVersionsDLG
import trust.jesus.discover.dlg_data.CsvData
import trust.jesus.discover.dlg_data.FileDlg
import java.util.Locale


class EntriesFrag: BaseFragment(), View.OnClickListener, OnItemClickListener {

    private lateinit var binding: FragEntrieBinding
    private val curDataItem= CsvData() //must val!!
    private var curDataidx = 0
    private var waitSec = 10
    private var listBereichPopupWindow: ListPopupWindow? = null
    private val versionsNames: MutableList<String> = ArrayList()
    private var listVersionsPopupWindow: ListPopupWindow? = null
    private val timHandel = Handler(Looper.getMainLooper())



    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle? ): View? {
        binding = FragEntrieBinding.inflate(layoutInflater)

        binding.tvBereich.setOnClickListener(this)
        //binding.ybtVersions.setOnClickListener(this)
        //binding.ybtspeakObt.setOnClickListener(this)
        binding.ybtspeak.setOnClickListener(this)
        //binding.entriesGetVers.setOnClickListener(this)
        binding.ybtClear.setOnClickListener(this)
        binding.ybtprevdat.setOnClickListener(this)
        binding.ybtnextdat.setOnClickListener(this)
        binding.ybtadd.setOnClickListener(this)
        binding.ybtdele.setOnClickListener(this)
        binding.ybtchange.setOnClickListener(this)
        binding.ybtsearch.setOnClickListener(this)
        binding.ybtshareData.setOnClickListener(this)
        binding.ybtloadOrg.setOnClickListener(this)
        binding.ybtopen.setOnClickListener(this)
        binding.ybtsaveAs.setOnClickListener(this)
        //binding.ybcheese.setOnClickListener(this)
        //binding.btBible.setOnClickListener(this)
        binding.ybttoLearn.setOnClickListener(this)
        binding.btnAddLearnItem.setOnClickListener(this)

        binding.ytvcurSpruchFile.text = gc.spruchFileName()


        doThemesListPopup();
        //doVersionsListPopup()
        loadData()


        return binding.root
    }
    override fun onResume() {
        super.onResume()
        doSharedText()
    }

    private fun doSharedText() {
        if (gc.sharedText == null || gc.sharedText!!.length < 5) return
        ybtclearFieldsClick()
        binding.sedVersTxt.setText(gc.sharedText)
        ybtAddDataClick()
        gc.sharedText = null
    }


    private val saveNewName: Runnable = object : Runnable {
        //geht nur wenn handy eingeschalten ist.
        override fun run() {
            waitSec--
            if (waitSec > 0) {
                timHandel.postDelayed(saveNewName, 1111)
                return
            }
            val fn = binding.ytvcurSpruchFile.text.toString()
            gc.appVals().valueWriteString("eCurDataFile", fn)
            gc.csvList()!!.saveToPrivate(fn, '#')
            gc.toast("Saved to: $fn")
        }
    }
    private val runPopupList: Runnable = object : Runnable {
        //geht nur wenn handy eingeschalten ist.
        override fun run() {
            if (binding.yedBereich.selectionEnd - binding.yedBereich.selectionStart > 0) return
            val txt = binding.yedBereich.text.toString()
            //listBereichPopupWindow!!.listView?.adapter?.count themesList.size > 1
            if (txt.length < 3 || gc.csvList()!!.themesList.size > 0) listBereichPopupWindow!!.show()
        }
    }


    private fun doThemesListPopup() {
        if (listBereichPopupWindow != null) return
        binding.yedBereich.onFocusChangeListener = OnFocusChangeListener { view: View?, b: Boolean ->
            //if (binding.yedBereich == null) return @setOnFocusChangeListener
            if (view?.isFocused == true) {
                timHandel.postDelayed(runPopupList, 1111)
            }
        }




        listBereichPopupWindow = ListPopupWindow(requireContext())
        listBereichPopupWindow!!.setAdapter(
            ArrayAdapter(
                requireContext(),//android.R.layout.simple_list_item_2 R.layout.lpw_item
                R.layout.lpw_item,//must be TextView
                gc.csvList()!!.themesList
            )
        )
        listBereichPopupWindow!!.anchorView = binding.yedBereich
        listBereichPopupWindow!!.width = 350
        listBereichPopupWindow!!.height = 400
        val drawable = ContextCompat.getDrawable(this.requireContext(), R.drawable.back_dyn)
        listBereichPopupWindow?.setBackgroundDrawable( drawable)
        //ne listBereichPopupWindow?.background = drawable falschplatz back_dyn richtig
        listBereichPopupWindow!!.isModal = false
        listBereichPopupWindow!!.setOnItemClickListener(this) //setOnClickListener

    }


    private fun doVersionsListPopup() {
        versionsNames.clear()
        val version = binding.yedTranslation.text.toString().trim()
        val cnt = gc.csvList()!!.dataList.size
        if (cnt > 2) for (i in 1..<cnt) {
            val csvData = gc.csvList()!!.dataList[i]
            csvData.translation = csvData.translation.trim()
            if (csvData.translation.length >3 && !versionsNames.contains(csvData.translation) &&
                ( version.length < 3 || !csvData.translation.contains(version)) )
                    versionsNames.add(csvData.translation)
        }
        versionsNames.sort()

        if (listVersionsPopupWindow != null) return

        listVersionsPopupWindow = ListPopupWindow(requireContext() )
        listVersionsPopupWindow!!.setAdapter(
            ArrayAdapter( requireContext(),
                R.layout.lpw_item, versionsNames ) )

        val drawable = ContextCompat.getDrawable(this.requireContext(), R.drawable.back_dyn)
        listVersionsPopupWindow?.setBackgroundDrawable( drawable)
        listVersionsPopupWindow?.anchorView = binding.yedTranslation
        listVersionsPopupWindow!!.width = 350
        listVersionsPopupWindow!!.height = 400

        listVersionsPopupWindow!!.isModal = false
        listVersionsPopupWindow?.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            binding.yedTranslation.setText(versionsNames[position])
            listVersionsPopupWindow!!.dismiss()
        }

        binding.yedTranslation.onFocusChangeListener = OnFocusChangeListener { view: View?, b: Boolean ->
            //if (binding.yedBereich == null) return @setOnFocusChangeListener
            if (view?.isFocused == true) {
                timHandel.postDelayed(runVersionPopupList, 1111)
            }
        }
    }
    private val runVersionPopupList: Runnable = object : Runnable {
        override fun run() {
            if (binding.yedTranslation.selectionEnd - binding.yedTranslation.selectionStart > 0) return
            val txt = binding.yedTranslation.text.toString()
            if (txt.length < 3 || versionsNames.size > 1) listVersionsPopupWindow!!.show()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var txt = binding.yedBereich.text.toString() + " " + gc.csvList()?.themesList[position]
        txt = txt.replace("  ", " ")
        binding.yedBereich.setText(txt)
        listBereichPopupWindow!!.dismiss()
    }

    fun ybtclearFieldsClick() {
//eVersText eBibleVers, binding.yedBereich, eTranslation;
        binding.sedVersTxt.setText("")
        binding.yedBibelstelle.setText("")
        binding.yedBereich.setText("")
        binding.yedTranslation.setText("")
    }

    fun ybtPrevDataClick() {
        doCurDataIdx(true)
    }

    fun ybtNextDataClick() {
        doCurDataIdx(false)

        //ttobj.setLanguage(Locale.GERMAN);
        //gc.Logl(curDataItem.Text, true);startActivity(new Intent(android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS), 0);

        //startActivity(Intent("com.android.settings.TTS_SETTINGS"))
    }


    fun ybtdoSpeakClick() {
        gc.ttSgl()?.cleanSpeak(binding.sedVersTxt.text.toString())
    }

    private fun versDataToListAndFields(csvData: CsvData?) {
        gc.csvList()!!.copyData(csvData!!, curDataItem)
        gc.csvList()!!.dataList.add(curDataidx, csvData)
        setFields()
        gc.toast("Data added")
    }

    fun ybtDeleDataClick() {
        gc.csvList()!!.dataList.removeAt(curDataidx)
        doCurDataIdx(true)
        backUpAndSave("del")
    }


    fun ybtAddDataClick() {
        if (gc.csvList()!!.hasDataText(binding.sedVersTxt.text.toString())) {
            gc.globDlg().messageBox("Text already exists", requireContext())
            return
        }
        val csvData = CsvData()
        fieldsToItem(csvData) //
        versDataToListAndFields(csvData)
        backUpAndSave("add")
    }
/*

 */
    fun btnAddLearnItemClick() {
        if (gc.csvList()!!.hasDataText(gc.lernItem.text)) {
            gc.globDlg().messageBox("Text already exists", requireContext())
            return
        }
        gc.lernItem.bereich = ""
            versDataToListAndFields(gc.lernItem.tocsvData())
        backUpAndSave("addLearn")
    }
    private fun ybtChangeDataClick() {
        fieldsToItem(gc.csvList()!!.dataList[curDataidx] )
        //gc.csvList()!!.copyData(curDataItem, gc.csvList()!!.dataList[curDataidx])
        backUpAndSave("change")
        //doVersionsListPopup()
    }
    private fun backUpAndSave(backAdd: String) {
        val oldName = gc.spruchFileName().toString()
        val newName = oldName.replace(".csv", "", true) + backAdd + ".csv"
        gc.dateien().backUpPrivateTextFile(oldName,newName)
        gc.csvList()!!.saveToPrivate(gc.spruchFileName(), '#')
        //doVersionsListPopup()
    }
    fun ybtSearchClick() {
        val idx = gc.csvList()!!.findText(binding.yedSearchTxt.text.toString(), curDataidx + 1)
        if (idx < 0) return
        gc.csvList()!!.copyData(gc.csvList()!!.dataList[idx], curDataItem)
        curDataidx = idx
        setFields()
    }

    //  START         **************  extra save / load / restore / share ....   START
    fun ybtLoadOrgDataClick() {
        val fn = getString(R.string.spruch_csv)
        if (!gc.dateien().assetFileToPrivate(fn)) {
            gc.logl("failed get org", true)
            return
        }
        binding.ytvcurSpruchFile.text = fn
        gc.appVals().valueWriteString("eCurDataFile", fn)
        if (!gc.csvList()!!.readFromPrivate(fn, '#'))
            gc.toast("failed to load: $fn") else//should never be...
                ybtNextDataClick()
    }

    fun ybtShareDataClick() {
        gc.dateien().shareFile(gc.spruchFileName(), requireContext())
    }

    fun ybtSaveAsClick() {
        val fileDlg = FileDlg(requireContext(),
            "FileSave",  //or FileSave or FileSave..  ..= chosenDir with dir | or FileOpen
            ".csv"
        ) { chosenDir: String? ->
            binding.ytvcurSpruchFile.text = chosenDir
            gc.csvList()!!.saveToPrivate(chosenDir, '#')
            gc.appVals().valueWriteString("eCurDataFile", chosenDir)
        }
        //fileDlg.create(); //!
        fileDlg.chooseFileOrDir(gc.filesDir.absolutePath)
    }

    fun ybtOpenClick() {
        val fileDlg = FileDlg(
            requireContext() ,// no: gc.mainActivity!! = no theme...
            "FileOpen",  //or FileSave or FileSave..  ..= chosenDir with dir | or FileOpen
            ".csv"
        ) { chosenDir: String? ->
            binding.ytvcurSpruchFile.text = chosenDir
            gc.csvList()!!.readFromPrivate(chosenDir, '#')
            gc.appVals().valueWriteString("eCurDataFile", chosenDir)
            loadData()
        }
        //fileDlg.create(); //!
        fileDlg.chooseFileOrDir(gc.filesDir.absolutePath)
    }

    /*
        private val PICKFILE_RESULT_CODE = 123

        fun ybtChooserClick() {//import out use share
            if (!gc.checkPermissions(true, Manifest.permission.READ_EXTERNAL_STORAGE)) return
            val intent: Intent?
            //https://developer.android.com/training/data-storage/shared/documents-files?hl=de
            val chooseFile = Intent(Intent.ACTION_GET_CONTENT) //ACTION_OPEN_DOCUMENT
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
            chooseFile.type = "text/plain"

        intent = Intent.createChooser(chooseFile, "Choose a file")
        gc.mainActivity!!.startActivityForResult(intent, PICKFILE_RESULT_CODE)
    }

 */
    //private String displayName = null;


    // **************  extra save / load / restore / share ....   END
    private fun doCurDataIdx(minus: Boolean) {
        curDataidx = gc.csvList()!!.doDataIdx(minus, binding.yedDataIdx.text.toString().toInt())
        gc.csvList()!!.copyData(gc.csvList()!!.dataList[curDataidx], curDataItem)

        setFields()
    }

    private fun setFields() {
        binding.sedVersTxt.setText(curDataItem.Text)
        binding.yedBereich.setText(curDataItem.bereich)
        binding.yedBibelstelle.setText(curDataItem.vers)
        binding.yedTranslation.setText(curDataItem.translation)
        binding.yedDataIdx.setText(String.format(Locale.getDefault(), "%d", curDataidx))
        binding.ytvCount.text = String.format(
            Locale.getDefault(),
            "%d",
            gc.csvList()!!.dataList.size - 1
        )
        binding.ytvDataVers.text = curDataItem.vers
        binding.ytvGlobeVers.text = gc.lernItem.vers
        doVersionsListPopup()
        gc.csvList()?.doThemesList(binding.yedBereich.text.toString())
    }



    private fun fieldsToItem(item: CsvData) {
        item.bereich = binding.yedBereich.text.toString()
        item.vers = binding.yedBibelstelle.text.toString()
        item.translation = binding.yedTranslation.text.toString()
        item.Text = binding.sedVersTxt.text.toString()

        //((EditText)  findViewById(R.id.yedDataIdx) ).setText( String.format(Locale.GERMAN,"%d", curDataidx) );
    }

    private fun loadData() {
        if (gc.csvList()!!.dataList.size > 3) {
            curDataidx = gc.lernDataIdx
            gc.csvList()!!.copyData(gc.csvList()!!.dataList.get(curDataidx), curDataItem)
            //curDataItem = gc.csvList()!!.getLernData(curDataidx)
            setFields()

        }
    }



    fun doPopUpClick() {
        listBereichPopupWindow!!.show()
    }

    /*fun srResi() {
        val respo = "{\"hash\":\"k5790vbscc\",\"disambiguation\":[],\"strongs\":[],\"paging\":[],\"errors\":[],\"error_level\":0,\"results\":[{\"book_id\":45,\"book_name\":\"Romans\",\"book_short\":\"Rom\",\"book_raw\":\"Rom\",\"chapter_verse\":\"2:2 - 5\",\"chapter_verse_raw\":null,\"verse_index\":{\"2\":[2,3,4,5]},\"verses\":{\"kjv\":{\"2\":{\"2\":{\"id\":27965,\"book\":45,\"chapter\":2,\"verse\":2,\"text\":\"But we are sure that the judgment of God is according to truth against them which commit such things.\",\"italics\":\"\",\"claimed\":true},\"3\":{\"id\":27966,\"book\":45,\"chapter\":2,\"verse\":3,\"text\":\"And thinkest thou this, O man, that judgest them which do such things, and doest the same, that thou shalt escape the judgment of God?\",\"italics\":\"\",\"claimed\":true},\"4\":{\"id\":27967,\"book\":45,\"chapter\":2,\"verse\":4,\"text\":\"Or despisest thou the riches of his goodness and forbearance and longsuffering; not knowing that the goodness of God leadeth thee to repentance?\",\"italics\":\"\",\"claimed\":true},\"5\":{\"id\":27968,\"book\":45,\"chapter\":2,\"verse\":5,\"text\":\"But after thy hardness and impenitent heart treasurest up unto thyself wrath against the day of wrath and revelation of the righteous judgment of God;\",\"italics\":\"\",\"claimed\":true}}}},\"verses_count\":4,\"single_verse\":false,\"nav\":{\"prev_book\":\"Acts\",\"next_book\":\"1 Corinthians\",\"next_chapter\":\"Romans 3\",\"ncb_name\":\"Romans\",\"prev_chapter\":\"Romans 1\",\"pcb_name\":\"Romans\",\"cur_chapter\":\"Romans 2\",\"ccb_name\":\"Romans\",\"ncb\":45,\"ncc\":3,\"pcb\":45,\"pcc\":1,\"ccb\":45,\"ccc\":2,\"nb\":46,\"pb\":44}}]}"
        try {
            val verses = gc.bibleSuperSearch()?.parseJsonToBibleVerses(
                "kjv",
                "2", respo
            )
            binding.sedVersTxt.setText(verses?.get(0)?.text)
        }catch (e: Exception) {
            binding.sedVersTxt.setText(e.message)
        }
    }*/
    fun fetchSupiBibleVerse(version: String, bookName: String, chapter: String, versStart: String,
                            versEnd: String) {
        lifecycleScope.launch {
            try { //Date(), // https://api.biblesupersearch.com/api?bible=kjv&reference=Rom%204:1-10
                gc.log("fetchSupiBibleVerse start")
                gc.bibleSuperSearch()!!.fetchBibleVerses( version, bookName, chapter, versStart, versEnd)
                    .collect { result ->
                        if (result.isSuccess) {
                            val collectedBibleVerse = result.getOrNull()
                            if (collectedBibleVerse != null) {
                                // bibleVerse = collectedBibleVerse
                                //val referenceAndVersion = "${bibleVerse!!.reference} (${bibleVerse!!.versionLong})"
                                //verseText.text = bibleVerse!!.text
                                binding.sedVersTxt.setText( collectedBibleVerse.get(0)?.text )
                                //gc.Logl(collectedBibleVerse.text, true)
                            }
                        } else {
                            binding.sedVersTxt.setText(result.exceptionOrNull()?.toString())
                            gc.log("fetchSupiBibleVerse failed " + result.exceptionOrNull()?.toString())
                        }
                    }
            } catch (e: Exception) {
                binding.sedVersTxt.setText(e.message)
            }
        }
    }
    fun fetchBollsBibleVerses(version: String, bookName: String, chapter: String, versStart: String,
                        versEnd: String) {
        if (versEnd.toInt() <= versStart.toInt()) {
            fetchBollsBibleVerse(version, bookName, chapter, versStart)
            return
        }
        lifecycleScope.launch {
            try { //Date(),  bibleSuperSearch

                gc.bolls()!!.fetchBibleChapter(version, bookName, chapter)
                    .collect { result ->
                        if (result.isSuccess) {
                            val collectedBibleVerses = result.getOrNull()
                            if (collectedBibleVerses != null) {
                                var txt = ""
                                for (i in versStart.toInt() .. versEnd.toInt()) {
                                    txt += "$i ${collectedBibleVerses[i-1]?.text}\n"
                                }

                                binding.sedVersTxt.setText( txt )
                                //gc.Logl(collectedBibleVerse.text, true) versStart .. versEnd
                            }
                        }
                    }
            } catch (e: Exception) {
                //showNetworkError(requireContext())
            }
        }
    }
    fun fetchBollsBibleVerse(version: String, bookName: String, chapter: String, vers: String ) {
        lifecycleScope.launch {
            try { //Date(),  bibleSuperSearch

                gc.bolls()!!.fetchBibleVerse(version, bookName, chapter, vers)
                    .collect { result ->
                        if (result.isSuccess) {
                            val collectedBibleVerse = result.getOrNull()
                            if (collectedBibleVerse != null) {
                                // bibleVerse = collectedBibleVerse
                                //val referenceAndVersion = "${bibleVerse!!.reference} (${bibleVerse!!.versionLong})"
                                //verseText.text = bibleVerse!!.text
                                binding.sedVersTxt.setText( collectedBibleVerse.text )
                                //gc.Logl(collectedBibleVerse.text, true)
                            }
                        } else
                            binding.sedVersTxt.setText(result.exceptionOrNull()?.toString())
                    }
            } catch (e: Exception) {
                //showNetworkError(requireContext())
                binding.sedVersTxt.setText(e.message)
            }
        }
    }
    fun ybtGetVersTextClick() {
        var crashcnt = 0
        //srResi()
        try {

            //fetchSupiBibleVerse("aa", "bb")
            binding.sedVersTxt.setText("getting Vers")
            val translationEd = binding.yedTranslation.text.toString()//gc.bolls()!!.bibelVersionShort(binding.yedTranslation.text.toString())
            val bs = binding.yedBibelstelle.text.toString()
            crashcnt=2
            val bibleVerse = gc.bBlparseBook()!!.parseBiblePassage(bs)
            val bookNum = gc.bBlparseBook()!!.bookNumber(bibleVerse.bookName)
            fetchBollsBibleVerses (translationEd, bookNum.toString(), bibleVerse.chapter,
                bibleVerse.startVerse, bibleVerse.endVerse   )
            //gc.Logl(bibelvers.toString(), false) fetchSupiBibleVerse
            //binding.sedVersTxt.setText(bvShort.lowercase() + " " + bibelvers.toString())
            //fetchSupiBibleVerse(bvShort, bs)
               // bibelvers.chapter.toString(), bibelvers.startVerse.toString())
        } catch (e: Exception) {
            gc.logl("MA_Crash Nr: " + crashcnt + " Msg " + e.message, true)
            binding.sedVersTxt.setText(e.message)
        }
    }

    private fun ybttoLearnClick() {
        try {
            gc.lernItem.text = binding.sedVersTxt.text.toString()

            gc.lernItem.vers = binding.yedBibelstelle.text.toString()
            gc.lernItem.translation = binding.yedTranslation.text.toString()

            /*
            val bvShort = gc.bolls()!!.bibelVersionShort(binding.yedTranslation.text.toString())
            val bs = binding.yedBibelstelle.text.toString()
            val bibelvers = gc.parseBook()!!.parse(bs)//binding.yedBibelstelle.text.toString()
            gc.LernItem.BookNum = bookNum.toInt()
            gc.LernItem.ChapterNum = chapter.toInt()*/

        } catch (e: Exception) {
            //gc.Logl("MA_Crash Nr: " + crashcnt + " Msg " + e.message, true)
            binding.sedVersTxt.setText(e.message)
        }
    }

    private fun btBibleClick() {
        var crashcnt = 0
        try {
            val bvShort = gc.bolls()!!.getNearBollsVersion(binding.yedTranslation.text.toString())
            val bs = binding.yedBibelstelle.text.toString()
            crashcnt=2
            //binding.sedVersTxt.setText(bvShort) bibelVersionShort
            val bibelvers = gc.bBlparseBook()!!.parse(bs)//binding.yedBibelstelle.text.toString()
            gc.logl(bibelvers.toString(), false)
            gc.lernItem.numVersStart = bibelvers.startVerse
            fetchBibleChapter(bvShort, bibelvers.bookNumber.toString(),
                bibelvers.chapter.toString())
        } catch (e: Exception) {
            gc.logl("MA_Crash Nr: " + crashcnt + " Msg " + e.message, true)
            binding.sedVersTxt.setText(e.message)
        }
    }

    fun fetchBibleChapter(version: String, bookNum: String, chapter: String) {
        lifecycleScope.launch {
            try { //Date(),
                gc.bolls()!!.fetchBibleChapter(version, bookNum, chapter)
                    .collect { result ->
                        if (result.isSuccess) {
                            val verses = result.getOrNull()  //verses?.forEach { verse ->
                            gc.lernItem.setBollsSearchResult( verses,
                                verses!![0]?.text.toString(), version,1, bookNum.toInt(), chapter.toInt())

                            /*gc.lernItem.vers =
                                gc.bBlparseBook()!!.versShortName(bookNum.toInt(), chapter.toInt(), 1)
                            gc.lernItem.translation = version

                            gc.lernItem.numBook = bookNum.toInt()
                            gc.lernItem.numChapter = chapter.toInt()*/
                            gc.startBibleActivity(gc.lernItem.numVersStart)
                        }
                    }
            } catch (e: Exception) {
                //showNetworkError(requireContext())
            }
        }
    }
    fun ybtVersionsClick() {
        val fileDlg = BibleVersionsDLG(
            gc.mainActivity!!
        ) { chosenDir: String? ->
            binding.yedTranslation.setText(chosenDir)
        }
        fileDlg.showDialog() //!
        //fileDlg.chooseFile_or_Dir(getFilesDir().getAbsolutePath())
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.tvBereich -> doPopUpClick()
            //R.id.ybtspeakObt -> ttsSettingsClick()
            R.id.ybtspeak -> ybtdoSpeakClick()
            R.id.ybtClear -> ybtclearFieldsClick()
            R.id.ybtprevdat -> ybtPrevDataClick()
            R.id.ybtnextdat -> ybtNextDataClick()
            R.id.ybtadd -> ybtAddDataClick()
            R.id.btnAddLearnItem -> btnAddLearnItemClick()
            R.id.ybtdele -> ybtDeleDataClick()
            R.id.ybtchange -> ybtChangeDataClick()
            R.id.ybtsearch -> ybtSearchClick()
            R.id.ybtshareData -> ybtShareDataClick()
            R.id.ybtloadOrg -> ybtLoadOrgDataClick()
            R.id.ybtopen -> ybtOpenClick()
            R.id.ybtsaveAs -> ybtSaveAsClick()
            //R.id.ybcheese -> ybtChooserClick()
            //R.id.entriesGetVers -> ybtGetVersTextClick()
            //R.id.ybtVersions -> ybtVersionsClick()
            //R.id.btBible -> btBibleClick()
            R.id.ybttoLearn -> ybttoLearnClick()
        }
    }



}