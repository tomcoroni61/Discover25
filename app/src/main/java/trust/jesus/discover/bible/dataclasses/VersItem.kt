package trust.jesus.discover.bible.dataclasses

data class VersItem(
    val vers: String,
    val text: String,
    var nVers: Int = 0, //nVers: Int,
    var nBook: Int = 0, //nBook: Int, nChapter: Int
    var nChapter: Int = 0,
    var selected: Boolean = false,
    var positon: Int = 0
)
