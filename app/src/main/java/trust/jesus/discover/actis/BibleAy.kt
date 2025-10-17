package trust.jesus.discover.actis

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import trust.jesus.discover.bible.BblChapters
import trust.jesus.discover.R
import trust.jesus.discover.databinding.ActivityBibleAyBinding
import trust.jesus.discover.little.Globus

class BibleAy : AppCompatActivity() {

    lateinit var binding: ActivityBibleAyBinding
    val gc: Globus = Globus.getAppContext() as Globus
    var booknum: Int = 1
    var chapter: Int = 1
    //private var seek: Searcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        gc.mainActivity?.setBibleTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityBibleAyBinding.inflate(layoutInflater)
        gc.mainActivity?.setBibleTheme(this, true)
        setContentView(binding.root)
        enableEdgeToEdge()

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
        booknum = gc.lernItem.numBook
        chapter = gc.lernItem.numChapter

        binding.tvCChapter.text = gc.lernItem.chapter
        if (gc.lernItem.chapter.length < 9) {
            intent!!.putExtra("startVers", gc.lernItem.numVers)
            fetchBibleChapter(gc.lernItem.translation, booknum.toString(), chapter.toString())
        }
            else
            setVersCaps(true)
    }

    private fun setVersCaps(markVers: Boolean = false) {
        binding.tvVersion.text = gc.bolls()?.bibelVersionShortToLong(gc.lernItem.translation)
        val txt = gc.bBlparseBook()?.bookNameCapital( booknum) + "  " + chapter
        binding.tvBooknameChap.text = txt
        if (!markVers || gc.lernItem.numVers < 4) return //"\n"+
        val searchVers = "\n"+gc.lernItem.numVers.toString() + " "
        val idx = binding.tvCChapter.text.indexOf(searchVers)
        //gc.log("search: $searchVers, idx: $idx")
        if (idx < 3) return

        Handler(Looper.getMainLooper()).postDelayed({
            scrollToIndex(idx)
        },  100L)
        //Handler(Looper.getMainLooper()).postDelayed({ // Your Code }, 1000)


    }
    fun scrollToIndex(idx: Int): Boolean {
        try {
            val textField = binding.tvCChapter
            val lt = textField.layout
            if (lt == null) return false
            var lineNumber = lt.getLineForOffset(idx)
            //gc.log("scrollToIndex: " + lineNumber)
            if (lineNumber < 3) return false
            //highlightFoundWort(foundIdx);
            lineNumber = lineNumber - 2
            val scrollY = lt.getLineTop(lineNumber)
            if (scrollY > 3) binding.svChapText.scrollTo(0, scrollY)
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
    fun btnPrevClick(view: View) {
        var nc = chapter - 1
        var nb = booknum
        if (nc < 1) {
            nb --
            if (nb < 1) nb = 66
            nc = BblChapters.maxChapter(nb)
        }
        binding.tvBooknameChap.text = getString(R.string.wait)
        binding.tvCChapter.text = ""
        booknum = nb
        chapter = nc
        fetchBibleChapter(gc.lernItem.translation, nb.toString(), nc.toString())

    }
    fun btnNextClick(view: View) {
        val maxChapter = BblChapters.maxChapter(booknum)
        var nc = chapter + 1
        var nb = booknum
        if (nc > maxChapter) {
            nb ++
            nc = 1
        }
        if (nb > 66) {
            nb = 1
        }

        binding.tvBooknameChap.text = getString(R.string.wait)
        binding.tvCChapter.text = ""
        booknum = nb
        chapter = nc
        fetchBibleChapter(gc.lernItem.translation, nb.toString(), nc.toString())

    }

    fun fetchBibleChapter(version: String, bookNum: String, chapter: String) {
        lifecycleScope.launch {
            try { //Date(),
                gc.bolls()!!.fetchBibleChapter(version, bookNum, chapter)
                    .collect { result ->
                        if (result.isSuccess) {
                            val verses = result.getOrNull()  //verses?.forEach { verse ->

                            binding.tvVersion.text = gc.lernItem.translation
                            binding.tvBooknameChap.text = gc.bBlparseBook()?.bookNameCapital( gc.lernItem.numBook)
                            binding.tvCChapter.text = gc.bolls()?.versArrayToChaptertext(verses)

                            val intent = getIntent()
                            val vx = intent.getIntExtra("startVers", 0)
                            setVersCaps(vx>0)
                            intent.putExtra("startVers", 0)
                        }
                    }
            } catch (e: Exception) {
                //showNetworkError(requireContext())
            }
        }
    }

}