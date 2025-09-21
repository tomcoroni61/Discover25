package trust.jesus.discover.bible.online

data class BollsSR ( //= SearchResult

        val pk: Int,
        val translation: String,
        val book: Int,
        val chapter: Int,
        val verse: Int,
        var text: String,
        //var gotit: Boolean = false
    )

//"pk": 27719, "translation": "YLT", "book": 4, "chapter": 26, "verse": 15,
//"text":