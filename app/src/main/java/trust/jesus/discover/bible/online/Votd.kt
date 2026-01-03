package trust.jesus.discover.bible.online

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import trust.jesus.discover.little.Globus
import java.io.IOException

class Votd {
    private val gc: Globus = Globus.getAppContext() as Globus
    private val client = OkHttpClient()

    fun fetchVotdUrl(apiUrl: String): Flow< Result < String? > > = flow {
        //val apiUrl = "https://labs.bible.org/api/?passage=votd"
        //val apiUrl = "https://www.biblegateway.com/votd/get/?format=json&version=HFA"
//"https://www.biblegateway.com/votd/get/?format=json&version=HFA"    ?????
        gc.bolls()?.hasInternet()?.let {
            if (!it) {
                emit(Result.failure(Error("No internet connection")))
            }
        }
        val request = Request.Builder()
            .url(apiUrl)
            .get()
            .build()

        val response = try {
            client.newCall(request).await()
        } catch (e: IOException) {
            emit(Result.failure(Error(e)))
            //gc.Logl("fetch IOException " , true)
            null
        }
        //gc.Logl("fetch response: " + (response!=null) + " succses " + response?.isSuccessful , true)
        if (response?.isSuccessful == true) {
            val json = response.body?.string()
            try {
                emit(Result.success(json))
            } catch (
                e: IllegalArgumentException
            ) {
                emit(Result.failure(e))
            }
        } else {
            emit(Result.failure(Error("Error fetching vers from https://labs.bible.org")))
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
        //gc.Logl("fetch invokeOnCancellation " , true)
        continuation.invokeOnCancellation { cancel() }
    }

}

/*
Vers of the day votd
https://labs.bible.org/api/?passage=votd     returns:
<b>Revelation 2:10</b> Do not be afraid of the things you are about to suffer. The devil is about to have some of you thrown into prison so you may be tested, and you will experience suffering for ten days. Remain faithful even to the point of death, and I will give you the crown that is life itself.

https://bible-api.com/votd ne
fetchDailyVerse
"https://www.biblegateway.com/votd/get/?format=json&version=HFA"    ?????
https://www.biblegateway.com/reading-plans/verse-of-the-day/next?version=NLT  OFF also

https://dailyverses.net/get/verse.js?language=niv  returns:
document.getElementById("dailyVersesWrapper").innerHTML = '\u003cdiv class=\"dailyVerses bibleText\"\u003eWhen God raised up his servant, he sent him first to you to bless you by turning each of you from your wicked ways.\u003c/div\u003e\u003cdiv class=\"dailyVerses bibleVerse\"\u003e\u003ca href=\"https://dailyverses.net/2025/11/25\" rel=\"noopener\" target=\"_blank\"\u003eActs 3:26\u003c/a\u003e\u003c/div\u003e';

https://beta.ourmanna.com/api/v1/get
I put no trust in my bow, my sword does not bring me victory; - Psalm 44:6 (NIV)

YouVersion free? MIT-Licence
from: https://github.com/Glowstudent777/YouVersion-Core/blob/40f1662b8c7eb766d83fe4d3c51f7f7ef3629be0/src/db/versions.json
https://www.bible.com/de/verse-of-the-day

 */
