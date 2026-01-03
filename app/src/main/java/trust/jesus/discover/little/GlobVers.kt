package trust.jesus.discover.little

import trust.jesus.discover.bible.dataclasses.VersItem
import trust.jesus.discover.bible.online.BollsSR
import trust.jesus.discover.bible.online.BollsVers
import trust.jesus.discover.dlg_data.CsvData

class GlobVers {
    //val globi(): Globus = Globus.Companion.getAppContext() as Globus

    var bereich: String = "?"
    var vers: String = "?"
    var translation: String = "?"
    var bollsVersion: String = "?"
    var partText: String = ""
    val chapter = mutableListOf<VersItem?>() // bssArray = bssArray.plus(bssR)
    //var chapter = ""
    var text: String = "null"
    var numVersStart: Int = 0;    var numBook: Int = 1;    var numChapter: Int = 1
    var numVersEnd = 0
    private var gs: Globus? = null
    private var bookNameFound = true

    fun globi(): Globus {
        if (gs == null) gs = Globus.getAppContext() as Globus
        return gs!!
    }

    fun setVersTitel() {
        var versTitel = vers
        if (bookNameFound) {
            versTitel = globi().bBlparseBook()!!.versShortName(numBook, numChapter, numVersStart)
            if (numVersEnd>0)
                versTitel += "-$numVersEnd"
            versTitel += "  " + globi().bolls()!!.bibelVersionNameToShort(translation)
        }
        globi().mainActivity!!.binding.tvVersTop.text = versTitel
    }
    fun setLernData(idx: Int, csvData: CsvData) {
        bereich = csvData.bereich
        vers = csvData.vers
        translation = csvData.translation
        partText = csvData.partText
        text = csvData.Text
        numVersEnd = 0
        //globi().log("setLernData: $text")

        globi().lernDataIdx = idx
        bookNameFound = true
        partText = "";        chapter.clear()
        val vp = globi().bBlparseBook()!!.parse(vers)
        if (vp.bookNumber == 0) {
            vp.bookNumber = 17
            bookNameFound = false
            if (vp.chapter==0) vp.chapter = 1
            if (vp.startVerse==0) vp.startVerse = 1
        }
        numBook = vp.bookNumber
        numChapter = vp.chapter
        numVersStart = vp.startVerse
        if (vp.endVerse !=null)
            numVersEnd = vp.endVerse
        val version = translation.uppercase().trim()//Locale.getDefault()
        //log("checkLernItemForBolls: $version")
        bollsVersion = globi().bolls()!!.getNearBollsVersion(version)

    }

    fun setBollsSearchResult(verses:  Array<BollsVers?>?, text: String, version: String, nVers: Int, nBook: Int, nChapter: Int) {
        var cnt = 1
        chapter.clear()
        if (verses != null) {
            for (verse in verses) {
                chapter.add(VersItem(verse!!.text, cnt.toString() + " " + verse.text,
                    cnt, nBook, nChapter))
                cnt ++
            }
        }

        this.text = text;        translation = version;        bollsVersion = version
        numVersStart = nVers;        numBook = nBook;        numChapter = nChapter

        vers = globi().bBlparseBook()!!.versShortName(nBook, nChapter, nVers)
        partText = "ne"
    }
    fun addToHistory() {
        val cdata = CsvData()
        cdata.bereich = bereich
        cdata.vers = vers
        cdata.translation = translation
        cdata.partText = partText
        cdata.Text = text
        //cdata.Chapter = chapter
        cdata.NumBook = numBook
        cdata.NumChapter = numChapter
        cdata.NumVers = numVersStart

        globi().versHistory.addVers(cdata)
    }
    fun setCurHistory(): Boolean {
        val vers = globi().versHistory.currentVers()
        if (vers != null) {
            setLernData(-1, vers)
            //globi().setVersTitel(vers.vers)
            setVersTitel()
            return true
        } else return false
    }
    fun setBollsSrVers(jvers: BollsSR?) {
        if (jvers == null) return
        vers =
            globi().bBlparseBook()!!.versShortName(jvers.book, jvers.chapter, jvers.verse)
        translation = jvers.translation
        numVersStart = jvers.verse
        numBook = jvers.book
        numChapter = jvers.chapter
        text = jvers.text
    }//BollsVers
    //fun setBollsVers(jvers: BollsVers) use setBollsSrVers

    fun setSeekVers(vers: SeekList.SeekData?) {
        if (vers == null) return
        this.vers = globi().bBlparseBook()!!.versShortName(vers.numBook, vers.numChapter, vers.numVers)
        translation = vers.translation
        numVersStart = vers.numVers
        numBook = vers.numBook
        numChapter = vers.numChapter
        text = vers.text
        partText = "ne"
        chapter.clear()
    }
    fun tocsvData(): CsvData {
        val csvData = CsvData()
        csvData.bereich = bereich
        csvData.vers = vers
        csvData.translation = translation
        csvData.partText = partText
        csvData.Text = text
        //csvData.Chapter = chapter
        csvData.NumBook = numBook
        csvData.NumChapter = numChapter
        csvData.NumVers = numVersStart
        return csvData
    }
}