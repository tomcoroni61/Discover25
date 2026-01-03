package trust.jesus.discover.fragis

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
//import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import org.json.JSONObject
import trust.jesus.discover.R
import trust.jesus.discover.actis.AyWelcome
import trust.jesus.discover.actis.Reportus
import trust.jesus.discover.bible.dataclasses.SsBibel
import trust.jesus.discover.databinding.FragHomeBinding
import trust.jesus.discover.little.FixStuff.Filenames.Companion.crashLogName
import java.io.ByteArrayOutputStream
import java.io.IOException

class HomeFrag: BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragHomeBinding
    //private var bshowText = true


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        binding = FragHomeBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        //binding.cbShowTex.setOnClickListener(this)
        binding.sbtSpeak.setOnClickListener(this)
        binding.sbtPrevDat.setOnClickListener(this)
        binding.mabtMischDat.setOnClickListener(this)
        binding.sbtNextDat.setOnClickListener(this)
        binding.sbtSearch.setOnClickListener(this)
        binding.sbtCrash.setOnClickListener(this)
        binding.sbtSearchBollsApi.setOnClickListener(this)
        val text = "Bible search by <a href='https://bolls.life/api/'>Boll's api </a>"
        //val text = "<a href='http://www.google.com'> Google </a>"
        binding.tvBolls.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        binding.tvBolls.movementMethod = LinkMovementMethod.getInstance()
        //neLinkify.addLinks(binding.tvBolls, Linkify.WEB_URLS)
        //Linkify.addLinks(textView, Linkify.PHONE_NUMBERS|LINKIFY.WEB_URLS);
        if (!gc.appVals().valueReadBool("welcome", false)) {
            gc.appVals().valueWriteBool("welcome", true)
            gc.activityStart(context, AyWelcome::class.java)
        }
        if (gc.dateien().hasPrivateFile(crashLogName)) {
            var cc = gc.appVals().valueReadInt("showCrashBtn", 0)
            if (cc==0) cc=4
            cc--
            if (cc>1) binding.sbtCrash.visibility = View.VISIBLE
            if (cc==1) {
                gc.appVals().valueWriteInt("showCrashBtn", 0)
                gc.dateien().deletePrivateFile(crashLogName)
            }
        }
        // gc.log("HomeFrag onCreateView")
        // loadHtml()
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

    private fun getHtmlText(resId: Int): String {
        val inputStream = getResources().openRawResource(resId)
        val byteArrayOutputStream = ByteArrayOutputStream()
        var i: Int
        try {
            i = inputStream.read()
            while (i != -1) {
                byteArrayOutputStream.write(i)
                i = inputStream.read()
            }
            inputStream.close()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return byteArrayOutputStream.toString()
    }

    private fun loadHtml() {
        binding.HtmlView.movementMethod = LinkMovementMethod.getInstance()

        var htm = getHtmlText( R.raw.thankyou)
        binding.HtmlView.text = Html.fromHtml(htm, Html.FROM_HTML_MODE_COMPACT)
    }
    private fun setFields() {
        binding.matvVersText.text = gc.lernItem.text
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val lp = binding.matvVersText.layoutParams
        if (lp is RelativeLayout.LayoutParams) {
            if (binding.matvVersText.height < binding.nsv.height)
                params.gravity = Gravity.CENTER else
                params.gravity = Gravity.NO_GRAVITY
            binding.matvVersText.layoutParams = params
        }
        //gc.log("setFields: ${gc.lernItem.text}")
        gc.lernItem.setVersTitel()
        val txt: String = gc.lernDataIdx.toString() + "/" + gc.csvList()?.dataList?.size
        binding.ytvCount.text = txt
        //binding.matvVersTop.setText(gc.LernItem.Vers)
    }


    fun mabtPrevDataClick() {
//        throw IllegalArgumentException("Testing crash")
        doCurDataIdx(true)
    }

    fun mabtNextDataClick() {
        doCurDataIdx(false)
    }


    fun maspeackClick() {
        gc.ttSgl()?.cleanSpeak(gc.lernItem.text)
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


    private fun showErry() {
        val intent = Intent(requireContext(), Reportus::class.java)
        intent.putExtra("ErrMsg", "sw.toString()\n Many Errors found\n never ending") //or..Intent.EXTRA_TEXT
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)

    }

    fun mabtSearchClick() {
        // showErry()
        val sText = binding.sedSeek.text.toString()
        if (sText.isEmpty()) {
            gc.globDlg().messageBox("No text to search!", requireContext())
            return
        }
        val idx = gc.csvList()!!.findText(sText, gc.lernDataIdx + 1)
        if (idx < 0) {
            gc.globDlg().messageBox("Nothing found", requireContext())
            return
        }
        gc.csvList()!!.getLernData(idx)
        setFields()
    }



    fun merkToLearnVersClick(idx: Int) {
        gc.csvList()!!.getLernData(idx) //=gc.lernItem
        setFields()
    }


    fun searchBollsApi() {
        //gc.appVals().valueWriteBool("dolog", binding.chipLog.isChecked) discover
        gc.mainActivity!!.viewPager?.currentItem = gc.mainActivity!!.sectionsPagerAdapter!!.pIdxDiscover
        Looper.myLooper()?.let { Handler(it).postDelayed({
            gc.mainActivity!!.sectionsPagerAdapter!!.showBollsPrevActi() }, 1300) }
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
            //R.id.cbShowTex -> maShowTextClick(p0)
            //R.id.ybtspeakObt -> mattsSettingsClick()        //+ intern Tests
            R.id.sbtSpeak ->   maspeackClick() //bibleList()
            R.id.sbtPrevDat -> mabtPrevDataClick()
            R.id.mabtMischDat -> mabtMischDataClick()
            R.id.sbtNextDat -> mabtNextDataClick()
            R.id.sbtSearch -> mabtSearchClick()

            R.id.pwwordmix -> maWordMixClick()
            R.id.pwwordclck -> maWordClick()
            R.id.pwEditclk -> maEditClick()

            R.id.pwsaylck -> maSpeakRecClick()
            R.id.tvbottomLefttw -> maspeackClick()
            R.id.sbtSearchBollsApi -> searchBollsApi()
            R.id.sbtCrash -> sbtCrashClick()
        }
    }

    private fun sbtCrashClick() {
        if (gc.dateien().hasPrivateFile(crashLogName)) {
            val txt = gc.dateien().readPrivateFile(crashLogName).substring(250)
            if (txt.length > 10) gc.startErrorReporter(txt)
            gc.dateien().deletePrivateFile(crashLogName)
            binding.sbtCrash.visibility = View.GONE
            gc.appVals().valueWriteInt("showCrashBtn", 0)
        }
    }

}

