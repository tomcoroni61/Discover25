package trust.jesus.discover.bible.online

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import trust.jesus.discover.little.Globus
import java.io.IOException


class Kbolls {
//docs: https://bolls.life/api/  https://bolls.life/donate/  home https://bolls.life  = onlinebible mit Menu nice, nice
/* https://bolls.life/get-random-verse/YLT/   =
{"pk": 28578, "translation": "YLT", "book": 5, "chapter": 16, "verse": 21, "text": "'Thou dost not plant for thee a shrine of any trees near the altar of Jehovah thy God, which thou makest for thyself,"}
 */
    private val client = OkHttpClient()
    private val gson = Gson()
    private val gc: Globus = Globus.getAppContext() as Globus

    /* use in Activity:
    private fun fetchAndDisplayBibleVerse2(version: String, bookNum: String, chapter: String, vers: String) {
        lifecycleScope.launch {
            try { //Date(),
                val apiService = Kbolls()
                apiService.fetchBibleVerse(version, bookNum, chapter, vers)
                    .collect { result ->
                        if (result.isSuccess) {
                            val collectedBibleVerse = result.getOrNull()
                            if (collectedBibleVerse != null) {
                                // bibleVerse = collectedBibleVerse
                                //val referenceAndVersion = "${bibleVerse!!.reference} (${bibleVerse!!.versionLong})"
                                //verseText.text = bibleVerse!!.text
                                binding.actvText.text = collectedBibleVerse.text
                            } else {
                                gc.Logl( "showNoVerseError(requireContext())", true)
                            }
                        } else {
                            if (result.exceptionOrNull()?.message == "No Bible verse available from the API") {
                               gc.Logl( "showNoVerseError(requireContext()", true)
                            } else {
                                gc.Logl("showNetworkError(requireContext()", true)
                            }
                        }
                    }
                } catch (e: Exception) {
                //showNetworkError(requireContext())
            }
        }
    }
https://bolls.life/v2/find/MB?search=Gnade&match_case=false&match_whole=true&limit=3000&page=1
     */

    fun fetchBibleVerse(version: String, bookNum: String, chapter: String, vers: String): Flow< Result <BollsVers> > = flow {
        val apiUrl = "https://bolls.life/get-verse/$version/$bookNum/$chapter/$vers/"
        //"https://bolls.life/get-text/MB/22/8/" NKJV
        //gc.Logl(apiUrl, true)
        val request = Request.Builder()
            .url(apiUrl)
            .get()
            .build()

        val response = try {
            client.newCall(request).await()
        } catch (e: IOException) {
            null
        }

        if (response?.isSuccessful == true) {
            val json = response.body?.string()
            try {
                emit( Result.success( parseJsonToBibleVerse(json) ))
                //emit(Result.success(json) as Result<String>)
            } catch (
                e: IllegalArgumentException
            ) {
                emit(Result.failure(e))
            }
        } else {
            emit(Result.failure(Error("Error fetching Bible verse from the API")))
        }
    }

    private suspend fun Call.await(): Response = suspendCancellableCoroutine { continuation ->
        enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWith(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                continuation.resumeWith(Result.success(response))
            }
        })

        continuation.invokeOnCancellation { cancel() }
    }

    private fun parseJsonToBibleVerse(json: String?): BollsVers {
        if (json.isNullOrEmpty()) {
            throw IllegalArgumentException("JSON is empty or null")
        }
        val votdApiResponse = gson.fromJson(json, BollsVers::class.java)

        if (votdApiResponse.text.isEmpty()) {
            throw IllegalArgumentException("JSON does not contain a Bible verse")
        }

        val text = HtmlCompat.fromHtml(votdApiResponse.text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        //val comment = votdApiResponse.comment
        val pk = votdApiResponse.pk
        val vers = votdApiResponse.verse

        return BollsVers(pk, vers, text)
    }

    fun versArrayToChaptertext(verses: Array<BollsVers?>?): String {
        var ret = ""
        var cnt = 1
        if (verses != null) {
            for (verse in verses) {
                ret += cnt ++.toString() + " " + verse?.text + "\n"
            }
        }
        ret = ret + "\n"+ "\n"+ "\n"+ "\n"+ "\n"+ "\n"
        return ret
    }
    fun fetchBibleChapter(version: String, bookNum: String, chapter: String): Flow< Result <Array<BollsVers?>?> > = flow {
        val apiUrl = "https://bolls.life/get-text/$version/$bookNum/$chapter/"
        //"https://bolls.life/get-text/MB/22/8/" NKJV
        //gc.Logl(apiUrl, true)
        if (!hasInternet()) {
            emit(Result.failure(Error("No internet connection")))
            //gc.dl
            return@flow
        }
        val request = Request.Builder()
            .url(apiUrl)
            .get()
            .build()

        val response = try {
            client.newCall(request).await()
        } catch (e: IOException) {
            null
        }

        if (response?.isSuccessful == true) {
            val json = response.body?.string()
            try {
                emit( Result.success( parseJsonToBibleChapterVerses(json) ))
                //emit(Result.success(json) as Result<String>)
            } catch (
                e: IllegalArgumentException
            ) {
                emit(Result.failure(e))
            }
        } else {
            emit(Result.failure(Error("Error fetching Bible verses from the API")))
        }
    }

    private fun parseJsonToBibleChapterVerses(json: String?): Array<BollsVers?>? {
        if (json.isNullOrEmpty()) {
            throw IllegalArgumentException("JSON is empty or null")
        }
        //val votdApiResponse = gson.fromJson(json, BollsVers::class.java)
        val ChapterVerses: Array<BollsVers?>? =
            gson.fromJson<Array<BollsVers?>?>(json, Array<BollsVers>::class.java)

        //HtmlCompat.fromHtml(votdApiResponse.text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        //=val data: Array<Wrapper?>? = gson.fromJson(jElement, Array<Wrapper>::class.java)
        if (ChapterVerses.isNullOrEmpty()) {
            throw IllegalArgumentException("JSON does not contain a Bible verse")
        }

        for (verse in ChapterVerses) {
            verse!!.text = HtmlCompat.fromHtml(verse.text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

        }
        return  ChapterVerses //BollsVers( pk, vers, text )
    }
    fun bibelVersionShort(VersionName: String) = when (VersionName) {
        //bolls all en 38!
        "New King James Version, 1982" -> "NKJV"
        "New International Version, 1984" -> "NIV"
        "New American Standard Bible (1995)" -> "NASB"
        "Literal Standard Version" -> "LSV"
        "Revised Standard Version (1952)" -> "RSV"
        "Young's Literal Translation (1898)" -> "YLT"
        "The Legacy Standard Bible" -> "LSB"
        "World English Bible" -> "WEB"
        "The Complete Jewish Bible (1998)" -> "CJB"
        "The Scriptures 2009" -> "TS2009"
        "English version of the Septuagint Bible, 1851" -> "LXXE"
        "Tree of Life Version" -> "TLV"
        "Geneva Bible (1599)" -> "GNV"
        "Douay Rheims Bible" -> "DRB"
        "Amplified Bible, 2015" -> "AMP"
        "The Holy Bible, Berean Standard Bible" -> "BSB"
        "King James Version 1769 with Apocrypha and Strong's Numbers" -> "KJV"
        "Menge-Bibel" -> "MB"
        "Elberfelder Bibel, 1871" -> "ELB"
        "Schlachter (1951)" -> "SCH"
        "Schlachter 2000" -> "S00"
        "Luther (1912)" -> "LUT"
        "Hoffnung für Alle, 2015" -> "HFA"
        else -> "HFA" //throw Exception("Bibelversion '$VersionName' not found in list")
    }

    // https://bolls.life/api/#Search
    fun fetchBibleSearch(version: String, suchWort: String): Flow< Result <Array<BollsSR?>?> > = flow {
        val apiUrl = "https://bolls.life/v2/find/$version?search=$suchWort"
        //https://bolls.life/v2/find/YLT?search=haggi&match_case=false&match_whole=true&limit=128&page=1
        //gc.Logl(apiUrl, true)
        if (!hasInternet()) {
            emit(Result.failure(Error("No internet connection")))
            //gc.dl
            return@flow
        }
        val request = Request.Builder()
            .url(apiUrl)
            .get()
            .build()

        val response = try {
            client.newCall(request).await()
        } catch (e: IOException) {
            null
        }

        if (response?.isSuccessful == true) {
            val json = response.body?.string()
            try {
                emit( Result.success( parseJsonToBibleSearchVerses(json) ))
                //emit(Result.success(json) as Result<String>)
            } catch (
                e: IllegalArgumentException
            ) {
                emit(Result.failure(e))
            }
        } else {
            emit(Result.failure(Error("Error fetching Bible verses from the API")))
        }
    }

    fun parseJsonToBibleSearchVerses(json: String?): Array<BollsSR?>? {
        if (json.isNullOrEmpty()) {
            throw IllegalArgumentException("JSON is empty or null")
        }
        val jsonResponse = JSONObject(json)
        val jr = jsonResponse.getString("results")
        //gc.log(jr)
        if (jr.isNullOrEmpty()) {
            throw IllegalArgumentException("JSON does not contain a Bible verse")
        }

        jsonResponse.put("searchword", "holy")
        val chapterVerses: Array<BollsSR?>? =
            gson.fromJson<Array<BollsSR?>?>(jr, Array<BollsSR>::class.java)

        if (chapterVerses.isNullOrEmpty()) {
            throw IllegalArgumentException("JSON does not contain a Bible verse")
        }

        for (verse in chapterVerses) {
            verse!!.text = HtmlCompat.fromHtml(verse.text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

        }
        return  chapterVerses //BollsVers( pk, vers, text )
    }

    /* suchergebniss YLT haggi
    https://bolls.life/v2/find/YLT?search=haggi&match_case=false&match_whole=true&limit=128&page=1
    {"results": [{"pk": 24617, "translation": "YLT", "book": 1, "chapter": 46, "verse": 16,
    "text": "And sons of Gad: Ziphion, and <mark>haggi</mark>, Shuni, and Ezbon, Eri, and Arodi, and Areli."},
{"pk": 27719, "translation": "YLT", "book": 4, "chapter": 26, "verse": 15,
"text": "Sons of Gad by their families: of Zephon [is] "}], "exact_matches": 9, "total": 8}

bible gateway
{"votd":{"text":"&ldquo;Denn der HERR hat Wohlgefallen an seinem Volk;er schm&#252;ckt die Elenden mit Heil.&rdquo;",
"content":"Denn der <span class=\"small-caps\" >Herr<\/span> hat Wohlgefallen an seinem Volk; er schm&#252;ckt die Elenden mit Heil.",
"display_ref":"Psalmen 149:4","reference":"Psalm 149:4",
"permalink":"https:\/\/www.biblegateway.com\/passage\/?search=Psalm%20149%3A4&amp;version=SCH2000",
"copyright":"","copyrightlink":"https:\/\/www.biblegateway.com\/versions\/index.php?action=getVersionInfo&amp;vid=188&amp;lang=2",
"audiolink":"","day":"08","month":"08","year":"2025",
"version":"Schlachter 2000","version_id":"SCH2000","merchandising":""}}


     */
    fun fetchBibleSearchJson(version: String, suchWort: String, match_case: Boolean,
                             match_whole: Boolean, range: String = "nt"): Flow< Result < String? > > = flow {
        var apiUrl = "https://bolls.life/v2/find/$version?search=$suchWort&match_case=$match_case&limit=3000&match_whole=$match_whole"
        //https://bolls.life/v2/find/YLT?search=haggi&match_case=false&match_whole=true&limit=128&page=1
        //gc.Logl(apiUrl, true)
        when (range) {
            "nt" -> apiUrl = "$apiUrl&book=nt"
            "ot" -> apiUrl = "$apiUrl&book=ot"
        }
        if (!hasInternet()) {
            emit(Result.failure(Error("No internet connection")))
            //gc.dl
            return@flow
        }
        val request = Request.Builder()
            .url(apiUrl)
            .get()
            .build()

        val response = try {
            client.newCall(request).await()
        } catch (e: IOException) {
            null
        }

        if (response?.isSuccessful == true) {
            val json = response.body?.string()
            try {
                emit(Result.success(json) )
            } catch (
                e: IllegalArgumentException
            ) {
                emit(Result.failure(e))
            }
        } else {
            emit(Result.failure(Error("Error fetching Bible verses from the API")))
        }
    }
    fun bibelVersionShortToLong(VersionName: String) = when (VersionName) {
        //bolls all en 38!
        "NKJV" -> "New King James Version, 1982"
        "NIV" -> "New International Version, 1984"
        "NASB" -> "New American Standard Bible (1995)"
        "RSV" -> "Revised Standard Version (1952)"
        "YLT" -> "Young's Literal Translation (1898)"
        "LSB" -> "The Legacy Standard Bible"
        "WEB" -> "World English Bible"
        "CJB" -> "The Complete Jewish Bible (1998)"
        "TS2009" -> "The Scriptures 2009"
        "LXXE" -> "English version of the Septuagint Bible, 1851"
        "TLV" -> "Tree of Life Version"
        "GNV" -> "Geneva Bible (1599)"
        "DRB" -> "Douay Rheims Bible"
        "AMP" -> "Amplified Bible, 2015"
        "BSB" -> "The Holy Bible, Berean Standard Bible"
        "KJV" -> "King James Version 1769 with Apocrypha and Strong's Numbers"
        "MB" -> "Menge-Bibel"
        "ELB" -> "Elberfelder Bibel, 1871"
        "SCH" -> "Schlachter (1951)"
        "S00" ->"Schlachter 2000"
        "LUT" -> "Luther (1912)"
        "HFA" -> "Hoffnung für Alle, 2015"
        else -> "Hoffnung für Alle, 2015" //throw Exception("Bibelversion '$VersionName' not found in list")
    }

    fun bibelVersionLang(VersionName: String) = when (VersionName) {
    //bolls all en 38!
    "NKJV", "NIV", "NASB", "LSV", "RSV", "YLT", "LSB", "WEB", "CJB" -> "EN"
    "TS2009", "LXXE", "TLV", "GNV", "DRB", "AMP", "BSB", "KJV" -> "EN"
    "ELB", "SCH", "MB", "S00", "LUT", "HFA" -> "DE"
    else -> "DE" // throw Exception("Bibelversion '$VersionName' not found in list")
}

    //@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun hasInternet(): Boolean {
        val connectivityManager = gc.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false

        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        val ret =  when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        if (!ret) gc.Logl("no Internet!!", true)
        return ret
    }
}