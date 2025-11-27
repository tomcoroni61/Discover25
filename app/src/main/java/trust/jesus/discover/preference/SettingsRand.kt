package trust.jesus.discover.preference

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewParent
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.DialogPreference
import androidx.preference.PreferenceFragmentCompat
import trust.jesus.discover.R
import trust.jesus.discover.little.Globus
import androidx.preference.EditTextPreference

class SettingsRand : AppCompatActivity() {
    private val gc: Globus = Globus.getAppContext() as Globus

    override fun onCreate(savedInstanceState: Bundle?) {
        gc.mainActivity!!.setActivityTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        gc.mainActivity!!.setActivityTheme(this)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        //private val gc: Globus = Globus.getAppContext() as Globus
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            //gc.mainActivity!!.setActivityTheme(this)

            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            preferenceScreen.findPreference<EditTextPreference>("search_word")?.setOnBindEditTextListener {
                it.setSingleLine()

                /*
                try {
                    val dialog: DialogPreference = it.parent as DialogPreference
                    dialog.setPositiveButtonText("search")
                    dialog.setNegativeButtonText("cancel")
                } catch (e: Exception) {
//it.parent.col setBackgroundResource(R.drawable.speech_sel) DialogPreference
                //it.setBackgroundColor( Color.MAGENTA) R.drawable.rounded_corner
                //it.background = ResourcesCompat.getDrawable(R.drawable.rounded_corner)
                try {
                    var xparent = it.parent
                    while (isLinearLayout(xparent)) {
                        (xparent as LinearLayout).setBackgroundColor(Color.GREEN)
                        xparent = xparent.parent
                    }

                } catch (e: Exception) {
                    it.setBackgroundColor(Color.RED)
                    it.setText(e.message)
                }
                 */

            }
        }
    }
}