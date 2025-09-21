package trust.jesus.discover.dlg_data

import android.content.Context
import trust.jesus.discover.little.FixStuff.Filenames.Companion.lineBreak
import trust.jesus.discover.little.Globus
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.Random
import java.util.StringTokenizer

//private val mContext: Context
class CsvList() {
    var dataList: ArrayList<CsvData> = ArrayList()
    var filename: String? = null

    private val random = Random()
    private val gc: Globus = Globus.getAppContext() as Globus
    private var brandi = false

    fun readFromPrivate(csvFile: String?, delim: Char): Boolean {
        if (!gc.dateien().openInputStream(csvFile)) return false

        buildList(delim)

        gc.dateien().closeInputStream()
        return dataList.size > 5
    }

    fun insertLineEndings(value: String): String {
        if (value.isEmpty()) {
            return " "
        }

        return value.replace( lineBreak, "\n")
    }
    fun removeLineEndings(value: String): String {
        if (value.isEmpty()) {
            return " "
        }
        val lineSeparator = (0x2028.toChar()).toString()
        val paragraphSeparator = (0x2029.toChar()).toString()

        return value.replace("\r\n", lineBreak)
            .replace("\n", lineBreak)
            .replace("\r", lineBreak)
            .replace(lineSeparator, "")
            .replace(paragraphSeparator, "")
            .trim()
    }
    fun saveToPrivate(csvFile: String?, delim: Char) {
        if (!gc.dateien().openOutputStream(csvFile, Context.MODE_PRIVATE)) return
        var line: String? //Thema # Vers # Translation # Res1 # Res2 # Text
        for (item in dataList) {
            line = item.bereich + delim + item.vers + delim + item.translation + delim +
                    item.partText + delim + item.Res2 + delim + //item.Text
                    removeLineEndings(item.Text)
            if (!gc.dateien().writeLine(line)) break
        }
        gc.dateien().closeOutputStream()
    }

    private fun buildList(delim: Char) { //readFromPrivate
        //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        //gc.Logl("alive readAndInsert: ", true);
        dataList.clear()
        var line: String?
        var block = " "
        var tblnum: Int
        var crashcnt = 0

        //no gc.dateien().readLine(); //ignore Header for save needed
        try {
            while (gc.dateien().readLine()) {
                line = gc.dateien().rLine
                block = ""
                tblnum = 1
                crashcnt = 6
                if (line != null && line.length > 5) {

                    val obj = CsvData()
                    crashcnt = 1
                    for (i in 0..<line.length) {
                        val c = line.get(i)
                        crashcnt = 8
                        if (c == delim) {
                            when (tblnum) {
                                1 -> obj.bereich = block
                                2 -> obj.vers = block
                                3 -> obj.translation = block
                                4 -> obj.partText = block
                                5 -> obj.Res2 = block
                                6 -> obj.Text = insertLineEndings(block)
                            }
                            tblnum++
                            block = ""
                            crashcnt = 4
                        } else block = block + c
                    } //for
                    if (tblnum==6) obj.Text = insertLineEndings(block) //added 2025.08
                    crashcnt = 11
                    dataList.add(obj)
                }
            }
        } catch (e: Exception) {
            gc.Logl("BL nr " + crashcnt + " Msg: " + e.message, true)
        }
    }


    fun readFromAssets(csvFile: String, delim: String?) {
        val assetManager = gc.assets
        val `is`: InputStream?
        if (assetManager == null) {
            gc.Logl(" assetManager = Null!!", true)
            return
        }
        try {
            val files = assetManager.list("")
            gc.Logl( "Files: " + files.contentToString(), false)

            //files = assetManager.getLocales();
            // Log.d(LOG_TAG, "Files: " + Arrays.toString(files));
            //AssetFileDescriptor afd = assetManager.open(csvFile);
            `is` = assetManager.open(csvFile)
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            gc.Logl("openCrash File " + csvFile + "  Err: " + e.message, true)
            return
        }
        filename = csvFile
        buildList2(`is`, delim)


    }

    fun doLearnDataIdx(minus: Boolean) {
        //gc.LernData_Idx = LeIdx;
        gc.lernDataIdx = doDataIdx(minus, gc.lernDataIdx)

        getLernData(gc.lernDataIdx)
    }

    fun getLernData(idx: Int): CsvData {
        copyData(dataList[idx], gc.lernItem)
        //ne gc.LernItem = dataList[idx]
        gc.lernDataIdx = idx
        return gc.lernItem
    }

    fun doDataIdx(minus: Boolean, aIdx: Int): Int {
        var aIdx = aIdx
        if (minus) {
            aIdx--
            if (aIdx < 1) aIdx = dataList.size - 1
        } else {
            aIdx++
            if (aIdx > dataList.size - 1) aIdx = 1
        }

        return aIdx
    }

    fun copyData(fromData: CsvData, toData: CsvData) {
        toData.bereich = fromData.bereich
        toData.vers = fromData.vers
        toData.translation = fromData.translation
        toData.partText = fromData.partText
        toData.Res2 = fromData.Res2
        toData.Chapter = fromData.Chapter
        toData.Text = fromData.Text
        toData.NumBook = fromData.NumBook
        toData.NumChapter = fromData.NumChapter
        toData.NumVers = fromData.NumVers
    }
    fun getRandomText(): String {
        var str = "Es ist aber der Glaube eine feste Zuversicht dessen, was man hofft, und ein Nichtzweifeln an dem, was man nicht sieht."
        val count = dataList.size
        var idx: Int
        gc.Logl("data cnt: $count", false)
        if (count > 2) {
            var mIdx = gc.appVals().valueReadInt("randi_Idx", 0)
            brandi = !brandi

            idx = if (brandi) {
                1 + random.nextInt(count - 2)
            } else {
                mIdx++
                if (mIdx > count - 2) mIdx = 0
                idx = mIdx
                gc.appVals().valueWriteInt("randi_Idx", mIdx)
                idx
            }
            //gc.Logl("idx cnt: $idx", false)
            val data = dataList[idx]
            copyData(data, gc.lernItem)

            //gc.LernItem = dataList[idx]
            gc.lernDataIdx = idx
            str = gc.lernItem.Text
            val cdata = CsvData()
            copyData(data, cdata)
            gc.versHistory.addVers(cdata)
            //gc.log(gc.LernItem.Text)
        }
        gc.log(str)
        return str
    }

    private fun buildList2(inputStream: InputStream, delim: String?) {
        val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
        //gc.Logl("alive readAndInsert: ", true);
        var line: String?
        var st: StringTokenizer?
        dataList.clear()
        //gc.Logl("alive 22222  readAndInsert: ", true);
        try {
            reader.readLine() //first Header...
            // Bereich # Vers # Translation # Res1 #  Res2 # Text
            while ((reader.readLine().also { line = it }) != null) {
                st = StringTokenizer(line, delim)
                //st.countTokens();
                val obj = CsvData()
                obj.bereich = st.nextToken()
                obj.vers = st.nextToken()
                obj.translation = st.nextToken()
                if (st.hasMoreTokens()) obj.partText = st.nextToken()
                if (st.hasMoreTokens()) obj.Res2 = st.nextToken() //2x Res

                if (st.hasMoreTokens()) obj.Text = st.nextToken() else gc.Logl(
                    "No Text Zeile: " + dataList.size + " Vers " + obj.vers,
                    true
                )
                // gc.Logl( "Inhalt Text: " + obj.Text , false);
                dataList.add(obj)
            }
            reader.close()
            inputStream.close()
        } catch (e: Exception) {
            //e.printStackTrace();
            gc.Logl("readline err Zeile: " + dataList.size + " err " + e.message, true)
        }


        // gc.Logl("Ok Zeilen: "+dataList.size(), true);
    }

    private fun isInData(sw: String?, Dataidx: Int): Boolean {
        val data = dataList.get(Dataidx)
        if (data.bereich.lowercase(Locale.ROOT).contains(sw!!)) return true
        if (data.vers.lowercase(Locale.ROOT).contains(sw)) return true
        return data.Text.lowercase(Locale.ROOT).contains(sw)
    }

    fun findText(aTxt: String, startIdx: Int): Int {
        //sz=gc.csvList().dataList.size(), cnt=sz+2;
        val sz = dataList.size
        var cnt = sz + 2
        var idx = startIdx
        val sw = aTxt.lowercase(Locale.getDefault())
        while (cnt > 0) {
            cnt--
            if (idx > sz - 1) idx = 0
            if (isInData(sw, idx)) {
                return idx
            }
            idx++
        }
        return -1
    }

    fun hasBibleVers(aVers: String): Int {
        var aVers = aVers
        aVers = aVers.lowercase(Locale.getDefault())
        var idx = -1
        for (item in dataList) {
            idx++
            if (item.vers.lowercase(Locale.ROOT) == aVers) return idx
        }
        return idx
    }



}

