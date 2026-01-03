package trust.jesus.discover.dlg_data

import android.content.Context
import trust.jesus.discover.little.FixStuff.Filenames.Companion.lineBreak
import trust.jesus.discover.little.Globus
import java.util.Locale
import java.util.Random

//private val mContext: Context
class CsvList() {
    var dataList: ArrayList<CsvData> = ArrayList()

    private val random = Random()
    private val gc: Globus = Globus.getAppContext() as Globus
    private var brandi = false
    val themesList: MutableList<String> = ArrayList()

    fun doThemesList(ignore: String) {
        themesList.clear()
        val cnt = dataList.size
        if (cnt > 2) for (i in 1..<cnt) {
            val csvData = dataList[i]
            val curTh = csvData.bereich.trim()
            if (curTh.length > 3 && !themesList.contains(curTh) &&
                ( ignore.length < 3 || !curTh.contains(ignore, true)) )
                themesList.add(csvData.bereich)
        }
        themesList.sort()
    }
    /*
    fun doBereichListPopup() {
        themesList.clear()
        val cnt = gc.csvList()!!.dataList.size
        if (cnt > 2) for (i in 1..<cnt) {
            val csvData = gc.csvList()!!.dataList[i]
            csvData.bereich = csvData.bereich.trim()
            if (!themesList.contains(csvData.bereich)) themesList.add(csvData.bereich)
        }
        themesList.sort()

        if (listBereichPopupWindow != null) return

        listBereichPopupWindow = ListPopupWindow(
            gc
        )
        listBereichPopupWindow!!.setAdapter(
            ArrayAdapter(
                gc,
                R.layout.lpw_item, themesList
            )
        )
        listBereichPopupWindow!!.anchorView = binding.yedBereich
        listBereichPopupWindow!!.width = 300
        listBereichPopupWindow!!.height = 400
        val drawable = ContextCompat.getDrawable(this.requireContext(), R.drawable.back_dyn)
        listBereichPopupWindow?.setBackgroundDrawable( drawable)
        //ne listBereichPopupWindow?.background = drawable falschplatz back_dyn richtig
        listBereichPopupWindow!!.isModal = false
        listBereichPopupWindow!!.setOnItemClickListener(this) //setOnClickListener


        binding.yedBereich.onFocusChangeListener = OnFocusChangeListener { view: View?, b: Boolean ->
            //if (binding.yedBereich == null) return @setOnFocusChangeListener
            if (view?.isFocused == true) {
                Timhandl.postDelayed(runPopupList, 1111)
            }
        }
    }
    */
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
    private val sepRepString = "SeparatorInTextReplaceHolder"
    private fun sepToHolder(value: String, separator: Char): String {
        return value.replace(separator.toString(), sepRepString)
    }

    private fun holderToSep(value: String): String {
        return value.replace(sepRepString, lineBreak)
    }
    fun saveToPrivate(csvFile: String?, separator: Char) {
        if (!gc.dateien().openOutputStream(csvFile, Context.MODE_PRIVATE)) return
        var line: String? //Thema # Vers # Translation # Res1 # Res2 # Text
        for (item in dataList) {
            line = sepToHolder(item.bereich, separator) + separator + sepToHolder(item.vers, separator) +
                    separator + sepToHolder(item.translation, separator) + separator +
                    sepToHolder(item.partText, separator) + separator + item.Res2 + separator + //item.Text
                    removeLineEndings(sepToHolder(item.Text, separator))
            if (!gc.dateien().writeLine(line)) break
        }
        gc.dateien().closeOutputStream()
    }

    //var seperator = '|'
    private fun buildList(separatorChar: Char) { //readFromPrivate
        //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        //gc.Logl("alive readAndInsert: ", true);
        dataList.clear()
        var line = ""
        var block = " "
        var tblnum: Int;        var crashcnt = 0;   //var lineCount=0 seperator = delim

        /* 12.25  data mismatch android-windows edit -android new user
        !! used two different buildList !!
        lineCount++
                when (lineCount) {
                    1 -> if (line.length == 1) seperator = line[0]
                }
         */
        try {
            while (gc.dateien().readLine()) {
                line = gc.dateien().rLine.toString();
                block = ""
                tblnum = 1
                crashcnt = 6
                if (line.length > 5) {

                    val obj = CsvData()
                    crashcnt = 1
                    for (i in 0..<line.length) {
                        val c = line[i]
                        crashcnt = 8
                        if (c == separatorChar) {
                            block = holderToSep(block)
                            crashcnt = 9
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
                        } else block += c
                    } //for
                    if (tblnum==6) obj.Text = insertLineEndings(block) //added 2025.08
                    crashcnt = 11
                    dataList.add(obj)
                }
            }
        } catch (e: Exception) {
            gc.crashLog("BL nr " + crashcnt + " Msg: " + e.message, 120)
        }
    }



    fun doLearnDataIdx(minus: Boolean) {
        //gc.LernData_Idx = LeIdx;
        gc.lernDataIdx = doDataIdx(minus, gc.lernDataIdx)

        getLernData(gc.lernDataIdx)
    }

    fun getLernData(idx: Int) {
        gc.lernItem.setLernData(idx, dataList[idx])

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
        //gc.Logl("data cnt: $count", false)
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
            gc.lernItem.setLernData(idx, data)

            str = gc.lernItem.text
            val cdata = CsvData()
            copyData(data, cdata)
            gc.versHistory.addVers(cdata)
            //gc.log(gc.LernItem.Text)
        }
        gc.log(str)
        return str
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

    fun hasDataText(aText: String): Boolean {
        for (item in dataList) {
            if (item.Text == aText) return true
        }
        return false
    }

}

