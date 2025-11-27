package trust.jesus.discover.dlg_data

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import org.apmem.tools.layouts.FlowLayout
import trust.jesus.discover.R
import trust.jesus.discover.bible.BblChapters
import trust.jesus.discover.little.Globus

class ChooserBcvDlg(context: Context, resultListener: ResultListener?) : Dialog(context), View.OnClickListener {

    private val mResultListener: ResultListener? = resultListener
    private var flowLayout: FlowLayout? = null
    private val gc: Globus = Globus.getAppContext() as Globus
    private var btnBack: Button? = null


    fun interface ResultListener {
        fun onChosenBcv(book: Int, chapter: Int, verse: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //ne in "dlg" binding = BibleVersionsDLG.(layoutInflater)
        setContentView(R.layout.dlgbcv_chooser)
        //setContentView(R.layout.constraint)
        btnBack = findViewById(R.id.btnBack)
        btnBack?.tag = 1
        btnBack?.setOnClickListener {
            when (it.tag) {
                1 -> dismiss()
                2 -> {
                    fillBooks(); btnBack?.tag = 1
                }
                3 -> {
                    fillChapters(bookNumber)
                    btnBack?.tag = 2
                }
            }
        }
        flowLayout = findViewById(R.id.acflowLayout)
        fillBooks()
    }
    var bookNumber = 1;     var chapterNumber = 1;      var verseNumber = 1

    private fun fillBooks() {
        if (flowLayout != null) {
            flowLayout!!.removeAllViews()
            for (i in 1 until 67) {
                val bookshort = gc.bBlparseBook()?.bookNumberToShortName(i)
                if (bookshort != null) {
                    val layoutInflater = LayoutInflater.from(flowLayout!!.context)
                    val ntStart = 40
                    val textView = when (i) {
                        in 1 .. ntStart-1
                            -> layoutInflater.inflate(R.layout.dlg_bcv_ot, flowLayout, false) as TextView
                        ntStart -> layoutInflater.inflate(R.layout.dlg_bcv_nt_nl, flowLayout, false) as TextView
                        else -> layoutInflater.inflate(R.layout.dlg_bcv_nt, flowLayout, false) as TextView
                    }

                    textView.tag = i
                    textView.text = bookshort
                    flowLayout!!.addView(textView)

                    textView.setOnClickListener { view: View? ->
                        val idx = view!!.tag as Int
                        fillChapters(idx)
                        btnBack?.tag = 2
                        //mResultListener?.onChosenString(gc.bBlparseBook()?.bookNumberToShortName[idx])
                        //dismiss()
                    }
                }
            }
        }
    }
    private fun fillChapters(aBookNumber: Int) {
        if (flowLayout != null) {
            flowLayout!!.removeAllViews()
            bookNumber = aBookNumber
            val chapters = BblChapters.versesInChapter[bookNumber - 1].second//BblChapters.maxChapter(aBookNumber)


            for (i in 1..chapters) {
                val textView = LayoutInflater.from(flowLayout!!.context)
                    .inflate(R.layout.dlg_bcv_ot, flowLayout, false) as TextView

                textView.setOnClickListener { view: View? ->
                    val idx = view!!.tag as Int
                    btnBack?.tag = 3;   chapterNumber = idx
                    val verscount = BblChapters.versesInChapter[bookNumber - 1].third[chapterNumber]
                    if (verscount < 9) {
                        verseNumber = 1
                        mResultListener?.onChosenBcv(bookNumber, chapterNumber, verseNumber)
                        dismiss()
                        return@setOnClickListener
                    }
                    fillVerses(idx)
                }
                textView.tag = i
                textView.text = i.toString()

                flowLayout!!.addView(textView)
            }

        }
    }
    private fun fillVerses( chapter: Int) {
        if (flowLayout != null) {
            flowLayout!!.removeAllViews()
            chapterNumber = chapter
            verseNumber =
                BblChapters.versesInChapter[bookNumber - 1].third[chapterNumber]//[chapterNumber]

            for (i in 1..verseNumber) {
                val textView = LayoutInflater.from(flowLayout!!.context)
                    .inflate(R.layout.dlg_bcv_ot, flowLayout, false) as TextView
                textView.setOnClickListener { view: View? ->
                    verseNumber = view!!.tag as Int
                    mResultListener?.onChosenBcv(bookNumber, chapterNumber, verseNumber)
                    dismiss()
                }
                textView.tag = i
                textView.text = i.toString()
                flowLayout!!.addView(textView)
            }

        }
    }

    fun showDialog() {
        create()
        if (!this.isShowing) this.show()

    }
    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }


}