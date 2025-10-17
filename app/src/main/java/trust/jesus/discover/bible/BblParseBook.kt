package trust.jesus.discover.bible

import trust.jesus.discover.little.Globus
import java.util.Locale.getDefault
import kotlin.random.Random
import kotlin.random.nextInt

class BblParseBook {
//most from https://github.com/nehemiaharchives/bbl
    //private val gc: Globus = Globus.getAppContext() as Globus
    data class BookChapterFilter(
        val book: Int? = null,
        val startChapter: Int? = null,
        val endChapter: Int? = null,
        val term: String
    )

    fun filterByBookChapter(term: String): BookChapterFilter {

        bookNameNumberArray.forEachIndexed { bookNumber, bookNames ->
            bookNames.forEach { bookName ->
                if (term.endsWith("in $bookName")) {
                    val trimmed = term.replace(" in $bookName", "").trim()
                    return BookChapterFilter(book = bookNumber, term = trimmed)
                }
            }
        }

        bookNameNumberArray.forEachIndexed { bookNumber, bookNames ->
            bookNames.forEach { bookName ->
                if (term.matches(".+ in $bookName ([1-9]|[1-9][0-9]|1[0-5][0-9])$".toRegex())) {

                    val detectedChapter = term.split("in $bookName ")[1].toInt()
                    val trimmed = term.replace(" in $bookName ([1-9]|[1-9][0-9]|1[0-5][0-9])\$".toRegex(), "").trim()

                    return BookChapterFilter(book = bookNumber, startChapter = detectedChapter, term = trimmed)
                }
            }
        }

        bookNameNumberArray.forEachIndexed { bookNumber, bookNames ->
            bookNames.forEach { bookName ->
                if (term.matches(".+ in $bookName ([1-9]|[1-9][0-9]|1[0-5][0-9])-([1-9]|[1-9][0-9]|1[0-5][0-9])$".toRegex())) {
                    val detectedChapters = term.split("in $bookName ")[1].split("-")

                    val trimmed = term.replace(
                        " in $bookName ([1-9]|[1-9][0-9]|1[0-5][0-9])-([1-9]|[1-9][0-9]|1[0-5][0-9])\$".toRegex(),
                        ""
                    ).trim()

                    return BookChapterFilter(
                        book = bookNumber,
                        startChapter = detectedChapters[0].toInt(),
                        endChapter = detectedChapters[1].toInt(),
                        term = trimmed
                    )
                }
            }
        }

        return BookChapterFilter(term = term)
    }

    val bookNameNumberArray = arrayOf(
        emptyArray(), //no book name for book number 0
        arrayOf("genesis", "gen", "ge", "gn"), // index 1 as book 1
        arrayOf("exodus", "ex", "exod", "exo"),
        arrayOf("leviticus", "lev", "le", "lv"),
        arrayOf("numbers", "num", "nu", "nm", "nb"),
        arrayOf("deuteronomy", "deut", "de", "dt"),
        arrayOf("joshua", "josh", "jos", "jsh"),
        arrayOf("judges", "judg", "jdg", "jg", "jdgs"),
        arrayOf("ruth", "rth", "ru"),
        arrayOf(
            "1st samuel",
            "1 sam",
            "1sam",
            "1sm",
            "1sa",
            "1s",
            "1 samuel",
            "1samuel",
            "1st sam",
            "first samuel",
            "first sam"
        ),
        arrayOf(
            "2nd samuel",
            "2 sam",
            "2sam",
            "2sm",
            "2sa",
            "2s",
            "2 samuel",
            "2ndsam",
            "2nd sam",
            "second samuel",
            "second sam"
        ),
        arrayOf("1st kings", "1kings", "1 kings", "1kgs", "1 kgs", "1ki", "1k", "1stkgs", "first kings", "first kgs"),
        arrayOf("2nd kings", "2kings", "2 kings", "2kgs", "2 kgs", "2ki", "2k", "2ndkgs", "second kings", "second kgs"),
        arrayOf(
            "1st chronicles",
            "1chronicles",
            "1 chronicles",
            "1chr",
            "1 chr",
            "1ch",
            "1stchr",
            "1st chr",
            "first chronicles",
            "first chr"
        ),
        arrayOf(
            "2nd chronicles",
            "2chronicles",
            "2 chronicles",
            "2chr",
            "2 chr",
            "2ch",
            "2ndchr",
            "2nd chr",
            "second chronicles",
            "second chr"
        ),
        arrayOf("ezra", "ezr", "ez"),
        arrayOf("nehemiah", "neh", "ne"),
        arrayOf("esther", "est", "esth", "es"),
        arrayOf("job", "jb"),
        arrayOf("psalms", "ps", "psalm", "pslm", "psa", "psm", "pss"),
        arrayOf("proverbs", "prov", "pro", "prv", "pr"),
        arrayOf("ecclesiastes", "eccles", "eccle", "ecc", "ec", "qoh"),
        arrayOf("song of solomon", "song", "song of songs", "sos", "so", "canticle of canticles", "canticles", "cant"),
        arrayOf("isaiah", "isa", "is"),
        arrayOf("jeremiah", "jer", "je", "jr"),
        arrayOf("lamentations", "lam", "la"),
        arrayOf("ezekiel", "ezek", "eze", "ezk"),
        arrayOf("daniel", "dan", "da", "dn"),
        arrayOf("hosea", "hos", "ho"),
        arrayOf("joel", "jl"),
        arrayOf("amos", "am"),
        arrayOf("obadiah", "obad", "ob"),
        arrayOf("jonah", "jnh", "jon"),
        arrayOf("micah", "mic", "mc"),
        arrayOf("nahum", "nah", "na"),
        arrayOf("habakkuk", "hab", "hb"),
        arrayOf("zephaniah", "zeph", "zep", "zp"),
        arrayOf("haggai", "hag", "hg"),
        arrayOf("zechariah", "zech", "zec", "zc"),
        arrayOf("malachi", "mal", "ml"),
        arrayOf("matthew", "matt", "mt"),
        arrayOf("mark", "mrk", "mar", "mk", "mr"),
        arrayOf("luke", "luk", "lk"),
        arrayOf("john", "joh", "jhn", "jn"),
        arrayOf("acts", "act", "ac"),
        arrayOf("romans", "rom", "ro", "rm"),
        arrayOf("1 corinthians", "1corinthians", "1 cor", "1cor", "1 co", "1co", "1st corinthians", "first corinthians"),
        arrayOf("2 corinthians", "2corinthians", "2 cor", "2cor", "2 co", "2co", "2nd corinthians", "second corinthians"),
        arrayOf("galatians", "gal", "ga"),
        arrayOf("ephesians", "eph", "ephes"),
        arrayOf("philippians", "phil", "php", "pp"),
        arrayOf("colossians", "col", "co"),
        arrayOf(
            "1 thessalonians",
            "1thessalonians",
            "1 thess",
            "1thess",
            "1 thes",
            "1thes",
            "1 th",
            "1th",
            "1st thessalonians",
            "1st thess",
            "first thessalonians",
            "first thess"
        ),
        arrayOf(
            "2 thessalonians",
            "2thessalonians",
            "2 thess",
            "2thess",
            "2 thes",
            "2thes",
            "2 th",
            "2th",
            "2nd thessalonians",
            "2nd thess",
            "second thessalonians",
            "second thess"
        ),
        arrayOf(
            "1 timothy",
            "1timothy",
            "1 tim",
            "1tim",
            "1 ti",
            "1ti",
            "1st timothy",
            "1st tim",
            "first timothy",
            "first tim"
        ),
        arrayOf(
            "2 timothy",
            "2timothy",
            "2 tim",
            "2tim",
            "2 ti",
            "2ti",
            "2nd timothy",
            "2nd tim",
            "second timothy",
            "second tim"
        ),
        arrayOf("titus", "tit", "ti"),
        arrayOf("philemon", "philem", "phm", "pm"),
        arrayOf("hebrews", "heb"),
        arrayOf("james", "jas", "jm"),
        arrayOf("1 peter", "1peter", "1 pet", "1pet", "1 pe", "1pe", "1 pt", "1pt", "1p", "1st peter", "first peter"),
        arrayOf("2 peter", "2peter", "2 pet", "2pet", "2 pe", "2pe", "2 pt", "2pt", "2p", "2nd peter", "second peter"),
        arrayOf("1 john", "1john", "1 jhn", "1jhn", "1 jn", "1jn", "1j", "1st john", "first john"),
        arrayOf("2 john", "2john", "2 jhn", "2jhn", "2 jn", "2jn", "2j", "2nd john", "second john"),
        arrayOf("3 john", "3john", "3 jhn", "3jhn", "3 jn", "3jn", "3j", "3rd  john", "third john"),
        arrayOf("jude", "jud", "jd"),
        arrayOf("revelation", "rev", "re", "the revelation"),
    )

    fun bookNumber(bookName: String) = when (bookName.lowercase()) {
        "genesis", "gen", "ge", "gn", "1mo" -> 1
        "exodus", "ex", "exod", "exo", "2mo" -> 2
        "leviticus", "lev", "le", "lv", "3mo" -> 3
        "numbers", "num", "nu", "nm", "nb", "4mo" -> 4
        "deuteronomy", "deut", "de", "dt", "5mo" -> 5
        "joshua", "josh", "jos", "jsh" -> 6
        "judges", "judg", "jdg", "jg", "jdgs", "ri", "richter" -> 7
        "ruth", "rth", "ru", "rut" -> 8
        "1st samuel", "1 sam", "1sam", "1sm", "1sa", "1s", "1 samuel", "1samuel", "1st sam", "first samuel", "first sam" -> 9
        "2nd samuel", "2 sam", "2sam", "2sm", "2sa", "2s", "2 samuel", "2ndsam", "2nd sam", "second samuel", "second sam" -> 10
        "1kön", "1st kings", "1kings", "1 kings", "1kgs", "1 kgs", "1 ki", "1k", "1stkgs", "first kings", "first kgs", "1kö", "1 kö" -> 11
        "2kön", "2nd kings", "2kings", "2 kings", "2kgs", "2 kgs", "2 ki", "2k", "2ndkgs", "second kings", "second kgs", "2kö", "2 kö" -> 12
        "1st chronicles", "1chronicles", "1 chronicles", "1chr", "1 chr", "1 ch", "1stchr", "1st chr", "first chronicles", "first chr" -> 13
        "2nd chronicles", "2chronicles", "2 chronicles", "2chr", "2 chr", "2 ch", "2ndchr", "2nd chr", "second chronicles", "second chr" -> 14
        "ezra", "ezr", "ez", "esra", "esr" -> 15
        "nehemiah", "neh", "ne" -> 16
        "esther", "est", "esth", "es" -> 17
        "job", "jb", "hiob" -> 18
        "psalms", "ps", "psalm", "pslm", "psa", "psm", "pss" -> 19
        "proverbs", "prov", "pro", "prv", "pr", "spr", "sprüche" -> 20
        "ecclesiastes", "eccles", "eccle", "ecc", "ec", "qoh", "pre", "pred", "prediger" -> 21
        "hohe lied", "hld", "lied", "song of solomon", "song", "song of songs", "sos", "so", "canticle of canticles", "canticles", "cant" -> 22
        "isaiah", "isa", "is", "jes", "jesa" -> 23
        "jeremiah", "jer", "je", "jr" -> 24
        "lamentations", "lam", "la", "kla", "klgl" -> 25
        "ezekiel", "ezek", "eze", "ezk", "hes" -> 26
        "daniel", "dan", "da", "dn" -> 27
        "hosea", "hos", "ho" -> 28
        "joel", "jl" -> 29
        "amos", "am" -> 30
        "obadiah", "obad", "ob", "obd" -> 31
        "jonah", "jnh", "jon" -> 32
        "micah", "mic", "mc", "mi" -> 33
        "nahum", "nah", "na" -> 34
        "habakkuk", "hab", "hb" -> 35
        "zephaniah", "zeph", "zep", "zp", "zef" -> 36
        "haggai", "hag", "hg" -> 37
        "zechariah", "zech", "zec", "zc", "sach" -> 38
        "malachi", "mal", "ml" -> 39
        "matthew", "matt", "mt", "mat" -> 40
        "mark", "mrk", "mar", "mk", "mr" -> 41
        "luke", "luk", "lk" -> 42
        "john", "joh", "jhn", "jn" -> 43
        "acts", "act", "ac", "apg" -> 44
        "romans", "rom", "ro", "rm", "röm" -> 45
        "1 corinthians", "1corinthians", "1 cor", "1cor", "1kor", "1 co", "1co", "1st corinthians", "first corinthians" -> 46
        "2 corinthians", "2corinthians", "2 cor", "2cor", "2kor", "2 co", "2co", "2nd corinthians", "second corinthians" -> 47
        "galatians", "gal", "ga" -> 48
        "ephesians", "eph", "ephes" -> 49
        "philippians", "phil", "php", "pp" -> 50
        "colossians", "col", "co", "ko", "kol" -> 51
        "1 thessalonians", "1thessalonians", "1 thess", "1thess", "1 thes", "1thes", "1 th", "1th", "1st thessalonians", "1st thess", "first thessalonians", "first thess" -> 52
        "2 thessalonians", "2thessalonians", "2 thess", "2thess", "2 thes", "2thes", "2 th", "2th", "2nd thessalonians", "2nd thess", "second thessalonians", "second thess" -> 53
        "1 timothy", "1timothy", "1 tim", "1tim", "1 ti", "1ti", "1st timothy", "1st tim", "first timothy", "first tim" -> 54
        "2 timothy", "2timothy", "2 tim", "2tim", "2 ti", "2ti", "2nd timothy", "2nd tim", "second timothy", "second tim" -> 55
        "titus", "tit", "ti" -> 56
        "philemon", "philem", "phm", "pm", "phlm" -> 57
        "hebrews", "heb", "hebr" -> 58
        "james", "jas", "jm", "jak" -> 59
        "1 peter", "1peter", "1 pet", "1pet", "1 pe", "1pe", "1 pt", "1pt", "1p", "1st peter", "first peter" -> 60
        "2 peter", "2peter", "2 pet", "2pet", "2 pe", "2pe", "2 pt", "2pt", "2p", "2nd peter", "second peter" -> 61
        "1 john", "1 joh", "1joh", "1john", "1 jhn", "1jhn", "1 jn", "1jn", "1j", "1st john", "first john" -> 62
        "2 john", "2 joh", "2joh", "2john", "2 jhn", "2jhn", "2 jn", "2jn", "2j", "2nd john", "second john" -> 63
        "3 john", "3 joh", "3joh", "3john", "3 jhn", "3jhn", "3 jn", "3jn", "3j", "3rd  john", "third john" -> 64
        "jude", "jud", "jd" -> 65
        "revelation", "rev", "re", "the revelation", "offb" -> 66
        else -> 0 //throw Exception("book '$bookName' not found in the list of book names")
    }

    val numberToName = mapOf(
        1 to "genesis",
        2 to "exodus",
        3 to "leviticus",
        4 to "numbers",
        5 to "deuteronomy",
        6 to "joshua",
        7 to "judges",
        8 to "ruth",
        9 to "1st samuel",
        10 to "2nd samuel",
        11 to "1st kings",
        12 to "2nd kings",
        13 to "1st chronicles",
        14 to "2nd chronicles",
        15 to "ezra",
        16 to "nehemiah",
        17 to "esther",
        18 to "job",
        19 to "psalms",
        20 to "proverbs",
        21 to "ecclesiastes",
        22 to "song of solomon",
        23 to "isaiah",
        24 to "jeremiah",
        25 to "lamentations",
        26 to "ezekiel",
        27 to "daniel",
        28 to "hosea",
        29 to "joel",
        30 to "amos",
        31 to "obadiah",
        32 to "jonah",
        33 to "micah",
        34 to "nahum",
        35 to "habakkuk",
        36 to "zephaniah",
        37 to "haggai",
        38 to "zechariah",
        39 to "malachi",
        40 to "matthew",
        41 to "mark",
        42 to "luke",
        43 to "john",
        44 to "acts",
        45 to "romans",
        46 to "1 corinthians",
        47 to "2 corinthians",
        48 to "galatians",
        49 to "ephesians",
        50 to "philippians",
        51 to "colossians",
        52 to "1 thessalonians",
        53 to "2 thessalonians",
        54 to "1 timothy",
        55 to "2 timothy",
        56 to "titus",
        57 to "philemon",
        58 to "hebrews",
        59 to "james",
        60 to "1 peter",
        61 to "2 peter",
        62 to "1 john",
        63 to "2 john",
        64 to "3 john",
        65 to "jude",
        66 to "revelation"
    )

    private val numberToShortName = mapOf(
        1 to "gen",
        2 to "ex",
        3 to "lev",
        4 to "num",
        5 to "deut",
        6 to "joshua",
        7 to "judges",
        8 to "ruth",
        9 to "1 sam",
        10 to "2 sam",
        11 to "1 ki",
        12 to "2 ki",
        13 to "1 ch",
        14 to "2 ch",
        15 to "ezra",
        16 to "neh",
        17 to "esther",
        18 to "job",
        19 to "psalms",
        20 to "prov",
        21 to "ecc",
        22 to "song",
        23 to "isa",
        24 to "jer",
        25 to "lam",
        26 to "ezekiel",
        27 to "daniel",
        28 to "hosea",
        29 to "joel",
        30 to "amos",
        31 to "obad",
        32 to "jonah",
        33 to "micah",
        34 to "nahum",
        35 to "hab",
        36 to "zeph",
        37 to "haggai",
        38 to "zech",
        39 to "mal",
        40 to "mat",
        41 to "mark",
        42 to "luke",
        43 to "john",
        44 to "acts",
        45 to "rom",
        46 to "1 cor",
        47 to "2 cor",
        48 to "gal",
        49 to "eph",
        50 to "phil",
        51 to "col",
        52 to "1 thes",
        53 to "2 thes",
        54 to "1 tim",
        55 to "2 tim",
        56 to "tit",
        57 to "phile",
        58 to "hebr",
        59 to "james",
        60 to "1 pe",
        61 to "2 pe",
        62 to "1 john",
        63 to "2 john",
        64 to "3 john",
        65 to "jude",
        66 to "rev"
    )

    fun bookName(bookNumber: Int) = numberToName[bookNumber]

    data class VersePointer(
        var book: Int = 0,
        var chapter: Int = 0,
        var startVerse:Int = 0,
        val endVerse: Int? = null
    )

    fun versShortName(bookNumber: Int, chapter: Int, verse: Int) = numberToShortName[bookNumber] + " " + chapter + ":" + verse

    fun bookNameCapital(bookNumber: Int): String {
        val lowerCase = bookName(bookNumber)!!
        val split = lowerCase.split(" ")
        return when (split.size) {
            1 -> lowerCase.replaceFirstChar { it.uppercase() }
            2 -> "${split[0]} ${split[1].replaceFirstChar { it.uppercase() }}"
            3 -> "Song of Solomon"
            else -> throw RuntimeException("book name must be less than 3 words")
        }
    }

    fun parse(bibelStelle: String): VersePointer {
        //gc.Logl("parse bibelStelle: $bibelStelle", true)
        //john 3:16  or john 3:16-18  or john 1
        var bookString = bibelStelle.lowercase(getDefault()).trim()
        val delIdx = bookString.indexOf(":")
        var chapterVerse = "1:1"
        if (delIdx > 3) {
            val startIdi = bookString.lastIndexOf(" ")
            if (startIdi > 1) {
                chapterVerse = bookString.substring(startIdi + 1)
                bookString = bookString.substring(0, startIdi).trim()
            }
        }
        val bookNumber = bookNumber(bookString)

        val chapterVerseSplit = chapterVerse.split(":")

        val chapterNumber = chapterVerseSplit[0].toInt()

        val startVerse = if (chapterVerseSplit.size == 2) chapterVerseSplit[1].split("-")[0].toInt() else 0

        val endVerse =
            if (chapterVerseSplit.size == 2 && chapterVerse.contains("-")) chapterVerseSplit[1].split("-")[1].toInt() else null

        return VersePointer(
            book = bookNumber,
            chapter = chapterNumber,
            startVerse = startVerse,
            endVerse = endVerse
        )
    }

    fun splitChapterToVerses(aChapter: String): Array<String> {
        return aChapter.substring(2).split("\\n\\d{1,3} ".toRegex()).toTypedArray()
    }
    fun selectVerses(versePointer: VersePointer, aChapter: String): String {

        val start = versePointer.startVerse
        val end = versePointer.endVerse

        var selected = aChapter

        if (start != null) {

            val verses = splitChapterToVerses(aChapter)

            if (end == null) {
                selected = start.toString() + " " + verses[start - 1]
            } else {
                val list = mutableListOf<String>()

                (start..end).forEach { verseNumber ->
                    list.add(verseNumber.toString() + " " + verses[verseNumber - 1])
                }

                selected = list.joinToString("\n")
            }
        }

        return selected
    }

    fun chapterTextPath(versePointer: VersePointer) =
       // "texts/${versePointer.translation}/${versePointer.translation}.${versePointer.book}.${versePointer.chapter}.txt"
        "texts/${versePointer.book}.${versePointer.chapter}.txt"

    fun randomBookAndChapter(fromNtOtxx: String?): VersePointer {
        val randomBook = when (fromNtOtxx) {
            "Ot", "ot", "ot verse", "ot chapter" -> Random.nextInt(1..39)
            "Nt", "nt", "nt verse", "nt chapter" -> Random.nextInt(40..66)
            "g", "g verse", "g chapter" -> Random.nextInt(40..43)
            "all" -> Random.nextInt(1..66)
            else -> bookNumber(fromNtOtxx!!.lowercase())
        }
        //logger.debug("randomBook: $randomBook")
        val randomChapter = Random.nextInt(1..BblChapters.maxChapter(randomBook))
        //logger.debug("randomChapter: $randomChapter")
        val versePointer = VersePointer(
            //translation = config.translation,
            book = randomBook,
            chapter = randomChapter
        )

        return versePointer
    }
}