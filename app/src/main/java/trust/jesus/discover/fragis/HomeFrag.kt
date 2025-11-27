package trust.jesus.discover.fragis

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.google.android.material.sidesheet.SideSheetDialog
import com.google.gson.Gson
import org.json.JSONObject
import trust.jesus.discover.R
import trust.jesus.discover.actis.AyWelcome
import trust.jesus.discover.bible.dataclasses.SsBibel
import trust.jesus.discover.bible.online.BssSR
import trust.jesus.discover.databinding.FragHomeBinding
import trust.jesus.discover.dlg_data.ChooserBcvDlg
import trust.jesus.discover.little.FixStuff.Filenames.Companion.merkVers

class HomeFrag: BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragHomeBinding
    private var bshowText = true


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        binding = FragHomeBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        binding.cbShowTex.setOnClickListener(this)
        binding.ybtspeakObt.setOnClickListener(this)
        binding.sbtSpeak.setOnClickListener(this)
        binding.sbtprevdat.setOnClickListener(this)
        binding.mabtmischdat.setOnClickListener(this)
        binding.sbtnextdat.setOnClickListener(this)
        binding.sbtSearch.setOnClickListener(this)
        binding.gbtsetLvers.setOnClickListener(this)
        binding.matvLerni.setOnClickListener(this)
        binding.matvMerkVers.setOnClickListener(this)
        binding.gbtgetLvers.setOnClickListener(this)
        binding.pwwordmix.setOnClickListener(this)
        binding.pwwordclck.setOnClickListener(this)
        binding.pwEditclk.setOnClickListener(this)
        binding.pwsaylck.setOnClickListener(this)
        binding.pwLetters.setOnClickListener(this)
        binding.pwThemes.setOnClickListener(this)
        binding.tvbottomLefttw.setOnClickListener(this)
        binding.mabtWelcome.setOnClickListener(this)
        binding.chipLog.setOnClickListener(this)

        readMerkvers()
        if (!gc.appVals().valueReadBool("welcome", false)) {
            gc.appVals().valueWriteBool("welcome", true)
            gc.activityStart(context, AyWelcome::class.java)
        }
        gc.log("HomeFrag onCreateView")
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setFields()
    }

    private fun doCurDataIdx(minus: Boolean) {
        gc.csvList()?.doLearnDataIdx(minus)
        setFields()
    }

    private fun setFields() {
        if (bshowText) binding.matvVersText.text = gc.lernItem.text
        else binding.matvVersText.text = ""
        //gc.log("setFields: ${gc.lernItem.text}")
        binding.headerTextView.text = gc.lernItem.vers
        gc.setVersTitel(gc.lernItem.vers)
        //binding.matvVersTop.setText(gc.LernItem.Vers)
    }



    fun matxtClick() {
        gc.globDlg().showPopupWin(gc.lernItem.text)
    }

    fun mattsSettingsClick() {
        gc.ttSgl()?.andoSetttings() //gc.mainActivity!! = binding.ybtspeakObt.context
        /*val chooserBcvDlg = ChooserBcvDlg(requireContext()) {
                book: Int, chapter: Int, verse: Int ->
        }
        chooserBcvDlg.showDialog()*/
    }

    fun mabtPrevDataClick() {
        doCurDataIdx(true)
    }

    fun mabtNextDataClick() {
        doCurDataIdx(false)
    }

    fun maShowTextClick(view: View?) {
        val cb = view as CheckBox
        bshowText = cb.isChecked
        setFields()
    }


    fun maspeackClick() {
        gc.ttSgl()?.speak(gc.lernItem.text)
    }

    fun maSpeakRecClick() {
        gc.mainActivity!!.viewPager!!.setCurrentItem(4, false)
    }

    fun maEditClick() {
        gc.mainActivity!!.viewPager!!.setCurrentItem(6, false)
    }

    fun maWordClick() {
        gc.mainActivity!!.viewPager!!.setCurrentItem(1, false)
    }

    fun maWordMixClick() {
        gc.mainActivity!!.viewPager!!.setCurrentItem(2, false)
    }

    fun mabtMischDataClick() {
        gc.csvList()?.getRandomText()
        setFields()
    }

    fun maLettersClick() {
        gc.mainActivity!!.viewPager!!.setCurrentItem(3, false)
    }

    fun mabtSearchClick() {
        val idx = gc.csvList()!!.findText(binding.sedSeek.text.toString(), gc.lernDataIdx + 1)
        if (idx < 0) return
        gc.csvList()!!.getLernData(idx)
        setFields()
    }

    private fun readMerkvers() {
        val vers = gc.appVals().valueReadString(merkVers, "joh 3:16")
        binding.matvMerkVers.text = vers
    }

    fun gSetLernVersClick() {
        gc.appVals().valueWriteString(merkVers, gc.lernItem.vers)
        readMerkvers()
        //
    }

    fun gMerktoLernVersClick() {
        val vers = binding.matvMerkVers.text.toString()
        val idx: Int = gc.csvList()!!.hasBibleVers(vers)
        if (idx < 0) {//matvLerni
            val txt = "$vers nicht gefunden"
            binding.matvVersText.text = txt
            return
        }
        gc.csvList()!!.getLernData(idx) //=gc.lernItem
        setFields()
    }

    fun maThemesClick() {
        // gc.activityStart(this, AyThemes::class.java)
        gc.mainActivity!!.viewPager!!.setCurrentItem(5, false)
    }

    fun mabtWelcomeClick() {
        gc.activityStart(this.activity, AyWelcome::class.java)
    }

    fun chipLogClick() {
        gc.appVals().valueWriteBool("dolog", binding.chipLog.isChecked)
        gc.mainActivity!!.doPageCount()
    }
    fun doThemeSheet() {
        val themeNames: Array<String> = arrayOf(
            getString(R.string.default_light_theme),
            getString(R.string.grey),
            getString(R.string.dark),
            getString(R.string.blue),
            getString(R.string.cyan),
            getString(R.string.green),
            getString(R.string.ocher),
            getString(R.string.orange),
            getString(R.string.purple),
            getString(R.string.red),
            getString(R.string.yellow),
            getString(R.string.default_night_theme),



            )
        val dialog = SideSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.sheet_themes, null)
        val list: ListView = view.findViewById(R.id.listView)
        val arr: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, themeNames)
        list.adapter = arr

        list.setOnItemClickListener { parent, view, position, id ->
            gc.appVals().valueWriteInt("theme", position)
            view.setBackgroundColor(android.graphics.Color.GREEN)
            gc.mainActivity?.setAppTheme(true)
            // dialog.dismiss()
        }

        dialog.setContentView(view)
        //ne list.setItemChecked(gc.appVals().valueReadInt("theme", 0), true)
        //list.setSelection( gc.appVals().valueReadInt("theme", 0))
        dialog.show()
        //list.setSelection( gc.appVals().valueReadInt("theme", 0))

        Handler(Looper.getMainLooper()).postDelayed({
            val item = list[gc.appVals().valueReadInt("theme", 0)]
            val typedValue = TypedValue()
            gc.mainActivity!!.theme.resolveAttribute(R.attr.tabSelected_colour, typedValue, true)
            val color = ContextCompat.getColor(requireContext(), typedValue.resourceId)
            item.setBackgroundColor(color)
        }, 100)
    }

    /*
    fun bibleList() {
        val asiDir = gc.assets.open("bibles/ssapibibles.json")
        val asi = asiDir.bufferedReader().use { it.readText() }
        val wie = JSONObject(asi)
        val bibles = wie.getJSONObject("results")
        val gson = Gson()
        val verseCount = bibles.names()?.length()
        gc.log("found $verseCount bibles")
        var bssArray = emptyArray<SsBibel?>()

        for (i in 0..<verseCount!!) {
            val name = bibles.names()?.getString(i)
            val text = bibles.getString(name.toString())
            val bssR = gson.fromJson(text, SsBibel::class.java)
            bssArray = bssArray.plus(bssR)
            /*
            val bssR = JSONObject(text)
            val sn = bssR.getString("shortname")
            val nm = bssR.getString("name") */

            gc.log("bssR: ${bssR.module} ${bssR.name}")
        }

    }

     */
    fun bibleList() {
        val asiDir = gc.assets.open("bibles/ssapibibles.json")
        val asi = asiDir.bufferedReader().use { it.readText() }
        val wie = JSONObject(asi)
        val bibles = wie.getJSONObject("results")
        val gson = Gson()
        val verseCount = bibles.names()?.length()
        gc.log("found $verseCount bibles")
        val bssArray = mutableListOf<SsBibel>() //emptyArray<SsBibel?>() bssArray = bssArray.plus(bssR)

        for (i in 0..<verseCount!!) {
            val name = bibles.names()?.getString(i)
            val text = bibles.getString(name.toString())
            val bssR = gson.fromJson(text, SsBibel::class.java)
            bssArray.add(bssR)
            //bssArray = bssArray.plus(bssR)
            /*
            val bssR = JSONObject(text)
            val sn = bssR.getString("shortname")
            val nm = bssR.getString("name") */

            //gc.log("bssR: ${bssR.module} ${bssR.name}")
        }
        bssArray.sortBy { it.lang_short }
        bssArray.forEach { gc.log("bssR: ${it.lang_short} ${it.shortname}") }


    }
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.cbShowTex -> maShowTextClick(p0)
            R.id.ybtspeakObt -> mattsSettingsClick()
            R.id.sbtSpeak ->   maspeackClick() //bibleList()
            R.id.sbtprevdat -> mabtPrevDataClick()
            R.id.mabtmischdat -> mabtMischDataClick()
            R.id.sbtnextdat -> mabtNextDataClick()
            R.id.sbtSearch -> mabtSearchClick()
            R.id.gbtsetLvers -> gSetLernVersClick()
            R.id.matvLerni -> matxtClick()
            R.id.matvMerkVers -> matxtClick()
            R.id.gbtgetLvers -> gMerktoLernVersClick()

            R.id.pwLetters -> maLettersClick()
            R.id.pwThemes -> maThemesClick()
            R.id.pwwordmix -> maWordMixClick()
            R.id.pwwordclck -> maWordClick()
            R.id.pwEditclk -> maEditClick()

            R.id.pwsaylck -> maSpeakRecClick()
            R.id.tvbottomLefttw -> maspeackClick()
            R.id.mabtWelcome -> mabtWelcomeClick()
            R.id.chipLog -> chipLogClick()

        }
    }

}

