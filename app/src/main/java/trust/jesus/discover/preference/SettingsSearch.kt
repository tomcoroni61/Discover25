package trust.jesus.discover.preference

import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceViewHolder
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import trust.jesus.discover.R
import trust.jesus.discover.little.FixStuff.Filenames.Companion.jsonLsFName
import trust.jesus.discover.little.Globus

class SettingsSearch (context: Context?, attrs: AttributeSet?) : Preference(context!!, attrs),
//    View.OnClickListener
  View.OnClickListener
{
    val gc: Globus = Globus.Companion.getAppContext() as Globus
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var tvResult: TextView
// use lifecycleScope with Preference

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        //currentHour = sharedPrefs.getString("notification_hour", "8").toString()

        super.onBindViewHolder(holder)

        // Find the TextView using findViewById
        val btn: View = holder.itemView.findViewById(R.id.btnbuildList)
        btn.setOnClickListener(this)

        tvResult = holder.itemView.findViewById(R.id.tvSearchRet)
        var cnt = gc.seekList().entries()
        if (gc.sharedPrefs.getBoolean("use_jsonList", true))
            cnt = gc.jsonList()!!.entries()

        val txt = cnt.toString() + "verse"
        tvResult.text = txt

        holder.itemView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnbuildList -> btnbuildListClick()
        }

    }

    //@OptIn(DelicateCoroutinesApi::class)
    private fun fetchBibleSearch(version: String?, suchWort: String?,
                                 matchcase: Boolean, matchwhole: Boolean, range: String?) = runBlocking  {
        launch {
            try { //Date(),
                gc.bolls()!!.fetchBibleSearchJson(version!!, suchWort!!, matchcase, matchwhole, range!!)
                    .collect { result ->
                        if (result.isSuccess) {
                            val verses = result.getOrNull()  //verses?.forEach { verse ->

                            if (verses != null) {
                                val jsonResponse = JSONObject(verses)
                                jsonResponse.getString("results")
                                //val jsonResponse2 = JSONObject(jr)
                                val count = jsonResponse.getInt("total")
                                var ati = "found $count verses"
                                if (count > 0) {
                                    if (sharedPrefs.getBoolean("use_jsonList", true)) {
                                        gc.dateien().writePrivateFile(jsonLsFName, verses)
                                        gc.appVals().valueWriteString("json_suchwort", sharedPrefs.getString("search_word", ""))
                                    } else
                                            gc.seekList().jsonToVersList(verses)
                                    //
                                } else ati = "no verses found"
                                // \n" + verses.substring(1, 200)
                                tvResult.text = ati
                            }

                        }
                    }
            } catch (e: Exception) {
                tvResult.text = "error " + e.message
            }
        }
    }
    private fun btnbuildListClick() {
        val suchwort = sharedPrefs.getString("search_word", "")
        if (suchwort.isNullOrEmpty()) {
            tvResult.text = "no word to search"
            return
        }
        tvResult.text = "wait.." //search_range
        fetchBibleSearch( sharedPrefs.getString("search_bible_version", ""),
            sharedPrefs.getString("search_word", ""),
            sharedPrefs.getBoolean("match_case", false),
            sharedPrefs.getBoolean("match_whole", false),
            sharedPrefs.getString("search_range", "") )

    }
}