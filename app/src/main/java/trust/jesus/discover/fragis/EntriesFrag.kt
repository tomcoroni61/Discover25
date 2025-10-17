package trust.jesus.discover.fragis

import android.Manifest
import android.content.Intent
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
    private var listPopupWindow: ListPopupWindow? = null
    private val bereichNames: MutableList<String> = ArrayList()
    private val Timhandl = Handler(Looper.getMainLooper())



    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle? ): View? {
        binding = FragEntrieBinding.inflate(layoutInflater)

        binding.tvBereich.setOnClickListener(this)
        binding.ybtVersions.setOnClickListener(this)
        binding.ybtspeakObt.setOnClickListener(this)
        binding.ybtspeak.setOnClickListener(this)
        binding.ytvText.setOnClickListener(this)
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
        binding.ybcheese.setOnClickListener(this)
        binding.btBible.setOnClickListener(this)
        binding.ybttoLearn.setOnClickListener(this)
        binding.btnAddLearnItem.setOnClickListener(this)

        binding.ytvcurSpruchFile.text = gc.spruchFileName()


        doListPopup()
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
                Timhandl.postDelayed(saveNewName, 1111)
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
            if (txt.length < 3 || bereichNames.size > 1) listPopupWindow!!.show()
        }
    }

    private fun hasBereich(txt: String): Boolean {
        for (s in bereichNames) {
            if (s.contains(txt)) return true
        }
        return false
    }

    private fun doListPopup() {
        bereichNames.clear()
        val cnt = gc.csvList()!!.dataList.size
        if (cnt > 2) for (i in 1..<cnt) {
            val csvData = gc.csvList()!!.dataList[i]
            if (!hasBereich(csvData.bereich)) bereichNames.add(csvData.bereich)
        }
        bereichNames.sort()

        if (listPopupWindow != null) return

        listPopupWindow = ListPopupWindow(
            gc
        )
        listPopupWindow!!.setAdapter(
            ArrayAdapter(
                gc,
                R.layout.lpw_item, bereichNames
            )
        )
        listPopupWindow!!.anchorView = binding.yedBereich
        listPopupWindow!!.width = 300
        listPopupWindow!!.height = 400

        listPopupWindow!!.isModal = false
        listPopupWindow!!.setOnItemClickListener(this) //setOnClickListener


        binding.yedBereich.onFocusChangeListener = OnFocusChangeListener { view: View?, b: Boolean ->
            //if (binding.yedBereich == null) return @setOnFocusChangeListener
            if (view?.isFocused == true) {
                Timhandl.postDelayed(runPopupList, 1111)
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var txt = binding.yedBereich.text.toString() + " " + bereichNames[position]
        txt = txt.replace("  ", " ")
        binding.yedBereich.setText(txt)
        listPopupWindow!!.dismiss()
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

    fun ttsSettingsClick() {
        gc.ttSgl()?.andoSetttings()
    }

    fun ybtdoSpeakClick() {
        gc.ttSgl()?.speak(binding.sedVersTxt.text.toString())
    }

    private fun versDataToListAndFields(csvData: CsvData?) {
        gc.csvList()!!.copyData(csvData!!, curDataItem)
        gc.csvList()!!.dataList.add(curDataidx, csvData)
        setFields()
        doListPopup()
        gc.toast("Data added")
    }

    fun ybtDeleDataClick() {
        gc.csvList()!!.dataList.removeAt(curDataidx)
        doCurDataIdx(true)
        backUpAndSave("del")
    }


    fun ybtAddDataClick() {
        val csvData = CsvData()
        fieldsToItem(csvData) //
        //gc.askDlg("Text doppelt, dennoch hinzufügen?", () -> adl(csvData)); kapput?
        versDataToListAndFields(csvData)
        backUpAndSave("add")
    }

    fun btnAddLearnItemClick() {
        gc.lernItem.bereich = ""
        versDataToListAndFields(gc.lernItem.tocsvData())
        backUpAndSave("addLearn")
    }
    private fun ybtChangeDataClick() {
        fieldsToItem(gc.csvList()!!.dataList[curDataidx] )
        //gc.csvList()!!.copyData(curDataItem, gc.csvList()!!.dataList[curDataidx])
        backUpAndSave("change")
        doListPopup()
    }
    private fun backUpAndSave(backAdd: String) {
        val oldName = gc.spruchFileName().toString()
        val newName = oldName.replace(".csv", "", true) + backAdd + ".csv"
        gc.dateien().backUpPrivateTextFile(oldName,newName)
        gc.csvList()!!.saveToPrivate(gc.spruchFileName(), '#')
        doListPopup()
    }
    fun ybtSearchClick() {
        val idx = gc.csvList()!!.findText(binding.yedSearchTxt.text.toString(), curDataidx + 1)
        if (idx < 0) return
        gc.csvList()!!.copyData(gc.csvList()!!.dataList[idx], curDataItem)
        curDataidx = idx
        setFields()
    }

    //  START         **************  extra save / load / restore / share ....   START
    fun ybtloadOrgDataClick() {
        val fn = getString(R.string.spruch_csv)
        if (!gc.dateien().assetFileToPrivate(fn)) {
            gc.Logl("failed get org", true)
            return
        }
        gc.csvList()!!.readFromPrivate(fn, '#')

        binding.ytvcurSpruchFile.text = fn
        gc.appVals().valueWriteString("eCurDataFile", fn)
    }

    fun ybtshareDataClick() {
        gc.dateien().shareFile(gc.spruchFileName(), requireContext())
    }

    fun ybtSaveAsClick() {
        val fileDlg = FileDlg(
            gc.mainActivity!!,
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
            gc.mainActivity!!,
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

    private val PICKFILE_RESULT_CODE = 123
    fun ybtChooserClickOld() { //import out use share
        if (!gc.checkPermissions(true, Manifest.permission.READ_EXTERNAL_STORAGE)) return
        val intent: Intent?
        //https://developer.android.com/training/data-storage/shared/documents-files?hl=de
        val chooseFile = Intent(Intent.ACTION_GET_CONTENT) //ACTION_OPEN_DOCUMENT
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
        chooseFile.type = "text/plain"
        //ne chooseFile.setType("*/*"); = alle or text/*  = all text like rtf, html jons
        intent = Intent.createChooser(chooseFile, "Choose a file")
        gc.mainActivity!!.startActivityForResult(intent, PICKFILE_RESULT_CODE)
    }

    fun ybtChooserClick() {//import out use share
        if (!gc.checkPermissions(true, Manifest.permission.READ_EXTERNAL_STORAGE)) return
        val intent: Intent?
        //https://developer.android.com/training/data-storage/shared/documents-files?hl=de
        val chooseFile = Intent(Intent.ACTION_GET_CONTENT) //ACTION_OPEN_DOCUMENT
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
        chooseFile.type = "text/plain"
        //ne chooseFile.setType("*/*"); = alle or text/*  = all text like rtf, html jons
        intent = Intent.createChooser(chooseFile, "Choose a file")
        gc.mainActivity!!.startActivityForResult(intent, PICKFILE_RESULT_CODE)
    }

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



    fun ae_doPopUpClick() {
        listPopupWindow!!.show()
    }


    fun fetchBibleVerse(version: String, bookNum: String, chapter: String, vers: String) {
        lifecycleScope.launch {
            try { //Date(),

                gc.bolls()!!.fetchBibleVerse(version, bookNum, chapter, vers)
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
                        }
                    }
            } catch (e: Exception) {
                //showNetworkError(requireContext())
            }
        }
    }

    fun ybtgetVersTextClick() {
        var crashcnt = 0
        try {
            val bvShort = gc.bolls()!!.bibelVersionShort(binding.yedTranslation.text.toString())
            val bs = binding.yedBibelstelle.text.toString()
            crashcnt=2
            binding.sedVersTxt.setText(bvShort)
            val bibelvers = gc.bBlparseBook()!!.parse(bs)//binding.yedBibelstelle.text.toString()
            gc.Logl(bibelvers.toString(), false)
            fetchBibleVerse(bvShort, bibelvers.book.toString(),
                bibelvers.chapter.toString(), bibelvers.startVerse.toString())
        } catch (e: Exception) {
            gc.Logl("MA_Crash Nr: " + crashcnt + " Msg " + e.message, true)
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
            val bvShort = gc.bolls()!!.bibelVersionShort(binding.yedTranslation.text.toString())
            val bs = binding.yedBibelstelle.text.toString()
            crashcnt=2
            //binding.sedVersTxt.setText(bvShort)
            val bibelvers = gc.bBlparseBook()!!.parse(bs)//binding.yedBibelstelle.text.toString()
            gc.Logl(bibelvers.toString(), false)
            gc.lernItem.numVers = bibelvers.startVerse!!
            fetchBibleChapter(bvShort, bibelvers.book.toString(),
                bibelvers.chapter.toString())
        } catch (e: Exception) {
            gc.Logl("MA_Crash Nr: " + crashcnt + " Msg " + e.message, true)
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
                            gc.lernItem.chapter = gc.bolls()?.versArrayToChaptertext(verses).toString()
                            gc.lernItem.vers =
                                gc.bBlparseBook()!!.versShortName(bookNum.toInt(), chapter.toInt(), 1)
                            gc.lernItem.translation = version

                            gc.lernItem.numBook = bookNum.toInt()
                            gc.lernItem.numChapter = chapter.toInt()
                            gc.startBibleActivity(gc.lernItem.numVers)
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
            R.id.tvBereich -> ae_doPopUpClick()
            R.id.ybtVersions -> ybtVersionsClick()
            R.id.ybtspeakObt -> ttsSettingsClick()
            R.id.ybtspeak -> ybtdoSpeakClick()
            R.id.ytvText -> ybtgetVersTextClick()
            R.id.ybtClear -> ybtclearFieldsClick()
            R.id.ybtprevdat -> ybtPrevDataClick()
            R.id.ybtnextdat -> ybtNextDataClick()
            R.id.ybtadd -> ybtAddDataClick()
            R.id.btnAddLearnItem -> btnAddLearnItemClick()
            R.id.ybtdele -> ybtDeleDataClick()
            R.id.ybtchange -> ybtChangeDataClick()
            R.id.ybtsearch -> ybtSearchClick()
            R.id.ybtshareData -> ybtshareDataClick()
            R.id.ybtloadOrg -> ybtloadOrgDataClick()
            R.id.ybtopen -> ybtOpenClick()
            R.id.ybtsaveAs -> ybtSaveAsClick()
            R.id.ybcheese -> ybtChooserClick()
            R.id.btBible -> btBibleClick()
            R.id.ybttoLearn -> ybttoLearnClick()
        }
    }



}