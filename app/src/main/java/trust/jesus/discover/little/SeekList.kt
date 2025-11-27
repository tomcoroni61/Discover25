package trust.jesus.discover.little

import android.content.Context
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import org.json.JSONObject
import trust.jesus.discover.bible.online.BollsSR
import trust.jesus.discover.little.FixStuff.Filenames.Companion.seekFileExtn


class SeekList {

    private var verscount: Int = 0
    private val gc: Globus = Globus.Companion.getAppContext() as Globus
    private val gson = Gson()
    private var curVerses: Array<BollsSR?>? = null
    //private val random: Random = Random()
    private var dataList: ArrayList<SeekData> = ArrayList()
    class SeekData { //= SearchResult
        var pk: Int = 0
        var numVers: Int = 0
        var numBook: Int = 0
        var numChapter: Int = 0
        var translation: String = "translation"
        var gotit: Boolean = false
        var text: String = "text"

    }
    private var headerList: ArrayList<String> = ArrayList()
    private val delim = '\t';   //gc.appVals().valueReadString("eCurSeekFile","curSeekFile$seekFileExtn" ).toString()
    private var slFileName: String = ""
        get() {return gc.appVals().valueReadString("eCurSeekFile","curSeekFile$seekFileExtn" ).toString() }
        set(value) {field = value; gc.appVals().valueWriteString("eCurSeekFile", value)}


    fun entries(): Int {
        readList()
        return verscount
    }

    fun getRandomVers(): SeekData? {
        val listCount = entries()
        if (entries() < 3) return null
        var idx = gc.random.nextInt(listCount -1) //Random(chapterVerses!!.size-1)
        var cnt = listCount * 5
        var vers: SeekData?
        var foundVers = false
        vers = dataList[idx]
        while (cnt > 0) {
            vers = dataList[idx]
            if (!vers.gotit) {
                vers.gotit = true
                foundVers = true
                break
            }
            idx = gc.random.nextInt(listCount -1)
            cnt--
            if (cnt < listCount) idx = cnt
        }

        //for (verse in curVerses!!)            verse!!.gotit = false
        if (!foundVers) {
            idx = gc.random.nextInt(listCount - 1)
            vers = dataList[idx]
            for (item in dataList)
                item.gotit = false
            gc.Logl("all viewed reset done", true)
        }
        writeList()
        return vers
    }
    fun getProzentReaded(): Int {
        if (entries() == 0) return 0
        var cnt = 0
        for (item in dataList) {
            if (item.gotit) cnt++
        }
        return cnt * 100 / entries()
    }
    fun jsonToVersList(jsonString: String, fileName: String) {
        var crashcnt = 0
        try {

            val jsonFile = JSONObject(jsonString)
            verscount = jsonFile.getInt("total")
            gc.log("verscount: $verscount")
            if (verscount == 0) {
                return
            }
            val jr = jsonFile.getString("results")
            //gc.log(jr)
            if (jr.isNullOrEmpty()) {
                throw IllegalArgumentException("JSON does not contain a verse list")
            }
            crashcnt = 8
                    //jsonResponse.put("searchword", "holy")
            curVerses = gson.fromJson<Array<BollsSR?>?>(jr, Array<BollsSR>::class.java)

            if (curVerses.isNullOrEmpty()) {
                throw IllegalArgumentException("JSON does not contain a Bible verse")
            }
            crashcnt = 12
                for (verse in curVerses)
                    verse!!.text =
                    HtmlCompat.fromHtml(verse.text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

            crashcnt = 15
            if (!gc.dateien().openOutputStream(fileName, Context.MODE_PRIVATE)) return
            var line: String?  //Thema # Vers # Translation # Res1 # Res2 # Text
            crashcnt = 18
            for (i in 0..19) {//= 20 "headerlines"
                line = when (i) {
                    1 -> gc.sharedPrefs.getString("search_word", " ")
                    2 -> gc.sharedPrefs.getBoolean("match_case", false).toString()
                    3 -> gc.sharedPrefs.getBoolean("match_whole", false).toString()
                    4 -> "Res2"
                    5 -> gc.sharedPrefs.getString("search_range", " ")
                    6 -> gc.sharedPrefs.getString("search_bible_version", " ")
                    else -> "headerList[i]"
                }
                crashcnt ++
                if (!gc.dateien().writeLine(line)) break
            }
            crashcnt = 20
            for (item in curVerses) {
                if (item == null) continue
                crashcnt ++
                line = item.verse.toString() + delim + item.book + delim + item.chapter + delim +
                        item.translation + delim + "false" + delim + //item.Text
                        "reservi" + delim + "reservi" + delim + "reservi" + delim +
                        gc.csvList()!!.removeLineEndings(item.text)
                if (!gc.dateien().writeLine(line)) break
            }
            gc.dateien().closeOutputStream()
            slFileName = fileName// gc.appVals().valueWriteString("eCurSeekFile", fileName)
            dataList.clear() //= read new next

        } catch (e: Exception) {
            //showNetworkError(requireContext())
            gc.Logl("crash  $crashcnt  msg: " +e.toString().substring(0, 33), true)
            //file.delete()
        }
    }

    fun getSuchwort(): String {
        if (headerList.size < 4) return ""
        return headerList[1]
    }
    fun openSeekFile(name: String) {
        slFileName = name
        //gc.appVals().valueWriteString("eCurSeekFile", name)
        dataList.clear()
        readList()
    }

    private fun readList() {
        verscount = dataList.size  //
        if (verscount > 2) return
        if (!gc.dateien().openInputStream(slFileName)) return
        buildList()
        gc.dateien().closeInputStream() //slFileName
        verscount = dataList.size
    }
    private fun buildList() { //readFromPrivate
        //gc.Logl("alive readAndInsert: ", true);
        dataList.clear()
        var line: String?
        var block = " "
        var tblnum: Int
        var crashcnt = 0

        //no gc.dateien().readLine(); //ignore Header for save needed
        try {
            headerList.clear()
            for (i in 0..19) {//= 20 "headerlines"
                if (!gc.dateien().readLine()) return
                // if (i == 0 && !gc.dateien().rLine?.startsWith("verslist 2025")!!) return
                headerList.add(gc.dateien().rLine.toString())

            }

            while (gc.dateien().readLine()) {
                line = gc.dateien().rLine
                block = ""
                tblnum = 1
                crashcnt = 6
                if (line != null && line.length > 5) {

                    val obj = SeekData()
                    crashcnt = 1
                    for (i in 0..<line.length) {
                        val c = line[i]
                        crashcnt = 8
                        if (c == delim) {
                            when (tblnum) {
                                1 -> obj.numVers = block.toInt()
                                2 -> obj.numBook = block.toInt()
                                3 -> obj.numChapter = block.toInt()
                                4 -> obj.translation = block
                                5 -> obj.gotit = block.toBoolean()
                                //6..8 reservi
                                9 -> obj.text = gc.csvList()!!.insertLineEndings(block)
                            }
/*
                line = item.verse.toString() + delim + item.book + delim + item.chapter + delim +
                        item.translation + delim + "false" + delim + //item.Text
                        "reservi" + delim + "reservi" + delim + "reservi" + delim +
                        gc.csvList()!!.removeLineEndings(item.text)

 */
                            tblnum++
                            block = ""
                            crashcnt = 4
                        } else block = block + c
                    } //for
                    if (tblnum==9) obj.text = gc.csvList()!!.insertLineEndings(block) //added 2025.08
                    crashcnt = 11
                    dataList.add(obj)
                }
            }
        } catch (e: Exception) {
            gc.Logl("BL nr " + crashcnt + " Msg: " + e.message, true)
        }
    }

    private fun writeList() {
        if (!gc.dateien().openOutputStream(slFileName, Context.MODE_PRIVATE)) return

        for (item in headerList) {
            if (!gc.dateien().writeLine(item)) break
        }
        var cnt = 0
        for (item in dataList) {
            cnt++
            val line = item.numVers.toString() + delim + item.numBook + delim + item.numChapter + delim +
                    item.translation + delim + item.gotit.toString() + delim + //item.Text
                    "reservi" + delim + "reservi" + delim + "reservi" + delim +
                    gc.csvList()!!.removeLineEndings(item.text)
            if (!gc.dateien().writeLine(line)) break
        }
        gc.dateien().closeOutputStream()
        //gc.log("written $cnt entries from " + dataList.size)
    }


}