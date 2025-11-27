package trust.jesus.discover.dlg_data

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.ListView
import trust.jesus.discover.R

class BibleVersionsDLG (a: Context, simpleListener: SimpleListener?) : Dialog(a), View.OnClickListener{
    //private lateinit var binding: BibleVersionsDLG
    var msimpleListener: SimpleListener? = simpleListener
    var listView: ListView? = null
    private val versionsnames= arrayOf(
        "deutschsprachig",
        "Menge-Bibel", "Elberfelder Bibel, 1871",
        "Schlachter (1951)", "Schlachter 2000", "Luther (1912)",
        "Hoffnung für Alle, 2015",
        "english Versions",
        "Young's Literal Translation (1898)", "King James Version 1769 with Apocrypha and Strong's Numbers", "New King James Version, 1982",
        "World English Bible", "Revised Standard Version (1952)", "The Complete Jewish Bible (1998)",
        "The Scriptures 2009", "English version of the Septuagint Bible, 1851", "Tree of Life Version",
        "The Legacy Standard Bible", "New American Standard Bible (1995)", "English Standard Version 2001, 2016",
        "Geneva Bible (1599)", "Douay Rheims Bible", "New International Version, 1984",
        "Amplified Bible, 2015", "Literal Standard Version", "The Holy Bible, Berean Standard Bible",
        "Portugal", "France", "England",
        "Portugal", "France", "England", "Italy"
    )



    //val arrayAdapter: ArrayAdapter<*>
    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }

    fun interface SimpleListener {
        fun onChosenVersion(version: String?)
    }

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        //ne in "dlg" binding = BibleVersionsDLG.(layoutInflater)
        setContentView(R.layout.dlgbibleversions)

        listView = findViewById(R.id.lVersionList)
        val adapter = ArrayAdapter(this.context, android.R.layout.simple_list_item_1, versionsnames)
        listView!!.adapter = adapter
        listView!!.setOnItemClickListener { parent, view, position, id ->
            msimpleListener!!.onChosenVersion(versionsnames[position])
            dismiss()
        }
        adapter.notifyDataSetChanged()

    }

    fun showDialog() {
        create()
        if (!this.isShowing) this.show()

    }
}