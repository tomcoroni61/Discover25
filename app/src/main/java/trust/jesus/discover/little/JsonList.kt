package trust.jesus.discover.little

import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import org.json.JSONObject
import trust.jesus.discover.bible.online.BollsSR
import trust.jesus.discover.little.FixStuff.Filenames.Companion.jsonLsFName
import java.io.File

class JsonList {
    private var curVerses: Array<BollsSR?>? = null
    private var lastModified: Long = 0
    private val gson = Gson()
    private val gc: Globus = Globus.Companion.getAppContext() as Globus
    private var verscount: Int = 0
    private var randi = true

    fun entries(): Int {
        readList()
        return verscount
    }

    fun readList() {
        val privateRootDir = gc.filesDir
        val file = File(privateRootDir, jsonLsFName)
        if (!file.exists()) return
        try {

            if (curVerses != null && file.lastModified() == lastModified) return
            lastModified = file.lastModified()
            val wortlist = file.readText()
            val jsonFile = JSONObject(wortlist)
            verscount = jsonFile.getInt("total")
            if (verscount == 0) {
                return
            }
            val jr = jsonFile.getString("results")
            //gc.log(jr)
            if (jr.isNullOrEmpty()) {
                throw IllegalArgumentException("JSON does not contain a verse list")
            }

            //jsonResponse.put("searchword", "holy")
            curVerses = gson.fromJson<Array<BollsSR?>?>(jr, Array<BollsSR>::class.java)

            if (curVerses.isNullOrEmpty()) {
                throw IllegalArgumentException("JSON does not contain a Bible verse")
            }
            for (verse in curVerses)
                verse!!.text =
                    HtmlCompat.fromHtml(verse.text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            if (curVerses!!.size < verscount)
                verscount = curVerses!!.size

            /* ne..
            val vers = curVerses.toString()
            jsonFile.put("results", vers)
            file.delete()
            file.writeText(jsonFile.toString()) */

        } catch (e: Exception) {
            //showNetworkError(requireContext())
            gc.Logl(e.toString(), true)
            //file.delete()
        }
    }

    fun getRandomVers(): BollsSR? {
        val listCount = entries()
        if (entries() < 3) return null
        var idx = gc.random.nextInt(listCount -1) //Random(chapterVerses!!.size-1)
        randi = !randi
        if (!randi) {
            idx = gc.appVals().valueReadInt("lastvers", 0) +1
            if (idx >= listCount) idx = 0
            gc.appVals().valueWriteInt("lastvers", idx)
        }
        return curVerses?.get(idx)
    }

    fun getSuchwort(): String? {
        return gc.appVals().valueReadString("json_suchwort", " ")

    }
    fun getProzentReaded(): Int {
        if (entries() == 0) return 0
        val count = gc.appVals().valueReadInt("lastvers", 0)
        return count * 100 / entries()
    }
}