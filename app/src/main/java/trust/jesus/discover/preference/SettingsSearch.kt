package trust.jesus.discover.preference

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
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
import trust.jesus.discover.dlg_data.FileDlg
import trust.jesus.discover.little.FixStuff.Filenames.Companion.defaultLsFName
import trust.jesus.discover.little.FixStuff.Filenames.Companion.seekFileExtn
import trust.jesus.discover.little.Globus

class SettingsSearch (context: Context?, attrs: AttributeSet?) : Preference(context!!, attrs),
  View.OnClickListener {
    val gc: Globus = Globus.Companion.getAppContext() as Globus
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var tvResult: TextView
// use lifecycleScope with Preference

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        //currentHour = sharedPrefs.getString("notification_hour", "8").toString()

        super.onBindViewHolder(holder)

        var btn: View = holder.itemView.findViewById(R.id.btnbuildList)
        btn.setOnClickListener(this)
        btn = holder.itemView.findViewById(R.id.btnopiDlg)
        btn.setOnClickListener(this)

        tvResult = holder.itemView.findViewById(R.id.tvSearchRet)
        val cnt = gc.seekList().entries()

        val txt = cnt.toString() + "verse"
        tvResult.text = txt

        holder.itemView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnbuildList -> {
                Handler(Looper.getMainLooper()).postDelayed(
                    { btnbuildListClick() },
                    333
                )

            }
            R.id.btnopiDlg -> btnopiDlgClick()
        }

    }

    private fun btnopiDlgClick() {
        val fileDlg = FileDlg(
            context,
            "FileOpen",  //or FileSave or FileSave..  ..= chosenDir with dir | or FileOpen
            seekFileExtn
        ) { chosenDir: String? ->
            //binding.tvSeekbar.text = chosenDir
            gc.seekList().openSeekFile(chosenDir!!)
        }
        //fileDlg.create(); //!
        fileDlg.chooseFileOrDir(gc.filesDir.absolutePath)
    }

    //@OptIn(DelicateCoroutinesApi::class)
    private fun fetchBibleSearch(version: String?, suchWort: String?,
                                 matchCase: Boolean, matchWhole: Boolean, range: String?) = runBlocking  {
        launch {
            try { //Date(),
                gc.bolls()!!.fetchBibleSearchJson(version!!, suchWort!!, matchCase, matchWhole, range!!)
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
                                    var sf = sharedPrefs.getString("search_word", "").toString()+seekFileExtn
                                    if (!sharedPrefs.getBoolean("new_search_list", false)) sf = defaultLsFName
                                    gc.seekList().jsonToVersList(verses, sf)
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