package trust.jesus.discover.bible

import java.util.Locale
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

    val numberToDeName = mapOf(
        1 to "1. Mose",
        2 to "2. Mose",
        3 to "3. Mose",
        4 to "4. Mose",
        5 to "5. Mose",
        6 to "Josua",
        7 to "Richter",
        8 to "Rut",
        9 to "1. samuel",
        10 to "2. samuel",
        11 to "1. Könige",
        12 to "2. Könige",
        13 to "1. Chroniken",
        14 to "2. Chroniken",
        15 to "esra",
        16 to "nehemiah",
        17 to "ester",
        18 to "hiob",
        19 to "psalm",
        20 to "sprüche",
        21 to "Prediger",
        22 to "Das hohe Lied",
        23 to "jesaja",
        24 to "jeremia",
        25 to "klagelieder",
        26 to "hesekiel",
        27 to "daniel",
        28 to "hosea",
        29 to "joel",
        30 to "amos",
        31 to "obadia",
        32 to "jona",
        33 to "micha",
        34 to "nahum",
        35 to "habakuk",
        36 to "zefanja",
        37 to "haggai",
        38 to "sachaja",
        39 to "maleachi",
        40 to "matthäus",
        41 to "markus",
        42 to "lukas",
        43 to "johannes",
        44 to "apostelgeschichte",
        45 to "römer",
        46 to "1. Korinther",
        47 to "2. Korinther",
        48 to "galater",
        49 to "epheser",
        50 to "philipper",
        51 to "kolosser",
        52 to "1. thessalonicher",
        53 to "2. thessalonicher",
        54 to "1. timotheus",
        55 to "2. timotheus",
        56 to "titus",
        57 to "philemon",
        58 to "hebräer",
        59 to "jakobus",
        60 to "1. petrus",
        61 to "2. petrus",
        62 to "1. johannes",
        63 to "2. johannes",
        64 to "3. johannes",
        65 to "judas",
        66 to "offenbarung"
    )

    val bookNumberToShortEnName = mapOf(
        1 to "Gen",
        2 to "Ex",
        3 to "Lev",
        4 to "Num",
        5 to "Deu",
        6 to "Jos",
        7 to "Judg",
        8 to "Rut",
        9 to "1Sa",
        10 to "2Sa",
        11 to "1Ki",
        12 to "2Ki",
        13 to "1Ch",
        14 to "2Ch",
        15 to "Ezr",
        16 to "Neh",
        17 to "Est",
        18 to "Job",
        19 to "Psa",
        20 to "Pro",
        21 to "Ecc",
        22 to "Song",
        23 to "Isa",
        24 to "Jer",
        25 to "Lam",
        26 to "Eze",
        27 to "Dan",
        28 to "Hos",
        29 to "Joe",
        30 to "Amo",
        31 to "Obd",
        32 to "Jon",
        33 to "Mic",
        34 to "Nah",
        35 to "Hab",
        36 to "Zep",
        37 to "Hag",
        38 to "Zec",
        39 to "Mal",
        40 to "Mat",
        41 to "Mar",
        42 to "Luk",
        43 to "Joh",
        44 to "Act",
        45 to "Rom",
        46 to "1Cor",
        47 to "2Cor",
        48 to "Gal",
        49 to "Eph",
        50 to "Phili",
        51 to "Col",
        52 to "1Th",
        53 to "2Th",
        54 to "1Tim",
        55 to "2Tim",
        56 to "Tit",
        57 to "Phile",
        58 to "Heb",
        59 to "Jam",
        60 to "1 Pe",
        61 to "2 Pe",
        62 to "1 Jo",
        63 to "2 Jo",
        64 to "3 Jo",
        65 to "Jude",
        66 to "Rev"
    )
    val bookNumberToShortDeName = mapOf(
        1 to "1Mo",
        2 to "2Mo",
        3 to "3Mo",
        4 to "4Mo",
        5 to "5Mo",
        6 to "Jos",
        7 to "Ri",
        8 to "Rut",
        9 to "1Sa",
        10 to "2Sa",
        11 to "1Kö",
        12 to "2Kö",
        13 to "1Ch",
        14 to "2Ch",
        15 to "Esr",
        16 to "Neh",
        17 to "Est",
        18 to "Hiob",
        19 to "Psa",
        20 to "Spr",
        21 to "Pred",
        22 to "Hld",
        23 to "Jes",
        24 to "Jer",
        25 to "Kla",
        26 to "Hes",
        27 to "Dan",
        28 to "Hos",
        29 to "Joe",
        30 to "Amo",
        31 to "Obd",
        32 to "Jon",
        33 to "Mic",
        34 to "Nah",
        35 to "Hab",
        36 to "Zep",
        37 to "Hag",
        38 to "Sach",
        39 to "Mal",
        40 to "Mat",
        41 to "Mar",
        42 to "Luk",
        43 to "Joh",
        44 to "Apg",
        45 to "Röm",
        46 to "1Kor",
        47 to "2Kor",
        48 to "Gal",
        49 to "Eph",
        50 to "Phili",
        51 to "Kol",
        52 to "1Th",
        53 to "2Th",
        54 to "1Tim",
        55 to "2Tim",
        56 to "Tit",
        57 to "Phile",
        58 to "Heb",
        59 to "Jam",
        60 to "1 Pe",
        61 to "2 Pe",
        62 to "1 Jo",
        63 to "2 Jo",
        64 to "3 Jo",
        65 to "Jud",
        66 to "Off"
    )
    fun bookNumberToShortName(bookNumber: Int): String? {//called by bookNameCapital
        return if (getDefault().language == "de") bookNumberToShortDeName[bookNumber]
        else bookNumberToShortEnName[bookNumber]
    }
    //fun bookName(bookNumber: Int) = numberToName[bookNumber]
    fun bookName(bookNumber: Int): String? {//called by bookNameCapital
        if (getDefault().language == "de") return numberToDeName[bookNumber]
        else return numberToName[bookNumber]
    }
    data class VersePointer(
        var bookNumber: Int = 0,
        var chapter: Int = 0,
        var startVerse:Int = 0,
        val endVerse: Int? = null
    )

    data class VersStrings(
        var bookName: String = "",
        var chapter: String = "",
        var startVerse: String = "",
        var endVerse: String = "null"
    )
    fun parseBibelStelle(bibelStelle: String): VersStrings { //Joh 3:16-18
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
        val chapterVerseSplit = chapterVerse.split(":")

        val chapterNumber = chapterVerseSplit[0]

        val startVerse = if (chapterVerseSplit.size == 2) chapterVerseSplit[1].split("-")[0].toInt() else 0

        val endVerse = if (chapterVerseSplit.size == 2 && chapterVerse.contains("-"))
                            chapterVerseSplit[1].split("-")[1] else startVerse

        return VersStrings(
            bookName = bookString,
            chapter = chapterNumber,
            startVerse = startVerse.toString(),
            endVerse = endVerse.toString()
        )
    }
    fun versStringsToBibelStelle(versStrings: VersStrings): String {
        var ret = versStrings.bookName + " " + versStrings.chapter + ":" + versStrings.startVerse
        if (versStrings.endVerse != versStrings.startVerse) ret += "-" + versStrings.endVerse
        return ret
    }

    fun versShortName(bookNumber: Int, chapter: Int, verse: Int) = bookNumberToShortName(bookNumber) + " " + chapter + ":" + verse

    fun bookNameCapital(bookNumber: Int): String {
        if (bookNumber<1 || bookNumber>66) return "unknown book number: $bookNumber"
        //return bookName(bookNumber)
        val lowerCase = bookName(bookNumber)!!
        val split = lowerCase.split(" ")
        return when (split.size) {
            1 -> lowerCase.replaceFirstChar { it.uppercase() }
            2 -> "${split[0]} ${split[1].replaceFirstChar { it.uppercase() }}"
            3 -> "Song of Solomon"
            else -> return "unknown book number: $bookNumber"//throw RuntimeException("book name must be less than 3 words")
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
            bookNumber = bookNumber,
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

        return selected
    }

    fun chapterTextPath(versePointer: VersePointer) =
       // "texts/${versePointer.translation}/${versePointer.translation}.${versePointer.book}.${versePointer.chapter}.txt"
        "texts/${versePointer.bookNumber}.${versePointer.chapter}.txt"

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
            bookNumber = randomBook,
            chapter = randomChapter
        )

        return versePointer
    }
}