package trust.jesus.discover.actis

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import trust.jesus.discover.R
import trust.jesus.discover.bible.BblChapters
import trust.jesus.discover.bible.dataclasses.VersItem
import trust.jesus.discover.databinding.ActivityBibleAyBinding
import trust.jesus.discover.dlg_data.ChooserBcvDlg
import trust.jesus.discover.little.Globus


class BibleAy : AppCompatActivity(), AbsListView.OnScrollListener {

    lateinit var binding: ActivityBibleAyBinding
    val gc: Globus = Globus.getAppContext() as Globus
    private var bookNum: Int = 1;    private var chapter: Int = 1;  private var verseNum: Int = 1
    private var lastIndex: Int = -1
    private var goPrev = false
    private var adapter: VersViewAdapter? = null
    private val timhandl = Handler(Looper.getMainLooper())

    private val runScrolido: Runnable = Runnable {//okhttp = "5.1.0"
        var bnPchap = gc.bBlparseBook()?.bookNameCapital(bookNum) + "  " + chapter
        if (verseNum > 0) bnPchap += ":$verseNum"
        binding.tvBooknameChap.text = bnPchap
        if (viewAsBook) return@Runnable
        if (binding.lvVers.lastVisiblePosition > adapter!!.count -5)
            btnNextClick(null)
    }
    override fun onScroll(p0: AbsListView?, firstVisibleItemIndex: Int, linesVisible: Int, linesCount: Int ) {
        if ( p0 == null || adapter == null || lastIndex==firstVisibleItemIndex) return
        //gc.Logl("onScroll: $firstVisibleItemIndex $linesVisible $linesCount", false)
        if (firstVisibleItemIndex >= adapter!!.count) return
        if (!viewAsBook && firstVisibleItemIndex < 5 && firstVisibleItemIndex < lastIndex) {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    if (!viewAsBook) btnPrevClick(null)
                },
                23
            )
            lastIndex = firstVisibleItemIndex
            return
        }
        lastIndex = firstVisibleItemIndex
        doTitelLine(firstVisibleItemIndex)
    }
    private fun doTitelLine(visiLine: Int) {
        val difi = 1
        val item = adapter!!.getItem(visiLine + difi)
        if (item == null ) return //item.nVers==-1 = no vers
        if (item.nBook == bookNum && item.nChapter == chapter && item.nVers == verseNum) return
        bookNum = item.nBook;        chapter = item.nChapter
        verseNum = item.nVers
        timhandl.postDelayed(runScrolido, 411)

    }

    override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
       // gc.log("onScrollStateChanged: $p1")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        gc.mainActivity?.setBibleTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityBibleAyBinding.inflate(layoutInflater)
        gc.mainActivity?.setBibleTheme(this, true)
        setContentView(binding.root)
        enableEdgeToEdge()
        adapter = VersViewAdapter(this)
//after many tries this fits R.id.main between systembars.. todo in all activities  android:id="@+id/main"
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top,
                systemBars.right, 0)
            //systemBars.bottom - systemBars.top
            insets
        }
        //gc.log("BibleAy onCreate")
        //gc.checkLernItemForBolls()
        bookNum = gc.lernItem.numBook
        chapter = gc.lernItem.numChapter
        binding.lvVers.adapter = adapter
        binding.lvVers.setOnScrollListener(this)
        viewAsBook = gc.appVals().valueReadBool( "viewAsBook", true)
        binding.switchChapterView.isChecked = viewAsBook

        val intent = getIntent()
        //binding.tvCChapter.text = gc.lernItem.chapter
        if (gc.lernItem.chapter.isEmpty()) {
            if (!intent.hasExtra("startVers"))
                intent!!.putExtra("startVers", gc.lernItem.numVers)
            fetchBibleChapter(gc.lernItem.translation, bookNum.toString(), chapter.toString())
        }
            else {
                doVersView()
                setVersCaps(gc.lernItem.numVers)
            }

    }

    fun doVersView() {
        someFetch=true
        adapter?.clear()
        val bn=bookNum;     val cr = chapter
        val bnPchap = gc.bBlparseBook()?.bookNameCapital(bn) + "  " + cr
        adapter?.add( VersItem("$bnPchap\n ", "$bnPchap\n ", -1, bn, cr) )
        for (i in gc.lernItem.chapter) {
            adapter?.add(i)
        }
        adapter?.add(VersItem("","\n ", -1, bn, cr))
        timhandl.postDelayed(runEndSpeaking, 3111)
    }
    private fun setVersCaps(markVers: Int = 0) {
        binding.tvVersion.text = gc.bolls()?.bibelVersionShortToLong(gc.lernItem.translation)
        val txt = gc.bBlparseBook()?.bookNameCapital( bookNum) + "  " + chapter
        binding.tvBooknameChap.text = txt
        if (markVers < 1) return //"\n"+

        Handler(Looper.getMainLooper()).postDelayed({
            scrollToIndex(markVers)
            val item = adapter!!.getItem(markVers)
            if (item == null ) return@postDelayed
            item.selected = true
            adapter?.notifyDataSetChanged()

            Handler(Looper.getMainLooper()).postDelayed({
                val item = adapter!!.getItem(markVers)
                if (item == null ) return@postDelayed
                item.selected = false
                adapter?.notifyDataSetChanged()
            },  2200L)

        },  200L)
        //Handler(Looper.getMainLooper()).postDelayed({ // Your Code }, 1000)


    }
    fun scrollToIndex(idx: Int): Boolean {
        try {
            //val textField = binding.tvCChapter smooth
            var idx = idx
            adapter?.count?.let {
                if (it > idx+3)  idx = idx + 2
            }

            binding.lvVers.smoothScrollToPosition(idx)
            Handler(Looper.getMainLooper()).postDelayed({
                binding.lvVers.performItemClick( binding.lvVers.getChildAt(idx), idx, 0 )
                //binding.lvVers.performLongClick()
            },  200L)
            return true
        } catch (e: Exception) {
            Toast.makeText(gc, "Crash by Searcher: " + e.message, Toast.LENGTH_LONG).show()
            //gc.HS().internLog("Crash on sturtup: " + chrashCnt + " msg: " + e.getMessage());
        }
        return false
    }


    fun btnExitClick(view: View) {
       // setVersCaps(true)
        finish()
    }
    private var toIdx = 0;  private var xcnt = 0
    private var scrollToBookNum: Int = 1;    private var scrollToChapter: Int = 1
    private val runScroltoIdi: Runnable = Runnable {
        gc.log( "fiLi ${binding.lvVers.firstVisiblePosition} < ${binding.lvVers.lastVisiblePosition} ")
        var pos = binding.lvVers.firstVisiblePosition + 2
        if (pos < 0) pos = 0
        val item = adapter!!.getItem(pos)
        if (item == null ) {
            timhandl.postDelayed(runEndSpeaking, 1111)
            return@Runnable
        }
        gc.log("item: ${item.nBook}=$scrollToBookNum   ${item.nChapter} = $scrollToChapter  ${item.nVers} > -2 ")
        if (!(item.nBook == scrollToBookNum && item.nChapter == scrollToChapter && item.nVers > -2)) {
            if (toIdx+xcnt >= adapter!!.count) {
                timhandl.postDelayed(runEndSpeaking, 3111)
                return@Runnable
            }
            binding.lvVers.smoothScrollToPosition(toIdx+xcnt)
            xcnt++
            if (xcnt > 20) {
                timhandl.postDelayed(runEndSpeaking, 3111)
                return@Runnable
            }
            timhandl.postDelayed(runScroltoIdi, 111)
            return@Runnable
        }
        timhandl.postDelayed(runEndSpeaking, 3111)
    }//if (binding.lvVers.firstVisiblePosition < toIdx-2) {

    fun btnPrevClick(view: View?) {
        if (someFetch || lastFetch > System.currentTimeMillis() - 5000) return
        var nc = chapter - 1
        var nb = bookNum
        if (nc < 1) {
            nb --
            if (nb < 1) nb = 66
            nc = BblChapters.maxChapter(nb)
        }
        if (hasChapter(nb, nc)) return
        binding.tvBooknameChap.text = getString(R.string.wait)
        goPrev = true
        bookNum = nb
        chapter = nc
        //if (view != null) adapter?.clear()
        scrolltochap = view != null
        fetchBibleChapter(gc.lernItem.translation, nb.toString(), nc.toString())

    }
    fun hasChapter(aBookNum: Int, aChapter: Int): Boolean {
        for (i in 0..<adapter!!.count) {
            val item = adapter!!.getItem(i)
            if (item == null ) return false
            if (item.nBook == aBookNum && item.nChapter == aChapter) {
                someFetch = true
                binding.lvVers.smoothScrollToPosition(i)
                toIdx = i+1;                xcnt = 0
                scrollToChapter = aChapter;         scrollToBookNum = aBookNum
                timhandl.postDelayed(runScroltoIdi, 111)
                //ne someFetch = false
                return true
            }
        }
        return false
    }

    fun btnNextClick(view: View?) {
        if (someFetch || lastFetch > System.currentTimeMillis() - 5000) return
        val maxChapter = BblChapters.maxChapter(bookNum)
        var nc = chapter + 1
        var nb = bookNum
        if (nc > maxChapter) {
            nb ++
            nc = 1
        }
        if (nb > 66) {
            nb = 1
        }
        if (hasChapter(nb, nc)) return
        goPrev = false
        binding.tvBooknameChap.text = getString(R.string.wait)
        //binding.tvCChapter.text = "" someFetch
        bookNum = nb
        chapter = nc
        //if (view != null) adapter?.clear()
        scrolltochap = view != null
        fetchBibleChapter(gc.lernItem.translation, nb.toString(), nc.toString())

    }

    private var scrolltochap = false
    private var someFetch = false;      private var lastFetch = 0L
    private var fetchVersion: String = ""

    fun fetchBibleChapter(version: String, bookNum: String, chapter: String) {
        if (someFetch) return
        someFetch = true; lastFetch = System.currentTimeMillis()
        if (viewAsBook)
            adapter?.clear()
        lifecycleScope.launch {
            try { //Date(),
                gc.bolls()!!.fetchBibleChapter(version, bookNum, chapter)
                    .collect { result ->
                        if (result.isSuccess) {
                            val verses = result.getOrNull()  //verses?.forEach { verse ->

                            fetchVersion = gc.bolls()?.lastVersion.toString()
                            binding.tvVersion.text = gc.bolls()?.bibelVersionShortToLong(fetchVersion)
                            val bnPchap = gc.bBlparseBook()?.bookNameCapital( bookNum.toInt()) + "  " + chapter
                            binding.tvBooknameChap.text = bnPchap
                            //gc.Logl("fetchBibleChapter: $bnPchap  success", true)
                            var cnt = 1 //(binding.lvVers.adapter as VersViewAdapter)
                            if (goPrev && adapter!!.count > 0) {//adapter?.clear() selectedItemPosition
                                if (verses != null) {
                                    cnt = verses.size
                                    var pos = binding.lvVers.firstVisiblePosition + cnt
                                    if (adapter?.count == 0) pos = 0
                                    adapter?.insert( VersItem("", "\n ", -1,
                                        bookNum.toInt(), chapter.toInt() ), 0 )
                                    for (verse in verses.reversed()) {
                                        adapter?.insert( VersItem(verse!!.text, cnt.toString() + " " + verse.text,
                                            cnt, bookNum.toInt(), chapter.toInt() ), 0 )
                                        cnt--
                                    }
                                    adapter?.insert( VersItem("$bnPchap\n ", "$bnPchap\n ", -1,
                                        bookNum.toInt(), chapter.toInt() ), 0 )

                                    Handler(Looper.getMainLooper()).postDelayed(
                                        {
                                            binding.lvVers.setSelection(pos+1)
                                        },
                                        635
                                    )
                                }
                            } else {
                                adapter?.add( VersItem("$bnPchap\n ", "$bnPchap\n ", -1, bookNum.toInt(), chapter.toInt() ) )
                                if (verses != null) {
                                    for (verse in verses) {
                                        adapter?.add( VersItem(verse!!.text, cnt.toString() + " " + verse.text,
                                                cnt, bookNum.toInt(), chapter.toInt() ) )
                                        cnt++
                                    }
                                }
                            }
                            adapter?.add(VersItem("","\n ", -1, bookNum.toInt(), chapter.toInt() ))
                            val intent = getIntent()
                            val vx = intent.getIntExtra("startVers", 0)
                            setVersCaps(vx)
                            intent.putExtra("startVers", 0)
                            if (scrolltochap) {
                                if (!hasChapter(bookNum.toInt(), chapter.toInt()))
                                    binding.lvVers.smoothScrollToPosition(0)
                            }
                            scrolltochap = false
                            Handler(Looper.getMainLooper()).postDelayed(
                                {
                                    doTitelLine(binding.lvVers.firstVisiblePosition)
                                },
                                335
                            )


                        } else {
                            adapter?.add(VersItem("", "wrong Version?  $version", -1))
                            binding.tvBooknameChap.text = ""
                        }
                        timhandl.postDelayed(runEndSpeaking, 3111)
                        lastFetch = System.currentTimeMillis()
                    }
            } catch (e: Exception) {
                adapter?.add(VersItem("vers", "wrong Version?  $version\n"
                        + e.message.toString() + "\n" + e.stackTraceToString() + "\n", -1))
                timhandl.postDelayed(runEndSpeaking, 3111)
                lastFetch = System.currentTimeMillis()
            }
        }
    }

    fun btnMenuClick(view: View) {// !keep view else crash!
        if (binding.nsvSheet.isVisible)
            binding.nsvSheet.visibility = View.GONE
        else {
            //binding.nsvSheet.setH
            binding.nsvSheet.visibility = View.VISIBLE
        }
        /* sheet opt:
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.sheet_bible_speak, null)
        dialog.setContentView(view)
        dialog.show()*/
    }
    fun btnTextSmalerClick(view: View) { adapter!!.textSizeMinus() }
    fun btnTextLargerClick(view: View) { adapter!!.textSizePlus() }
    private  val speakList = mutableListOf<VersItem>()
    private var speakItem: VersItem? = null
    private var wakeLock: PowerManager.WakeLock? = null

    private fun keepWake(secs: Int) {
        if (wakeLock != null) wakeLock?.release()
        wakeLock = (getSystemService(POWER_SERVICE) as PowerManager).
        newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TrustJesus:WakeLock")

        wakeLock?.acquire(secs*1000L)
    }
    fun btnSpeakChapterClick(view: View) {// !keep view else crash!
        if (chapter < 1) return
        speakList.clear()
        for (i in 0..<adapter!!.count) {
            val item = adapter!!.getItem(i)
            if (item == null ) return
            if (item.nBook == bookNum && item.nChapter == chapter) {
                speakList.add(item)
                item.positon = i
            }
        }
        //if (txt.isNotEmpty()) gc.ttSgl()?.speak(txt)
        if (speakList.isEmpty()) return
        someFetch = true  //must else "autofetch"
        timhandl.postDelayed(runSpeaki, 111)
    }
    private val runEndSpeaking: Runnable = Runnable {
        someFetch = false
        adapter?.unSelectAll()
        //timhandl.postDelayed(runEndSpeaking, 3111)
    }
    private val runSpeaki: Runnable = Runnable {
        if (speakItem != null) {
            speakItem!!.selected = false
            adapter?.notifyDataSetChanged()
            speakItem = null
        }
        if (speakList.isEmpty()) {
            someFetch = false
            return@Runnable
        }
        if (!gc.isScreenOn()) {
            var resttext = speakList.joinToString("\n") { it.vers }
            resttext = gc.doSpeaktext(resttext)
            gc.ttSgl()?.speak(resttext)
            timhandl.postDelayed(runEndSpeaking, 3111)
            return@Runnable
        }

        speakItem = speakList[0]
        speakList.removeAt(0)
        val spkText = gc.doSpeaktext( speakItem!!.vers)

        gc.ttSgl()?.speak(spkText)
        if (spkText.length > 15) keepWake(spkText.length / 2)
        val halfLines = (binding.lvVers.lastVisiblePosition -  binding.lvVers.firstVisiblePosition)/2
        var scrollTo = speakItem!!.positon
        if (scrollTo > binding.lvVers.lastVisiblePosition-halfLines)
            scrollTo += halfLines else {
            if (scrollTo < binding.lvVers.firstVisiblePosition-2)
                scrollTo -= halfLines
        }
        // if (scrollTo < binding.lvVers.firstVisiblePosition) scrollTo -= halfLines
        if (scrollTo >= binding.lvVers.adapter.count) scrollTo = binding.lvVers.adapter.count-1
        if (scrollTo < 0) scrollTo = 0
        binding.lvVers.smoothScrollToPosition(scrollTo)
        timhandl.postDelayed(runIsSpeaking, 3111)
        speakItem!!.selected = true
        adapter?.notifyDataSetChanged()
    }
    private val runIsSpeaking: Runnable = Runnable {
        if (gc.ttSgl()?.isSpeaking() == true) {
            timhandl.postDelayed(runIsSpeaking, 511)
            return@Runnable
        }
        timhandl.postDelayed(runSpeaki, 111)
    }
    fun btnSpeakSelectedClick(view: View) { // !keep view else crash!
        val txt = adapter!!.selectedText(true)

        if (txt.isNotEmpty()) gc.ttSgl()?.speak(txt)
        adapter?.unSelectAll()
    }
    fun btnStopSpeakClick(view: View) {
        speakList.clear()
        gc.ttSgl()?.stop()
        someFetch = false
        adapter?.unSelectAll()
    }

    fun btnTextCopyClick(view: View) {
        val txt = adapter!!.selectedText()
        if (txt.isNotEmpty()) gc.copyTextToClipboard(txt)
    }

    fun btnChoosiClick(view: View) {
        val chooserBcvDlg = ChooserBcvDlg(this) {
                book: Int, chapter: Int, verse: Int ->
            //gc.Logl("Book: $book, Chapter: $chapter, Verse: $verse", true)
            adapter!!.clear()
            intent.putExtra("startVers", verse);
            binding.tvBooknameChap.text = "wait.."
            fetchBibleChapter(fetchVersion, book.toString(), chapter.toString())
        }
        chooserBcvDlg.showDialog()
    }
    private var viewAsBook = true

    fun btnChapterViewClick(view: View) {
        viewAsBook = binding.switchChapterView.isChecked
        gc.appVals().valueWriteBool( "viewAsBook", viewAsBook)
    }


}