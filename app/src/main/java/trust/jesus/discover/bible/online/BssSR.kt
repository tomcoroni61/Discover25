package trust.jesus.discover.bible.online


data class BssSR (
    val id: Int,
    val book: Int,
    val chapter: Int,
    val verse: Int,
    var text: String,
    val italics: String,
    val claimed: Boolean
)