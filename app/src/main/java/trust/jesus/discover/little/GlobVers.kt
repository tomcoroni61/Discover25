package trust.jesus.discover.little

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

    var chapter = ""
    var text: String = "null"
    var numVers: Int = 0
    var numBook: Int = 1
    var numChapter: Int = 1

    private var gs: Globus? = null

    fun globi(): Globus {
        if (gs == null) gs = Globus.getAppContext() as Globus
        return gs!!
    }
    
    fun setLernData(idx: Int, csvData: CsvData) {
        bereich = csvData.bereich
        vers = csvData.vers
        translation = csvData.translation
        partText = csvData.partText
        text = csvData.Text
        globi().log("setLernData: $text")

        globi().lernDataIdx = idx

        partText = "";        chapter = "";
        val vp = globi().bBlparseBook()!!.parse(vers)
        if (vp.book == 0) {
            vp.book = 17
            vp.chapter = 1
            vp.startVerse = 1
        }
        numBook = vp.book
        numChapter = vp.chapter
        numVers = vp.startVerse
        val version = translation.uppercase().trim()//Locale.getDefault()
        //log("checkLernItemForBolls: $version")
        bollsVersion = globi().bolls()!!.getNearBollsVersion(version)

    }

    fun setBollsSearchResult(chapter: String, text: String, version: String, nVers: Int, nBook: Int, nChapter: Int) {
        this.chapter = chapter
        this.text = text;        translation = version;        bollsVersion = version
        numVers = nVers;        numBook = nBook;        numChapter = nChapter

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
        cdata.Chapter = chapter
        cdata.NumBook = numBook
        cdata.NumChapter = numChapter
        cdata.NumVers = numVers

        globi().versHistory.addVers(cdata)
    }
    fun setCurHistory(): Boolean {
        val vers = globi().versHistory.currentVers()
        if (vers != null) {
            setLernData(-1, vers)
            return true
        } else return false
    }
    fun setBollsSrVers(jvers: BollsSR?) {
        if (jvers == null) return
        vers =
            globi().bBlparseBook()!!.versShortName(jvers.book, jvers.chapter, jvers.verse)
        translation = jvers.translation
        numVers = jvers.verse
        numBook = jvers.book
        numChapter = jvers.chapter
        text = jvers.text
    }
    fun setSeekVers(vers: SeekList.SeekData?) {
        if (vers == null) return

        this.vers = globi().bBlparseBook()!!.versShortName(vers.numBook, vers.numChapter, vers.numVers)
        translation = vers.translation
        numVers = vers.numVers
        numBook = vers.numBook
        numChapter = vers.numChapter
        text = vers.text
        partText = "ne"
    }
    fun tocsvData(): CsvData {
        val csvData = CsvData()
        csvData.bereich = bereich
        csvData.vers = vers
        csvData.translation = translation
        csvData.partText = partText
        csvData.Text = text
        csvData.Chapter = chapter
        csvData.NumBook = numBook
        csvData.NumChapter = numChapter
        csvData.NumVers = numVers
        return csvData
    }
}