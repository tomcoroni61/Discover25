package trust.jesus.discover.bible.online

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

class Bss {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val gc: Globus = Globus.getAppContext() as Globus


    fun fetchBibleVerses(versionShort: String, bookName: String, chapter: String, versStart: String,
                         versEnd: String): Flow< Result < Array<BssSR?>?> > = flow {
        val apiUrl = "https://api.biblesupersearch.com/api?bible=$versionShort&reference=$bookName%20" +
                "$chapter:$versStart-$versEnd"
        // https://api.biblesupersearch.com/api?bible=kjv&reference=Rom%204:1-10
        //data_format https://api.biblesupersearch.com/api?data_format&bible=kjv&reference=Rom%204:1-10
        gc.Logl(apiUrl, true)
        gc.bolls()?.hasInternet()?.let {
            if (!it) {
                emit(Result.failure(Error("No internet connection")))
                //gc.dl
                return@flow
            }
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
                emit( Result.success( parseJsonToBibleVerses(versionShort,chapter, json) ))
                //emit(Result.success(json) as Result<String>)
            } catch (
                e: IllegalArgumentException
            ) {
                emit(Result.failure(e))
            }
        } else {
            emit(Result.failure(Error("Error fetching Bible verse from the API\n" +
                    getError(response) )))
        }
    }

    private fun getError(response: Response?): String {
        val json = response?.body?.string()
        val jsonResponse = JSONObject(json.toString())

        val errArray = jsonResponse.getJSONArray("errors") //oki count=0
        if (errArray.length() > 0)
            return errArray.getString(0)
        return "Unknown error"
    }
    private fun parseJsonToBibleVerses(versionShort: String, chapter: String, json: String?): Array<BssSR?>? {
        if (json.isNullOrEmpty()) {
            throw IllegalArgumentException("JSON is empty or null")
        }
        val jsonResponse = JSONObject(json)
        /*"results":[{"x".."verses":{"kjv":{"2":{"2":
        "xName":{..} = SONObject
         */
        gc.log("jre 1")

        val errArray = jsonResponse.getJSONArray("errors") //oki count=0
        if (errArray.length() > 0) {
            gc.log("jre 1a err array: $errArray")
            throw IllegalArgumentException("Error reason: ${errArray.getString(0)}")
        }

        val resultsArray = jsonResponse.getJSONArray("results") //oki count=1
        //output see: https://api.biblesupersearch.com/api?bible=kjv&reference=Rom%202:2-5
        val results0JSONObject = resultsArray.getJSONObject(0) //count=11
        gc.log("jre 2a count: ${results0JSONObject.length()}" + results0JSONObject.toString())
        val versesObj = results0JSONObject.getJSONObject("verses") //= "verses":{
        val versesFromBibleVersionObj = versesObj.getJSONObject(versionShort)
        val chapterVersesObj = versesFromBibleVersionObj.getJSONObject(chapter)
        //gc.log("jre 2d  count: ${chapterVersesObj.length()} " + chapterVersesObj.toString())
        val verseCount = chapterVersesObj.names()?.length()

        var bssArray = emptyArray<BssSR?>()

        for (i in 0..<verseCount!!) {
            val name = chapterVersesObj.names()?.getString(i)
            val text = chapterVersesObj.getString(name.toString())
            val bssR = gson.fromJson(text, BssSR::class.java)
            bssArray = bssArray.plus(bssR)
            //gc.log("bssR: ${bssR.text}")
        }
        return bssArray
        //return bssArray as Array<BssSR?>?


//22.10 ~20:00 - 03:00 23.10.25 wundersames Gelingen, Durchbruch kam nach Stoßgebet mit Hallelujah gegen Ende
        //"org" unterhalb
    }

 /* 22.10 ~20:00 - 03:00 23.10.25 wundersames Gelingen, Durchbruch kam nach Stoßgebet mit Hallelujah gegen Ende
 fun parseJsonToBibleVerses(versionShort: String, chapter: String, json: String?): Array<BssSR?>? {
        if (json.isNullOrEmpty()) {
            throw IllegalArgumentException("JSON is empty or null")
        }
        val jsonResponse = JSONObject(json)

        gc.log("jre 1")
        //ne val xx = jsonResponse.getJSONObject("results")
        gc.log("jre 1a")
        val jor = jsonResponse.getJSONArray("results") //oki
        //output see: https://bolls.life/v2/find/YLT?search=haggi&match_case=false&match_whole=true&limit=128&page=1
        gc.log("jre 2 count: ${jor.length()}")
        val xa = jor.getJSONObject(0)
        gc.log("jre 2a count: ${xa.length()}" + xa.toString())
        val xb = xa.getJSONObject("verses")
        gc.log("jre 2b count: ${xb.length()}" + xb.toString() )
        val xc = xb.getJSONObject(versionShort)
        gc.log("jre 2c")
        val xd = xc.getJSONObject(chapter)
        gc.log("jre 2d  count: ${xd.length()} " + xd.toString())

        val ya = xd.names()?.length()
        gc.log("jre 3 count: ${ya}" + ya.toString() )

        val versList: MutableList<BssSR> = ArrayList()

        for (i in 0..<ya!!) {
            val name = xd.names()?.getString(i)
            //if (name == "text")
            gc.log("name: $name")
            val text = xd.getString(name.toString())
            gc.log("text: $text")
            val bssR = gson.fromJson(text, BssSR::class.java)
            versList.add(bssR)
            gc.log("bssR: ${bssR.text}")
        }

        return versList.toTypedArray()



    }

  */
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

}