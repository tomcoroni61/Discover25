package trust.jesus.discover.fragis

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.size
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import trust.jesus.discover.R
import trust.jesus.discover.databinding.FragmentEntdeckeBinding
import trust.jesus.discover.dlg_data.CsvData
import trust.jesus.discover.preference.SettingsRand

private const val Arg_search = "Arg_search"

class EntdeckeFrag : BaseFragment(), View.OnClickListener {

    private val okList: MutableList<String?> = ArrayList()
    //private var chapterVerses: Array<BollsSR?>? = null
    private var paramSearch: String? = null
    private var showMixed = true
    private var txtSize = 18

    private lateinit var binding: FragmentEntdeckeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paramSearch = savedInstanceState?.getString(Arg_search)

        //gc.Logl("onCreate  " + (paramSearch!= null), false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,  savedInstanceState: Bundle? ): View? {
        paramSearch = savedInstanceState?.getString(Arg_search)
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

        showMixed = gc.appVals().valueReadBool("btn.splitTrack", false)
        txtSize = gc.appVals().valueReadInt("txt.size", 18)
        checkVerslist()

        textToFlowlayout(gc.lernItem.text, showMixed)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        checkVerslist()
        //gc.Logl("onResume  " + (paramSearch!= null), false)
        textToFlowlayout(gc.lernItem.text, showMixed)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // gc.Logl(("onSaveInstanceState  $paramSearch"), true)
        //outState.putBoolean(IS_EDITING_KEY, isEditing)
        if (paramSearch != null)
            outState.putString(Arg_search, paramSearch)

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            EntdeckeFrag().apply {
                arguments = Bundle().apply {
                    putString(Arg_search, param1)
                 //   putString(ARG_PARAM2, param2)
                }
            }
    }



    private fun getViewIdx(view: View?): Int {
        for (i in 0..<binding.flowLayout.size) {
            val v: View = binding.flowLayout.getChildAt(i)
            if (v === view) return i
        }
        return -1
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
    fun textToFlowlayout(txt: String?, doMischen: Boolean) {
        if (txt?.isEmpty() == true ) return
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
        if (doMischen) mischViewss()
    }

    fun setTextSizes(size: Int) {
        for (i in 0..<binding.flowLayout.size) {
            val v: TextView = binding.flowLayout.getChildAt(i) as TextView
            v.textSize = size.toFloat()
        }
    }
    fun mischViewss() {
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
        removeView = null
    }

    private var removeView: TextView? = null
    private var falschView: TextView? = null

    private fun checkNewPlatz(idx: Int, view: View) {
        val vText = (view as TextView).text.toString()
        val riPl = vText == okList.get(idx)

        if (riPl) view.setBackgroundResource(R.drawable.richtigplaz) else view.setBackgroundResource(
            R.drawable.rounded_corner
        )
    }

    private fun OkCheck() {
        var okcnt = 0
        val cnt: Int = binding.flowLayout.size
        for (i in 0..<cnt) {
            val v = binding.flowLayout.getChildAt(i) as TextView
            val txt = v.text.toString()
            if (txt == okList[i]) okcnt++ else v.setBackgroundResource(R.drawable.rounded_corner)
        }
        if (okcnt != cnt) {
            return
        }
        //gc.Logl("Oki: "+okcnt+ " / "+Cnt, true); binding.flowLayout.size
        for (i in 0..<cnt) {
            val v: View = binding.flowLayout.getChildAt(i)
            v.setBackgroundResource(R.drawable.richtigplaz)
        }
        binding.tvVerstext.text = gc.lernItem.text
        binding.ayheaderTextView.text = gc.lernItem.vers

        // gc.showPopupWin(this.binding.root, gc.LernItem.Text, { this.mischViews() })
    }

    private fun wordClick(view1: View?) {
        val tv = view1 as TextView
        val txt = tv.text.toString()
        val viewIdx = getViewIdx(tv)
        if (falschView != null) {
            falschView?.setBackgroundResource(R.drawable.rounded_corner)
            falschView = null
        }
        if (removeView != null) {
            if (removeView === tv) {
                removeView!!.setBackgroundResource(R.drawable.rounded_corner)
                removeView = null
            } else {
                if (viewIdx > -1) {
                    binding.flowLayout.removeView(removeView)
                    binding.flowLayout.addView(removeView, viewIdx)
                    checkNewPlatz(viewIdx, removeView!!)
                    OkCheck()
                }

                removeView = null
            }
        } else {
            if (txt == okList.get(viewIdx)) tv.setBackgroundResource(R.drawable.richtigplaz)
            else {
                val rid = isNext(view1)
                if (rid > -1) {
                    binding.flowLayout.removeView(view1)
                    binding.flowLayout.addView(view1, rid)
                    tv.setBackgroundResource(R.drawable.richtigplaz)
                    OkCheck()
                } else {
                    falschView = tv
                    //removeView!!.setBackgroundResource(R.drawable.selected)
                    tv.setBackgroundResource(R.drawable.falschplatz)
                }
            }
        }
    }

    private fun isNext(view: View): Int {
        val Cnt: Int = binding.flowLayout.size
        var okcnt = -1
        val viewtxt = (view as TextView).text.toString()

        for (i in 0..<Cnt) {
            val v = binding.flowLayout.getChildAt(i) as TextView
            val txt = v.text.toString()
            if (txt == okList.get(i)) okcnt++ else {
                if (viewtxt == okList.get(i)) return i
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
        PreferenceManager.getDefaultSharedPreferences(requireContext())
        val range = getRandRange(requireContext())
        val verse =  gc.bBlparseBook()?.randomBookAndChapter(range )
        binding.tvVerstext.text = "wait"
        binding.ayheaderTextView.text = "??"
        val version = getVersion( requireContext() )

        //fetchBibleVerse("NKJV", "1", "1", "1") //
        fetchBibleChapter(version, verse?.book.toString(), verse?.chapter.toString() )
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

                            gc.lernItem.setBollsSearchResult(gc.bolls()?.versArrayToChaptertext(verses).toString(),
                                txt.toString(), version,vers+1, bookNum.toInt(), chapter.toInt())
                            gc.lernItem.addToHistory()
                            /*gc.lernItem.chapter = gc.bolls()?.versArrayToChaptertext(verses).toString()
                            if (txt != null) {
                                gc.lernItem.text = txt
                            }
                            gc.lernItem.vers =
                                gc.bBlparseBook()!!.versShortName(bookNum.toInt(), chapter.toInt(), vers+1)
                            gc.lernItem.translation = version
                            gc.lernItem.partText = "ne"
                            gc.lernItem.numVers = vers
                            gc.lernItem.numBook = bookNum.toInt()
                            gc.lernItem.numChapter = chapter.toInt()
                            //gc.log("fetche: " + gc.lernItem.NumVers)
                            val cdata = CsvData()
                            gc.csvList()?.copyData(gc.lernItem, cdata)
                            gc.versHistory.addVers(cdata) */
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


    fun checkVerslist() {
        val count = if (gc.sharedPrefs.getBoolean("use_jsonList", true)) {
            gc.jsonList()!!.entries()
        } else
            gc.seekList().entries()

        if (count < 3) {
            binding.llseekbar.visibility = View.INVISIBLE
            return
        }
        binding.llseekbar.visibility = View.VISIBLE
        if (gc.sharedPrefs.getBoolean("use_jsonList", true)) {
            val stat =  gc.jsonList()!!.getSuchwort() +
                    "   Vc: " + gc.jsonList()!!.entries() + " done " + gc.jsonList()!!.getProzentReaded() + "% "
            binding.tvverscnt.text = stat
        } else {
            val stat =  gc.seekList().getSuchwort() +
                    "   Vc: " + gc.seekList().entries() + " done " + gc.seekList().getProzentReaded() + "% "
            binding.tvverscnt.text = stat
        }

    }

    private fun btnrandseekClick() {
        if (gc.sharedPrefs.getBoolean("use_jsonList", true)) {
            val jvers = gc.jsonList()?.getRandomVers()
            gc.lernItem.setBollsSrVers(jvers)


        }  else {
            if (gc.seekList().entries() < 2) return

            val vers = gc.seekList().getRandomVers()
            gc.lernItem.setSeekVers(vers)
            //gc.log("fetche: " + gc.LernItem.VersNum)
            binding.tvVerstext.text = ""
            binding.ayheaderTextView.text = ""

        }
        gc.lernItem.chapter = ""
        gc.lernItem.addToHistory()
        gc.setVersTitel(gc.lernItem.vers)
        checkVerslist()
        textToFlowlayout(gc.lernItem.text, showMixed)
        //mischViews()
    }

    fun btnrandseekAndSpeakClick() {
        btnrandseekClick()
        tvSpeackClick()
    }

    fun doButtonSheet() {
        val dialog = BottomSheetDialog(requireContext())

        val view = layoutInflater.inflate(R.layout.sheet_discover, null)
        val btn: SwitchCompat = view.findViewById(R.id.switchMix)
        btn.splitTrack = gc.appVals().valueReadBool("btn.splitTrack", false)
        btn.setOnClickListener {
            btn.splitTrack = !btn.splitTrack //!btn.isChecked
            showMixed = btn.splitTrack
            gc.appVals().valueWriteBool("btn.splitTrack", btn.splitTrack)

        }
        // dialog.setCancelable(false)
        var botn: ImageButton = view.findViewById(R.id.btnDown)
        botn.setOnClickListener {
            // txtSize = gc.appVals().valueReadInt("txt.size", 18)
            txtSize  --
            if (txtSize < 12) txtSize = 12
            setTextSizes(txtSize)
            gc.appVals().valueWriteInt("txt.size", txtSize)
        }
        botn = view.findViewById(R.id.btnUp)
        botn.setOnClickListener {
            txtSize ++
            if (txtSize > 24) txtSize = 24
            setTextSizes(txtSize)
            gc.appVals().valueWriteInt("txt.size", txtSize)
        }
        // set content view to our view.
        dialog.setContentView(view)
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

            R.id.btn_randObst -> btnrandObstClick()
            R.id.btnBible -> btnBibleClick()
            R.id.wtvSpeak -> tvSpeackClick()
            R.id.btnseekrund -> btnrandseekClick()
            R.id.btnseekplay -> btnrandseekAndSpeakClick()
            R.id.tvVerstext -> txtClick()

        }

    }


}