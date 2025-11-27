package trust.jesus.discover.fragis

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.size
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import trust.jesus.discover.R
import trust.jesus.discover.bible.BblParseBook.VersStrings
import trust.jesus.discover.databinding.FragmentEntdeckeBinding
import trust.jesus.discover.databinding.SheetDiscoverBinding
import trust.jesus.discover.dlg_data.FileDlg
import trust.jesus.discover.little.FixStuff.Filenames.Companion.seekFileExtn
import trust.jesus.discover.preference.SettingsRand


class EntdeckeFrag : BaseFragment(), View.OnClickListener {

    private val okList: MutableList<String?> = ArrayList()
    //private var chapterVerses: Array<BollsSR?>? = null
    private var showMixed = true
    private var txtSize = 18

    private lateinit var binding: FragmentEntdeckeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,  savedInstanceState: Bundle? ): View? {
    //    paramSearch = savedInstanceState?.getString(Arg_search)
        // gc.Logl("onCreateView  " + (paramSearch!= null), false)

        // Inflate the layout for this fragment
        binding = FragmentEntdeckeBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        val rootView: View = binding.getRoot()
        binding.wtvSpeak.setOnClickListener(this)
        binding.btnRandomvers.setOnClickListener(this)
        binding.ayheaderTextView.setOnClickListener(this)
        binding.etvTxtShow.setOnClickListener(this)
        binding.btnRandObst.setOnClickListener(this)
        binding.btnBible.setOnClickListener(this)
        binding.btnseekplay.setOnClickListener(this)
        binding.tvVerstext.setOnClickListener(this)
        binding.tvMischen.setOnClickListener(this)
        binding.btnseekrund.setOnClickListener(this)
        binding.btnSeekFileDlg.setOnClickListener(this)


        showMixed = gc.appVals().valueReadBool("discover_showMixed", false)
        txtSize = gc.appVals().valueReadInt("txt.size", 18)
        checkSeekList()
        textToFlowlayout(gc.lernItem.text, showMixed)
        return rootView
    }

    private var lastText = ""
    override fun onResume() {
        super.onResume()
        checkSeekList()
        if (lastText != gc.lernItem.text)
            textToFlowlayout(gc.lernItem.text, showMixed)
        //lastText = gc.lernItem.text
        //gc.Logl("onResume  " + (paramSearch!= null), false)
    }

    private fun addWort(wd1: String?, idx: Int, asMixed : Boolean) {
        var wd = wd1
        if (wd == null) return
        val infla = LayoutInflater.from(binding.flowLayout.context)
        val textView: TextView = if (asMixed)
            infla.inflate(R.layout.tv_word, binding.flowLayout, false) as TextView
        else
            infla.inflate(R.layout.tv_word_n, binding.flowLayout, false) as TextView

        textView.tag = idx
        if (asMixed) {
            textView.setBackgroundResource(R.drawable.rounded_corner)
            textView.setOnClickListener { view1: View? -> this.wordClick(view1) }

        } else wd = "$wd "
        textView.text = wd
        //setTextSizes(txtSize)
        textView.textSize = gc.appVals().valueReadInt("txt.size", txtSize).toFloat()
        binding.flowLayout.addView(textView)
        okList.add(wd)
    }
    //private var lastText = ""  || lastText == txt
    fun textToFlowlayout(txt: String?, doMischen: Boolean) {
        if (txt?.isEmpty() == true) return
        lastText = txt.toString()
        binding.flowLayout.removeAllViews()
        val text = txt.toString().trim() //gc.formatTextUpper(txt)
        // gc.Logl(text, false)
        var wort = StringBuilder()
        var cnt = 0
        okList.clear()
        for (i in 0..<text.length) {
            var c = text[i]
            when (c) {
                '!',',', '.', '\n' -> {
                    if (i < text.length - 1 && text[i + 1] != ' ') c = ' '
                }
            }
            if (c == ' ') {
                addWort(wort.toString(), cnt, doMischen)
                //addWort(" ");
                wort = StringBuilder()
                cnt++
            } else wort.append(c)
        }
        if (wort.isNotEmpty()) {
            addWort(wort.toString(), cnt, doMischen) //addWort(" ");
        }
        binding.ayheaderTextView.text = gc.lernItem.vers
        if (doMischen) shuffleViews()
    }

    fun setTextSizes(size: Int) {
        for (i in 0..<binding.flowLayout.size) {
            val v: TextView = binding.flowLayout.getChildAt(i) as TextView
            v.textSize = size.toFloat()
        }
    }
    fun shuffleViews() {
        val cnt: Int = binding.flowLayout.size
        if (cnt < 5) return
        //gc.Logl("Oki: "+okcnt+ " / "+Cnt, true); binding.flowLayout.size
        for (i in 0..<cnt) {
            val v: View = binding.flowLayout.getChildAt(i)
            v.setBackgroundResource(R.drawable.rounded_corner)
        }

        var mc = 5 + random.nextInt(cnt)
        var idx: Int
        var idx2: Int
        while (mc > 0) {
            idx = random.nextInt(cnt - 1)
            for (i in 0..21) {
                idx2 = random.nextInt(cnt - 1)
                if (idx != idx2 && idx2 < cnt - 2) {
                    val view: View = binding.flowLayout.getChildAt(idx)
                    binding.flowLayout.removeView(view)
                    binding.flowLayout.addView(view, idx2)
                    break
                }
            }
            mc--
        }
        okCheck() //if first is right..
    }

    private fun okCheck() {
        var okcnt = 0
        val cnt: Int = binding.flowLayout.size
        for (i in 0..<cnt) {
            val v = binding.flowLayout.getChildAt(i) as TextView
            val txt = v.text.toString()
            if (txt == okList[i] && okcnt==i) {
                okcnt++
                v.setBackgroundResource(R.drawable.richtigplaz)
                v.isClickable = false
            } else v.setBackgroundResource(R.drawable.rounded_corner)
        }
        if (okcnt != cnt) {
            return
        }
        textToFlowlayout(lastText, false)
        //binding.ayheaderTextView.text = gc.lernItem.vers

        // gc.showPopupWin(this.binding.root, gc.LernItem.Text, { this.mischViews() })
    }

    private fun wordClick(view1: View?) {
        val tv = view1 as TextView

        val rid = isNext(view1)
        if (rid > -1) {
            binding.flowLayout.removeView(view1)
            binding.flowLayout.addView(view1, rid)
            tv.setBackgroundResource(R.drawable.richtigplaz)
            okCheck()
        } else
            tv.setBackgroundResource(R.drawable.falschplatz)
    }

    private fun isNext(view: View): Int {
        val childCount: Int = binding.flowLayout.size
        var okCnt = -1
        val viewtxt = (view as TextView).text.toString()

        for (i in 0..<childCount) {
            val v = binding.flowLayout.getChildAt(i) as TextView
            val txt = v.text.toString()
            if (txt == okList[i]) okCnt++ else {
                if (viewtxt == okList[i]) return i
                else return -1
            }
        }
        return -1

    }

    private fun getVersion(context : Context) : String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val defaultVersion = "NKJV"//context.getString(R.string.preference_listPreference_bible_version_default_value)
        return preferences.getString("bible_version", defaultVersion) ?: defaultVersion
    }
    private fun getRandRange(context : Context) : String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        var defaultVersion = "all"
        if (preferences.getBoolean("one_book_only", false)) {
            defaultVersion = "Psalm"
            return preferences.getString("rand_bookname", defaultVersion)?: defaultVersion
        } else
            return preferences.getString("rand_range", defaultVersion) ?: defaultVersion
    }

    fun btnRandomversClick() {
        //PreferenceManager.getDefaultSharedPreferences(requireContext())
        val range = getRandRange(requireContext())
        val verse =  gc.bBlparseBook()?.randomBookAndChapter(range )
        binding.tvVerstext.text = "wait"
        binding.ayheaderTextView.text = "??"
        val version = getVersion( requireContext() )
        //gc.log( "btnRandomversClick: $version, $range  book: ${verse?.bookNumber}, chapter: ${verse?.chapter}")
        //fetchBibleVerse("NKJV", "1", "1", "1") //
        fetchBibleChapter(version, verse?.bookNumber.toString(), verse?.chapter.toString() )
    }


    fun fetchBibleChapter(version: String, bookNum: String, chapter: String) {
        lifecycleScope.launch {
            try { //Date(),
                gc.bolls()!!.fetchBibleChapter(version, bookNum, chapter)
                    .collect { result ->
                        if (result.isSuccess) {
                            val verses = result.getOrNull()  //verses?.forEach { verse ->
                            var vers = 1
                            if (verses?.size!! > 1)  vers = random.nextInt(verses.size)
                            val txt = verses[vers]?.text

                            gc.lernItem.setBollsSearchResult( verses,
                                txt.toString(), version,vers+1, bookNum.toInt(), chapter.toInt())
                            gc.lernItem.addToHistory()
                            gc.setVersTitel(gc.lernItem.vers)
                            binding.tvVerstext.text = ""
                            textToFlowlayout(gc.lernItem.text, showMixed)
                            //mischViews()
                        }
                    }
            } catch (e: Exception) {
                //showNetworkError(requireContext())
            }
        }
    }

    private fun btnrandObstClick() {
        gc.activityStart(activity, SettingsRand::class.java)
    }

    private fun btnBibleClick() {
        gc.startBibleActivity()

    }

    fun tvSpeackClick() {
        // gc.ttSgl()?.setLanguageAndVoice(Locale.UK, 0)//GERMAN

        gc.ttSgl()?.speak(gc.lernItem.text)
    }

    fun txtClick() {
        gc.globDlg().showPopupWin(gc.lernItem.text)
    }


    fun checkSeekList() {
        val count = gc.seekList().entries()

        if (count < 3) {
            binding.llseekbar.visibility = View.INVISIBLE
            return
        }
        binding.llseekbar.visibility = View.VISIBLE
        val stat =  gc.seekList().getSuchwort() +
                "   Vc: " + gc.seekList().entries() + " done " + gc.seekList().getProzentReaded() + "% "
        binding.tvverscnt.text = stat
    }

    private fun btnRandSeekClick() {

        if (gc.seekList().entries() < 2) return

        val vers = gc.seekList().getRandomVers()
        gc.lernItem.setSeekVers(vers)
        //gc.log("fetche: " + gc.LernItem.VersNum)
        binding.tvVerstext.text = ""
        binding.ayheaderTextView.text = ""


        gc.lernItem.chapter.clear()
        gc.lernItem.addToHistory()
        gc.setVersTitel(gc.lernItem.vers)
        checkSeekList()
        textToFlowlayout(gc.lernItem.text, showMixed)
        //mischViews()
    }

    fun btnRandSeekAndSpeakClick() {
        btnRandSeekClick()
        tvSpeackClick()
    }
    private fun fetchBibleVerse(vers: VersStrings) {
        lifecycleScope.launch {
            try { //Date(),
                //gc.Logl("fetch start", true) fetchBibleVerse: LUT, revelation, 21:3
                val bookNumber = gc.bBlparseBook()?.bookNumber(vers.bookName)
                gc.bolls()!!.fetchBibleVerse(
                    getVersion( requireContext() ),
                    bookNumber!!.toString(), vers.chapter, vers.startVerse)
                    .collect { result ->
                        if (result.isSuccess) {
                            val verses = result.getOrNull()  //verses?.forEach { verse ->
                            textToFlowlayout(verses!!.text, false)
                            gc.lernItem.text = verses.text
                            gc.lernItem.vers = gc.bBlparseBook()?.versStringsToBibelStelle(vers).toString()

                            //binding.tvVerstext.text = vers
                            //textToFlowlayout(gc.lernItem.text, showMixed)
                            //mischViews() val version = getVersion( requireContext() )
                        } else binding.tvVerstext.text = result.exceptionOrNull()?.message
                    }
            } catch (e: Exception) {
                binding.tvVerstext.text=e.message
            }
        }
    }

    private fun fetchvotd() {
        lifecycleScope.launch {
            try { //Date(),
                //gc.Logl("fetch start", true)
                gc.vtdo()!!.fetchLabsBibleVerse()
                    .collect { result ->
                        if (result.isSuccess) {
                            val verses = result.getOrNull()  //verses?.forEach { verse ->
                            textToFlowlayout(verses, false)
                            //<b>Revelation 2:10</b>
                            val start = verses!!.indexOf("<b>")
                            val end = verses.indexOf("</b>")
                            var vers = "error"
                            if (start >= 0 && end > 0) {
                                vers = verses.substring(start + 3, end)
                                val verse =  gc.bBlparseBook()?.parseBibelStelle(vers)
                                fetchBibleVerse(verse!!)
                                return@collect
                            }
                            gc.log("start: $start, end: $end")
                            gc.log(vers)
                            binding.tvVerstext.text = vers
                            //textToFlowlayout(gc.lernItem.text, showMixed)
                            //mischViews() val version = getVersion( requireContext() )
                        }
                    }
            } catch (e: Exception) {
                binding.tvVerstext.text=e.message
            }
        }
    }

    private lateinit var dlgBinding: SheetDiscoverBinding
    fun doButtonSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        dlgBinding = SheetDiscoverBinding.inflate(inflater)

        dlgBinding.switchMix.isChecked = gc.appVals().valueReadBool("discover_showMixed", false)
        //gc.Logl("splitTrack: " + btn.splitTrack + " checked: " + btn.isChecked, true)
        dlgBinding.switchMix.setOnClickListener {
            showMixed = dlgBinding.switchMix.isChecked //!btn.splitTrack
            gc.appVals().valueWriteBool("discover_showMixed", showMixed)
            //btn.isChecked
        }
        // dialog.setCancelable(false)
        dlgBinding.btnDown.setOnClickListener {
            // txtSize = gc.appVals().valueReadInt("txt.size", 18)
            txtSize  --
            if (txtSize < 12) txtSize = 12
            setTextSizes(txtSize)
            gc.appVals().valueWriteInt("txt.size", txtSize)
        }
        dlgBinding.btnUp.setOnClickListener {
            txtSize ++
            if (txtSize > 24) txtSize = 24
            setTextSizes(txtSize)
            gc.appVals().valueWriteInt("txt.size", txtSize)
        }
        dlgBinding.btnVersOfDay.setOnClickListener {
            fetchvotd()

        }
        // set content view to our view.
        dialog.setContentView(dlgBinding.root)
        dialog.show()
    }
    fun mischViews() {textToFlowlayout(gc.lernItem.text, true)}
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_randomvers ->  btnRandomversClick()
            R.id.ayheaderTextView -> btnRandomversClick()
            R.id.tvMischen -> mischViews()
            R.id.tvMoves -> doButtonSheet()
            R.id.etvTxtShow -> txtClick()

            R.id.btnRandObst -> btnrandObstClick()
            R.id.btnBible -> btnBibleClick()
            R.id.wtvSpeak -> tvSpeackClick()
            R.id.btnseekrund -> btnRandSeekClick()
            R.id.btnseekplay -> btnRandSeekAndSpeakClick()
            R.id.tvVerstext -> txtClick()
            R.id.btnSeekFileDlg -> doSeekFileDlg()

        }

    }

    private fun doSeekFileDlg() {
        val fileDlg = FileDlg(
            gc.mainActivity!!,
            "FileOpen",  //or FileSave or FileSave..  ..= chosenDir with dir | or FileOpen
            seekFileExtn
        ) { chosenDir: String? ->
            binding.tvSeekbar.text = chosenDir
            gc.seekList().openSeekFile(chosenDir!!)
            checkSeekList()
        }
        //fileDlg.create(); //!
        fileDlg.chooseFileOrDir(gc.filesDir.absolutePath)
        //binding.wtvSpeak.isSelected = true
    }


}