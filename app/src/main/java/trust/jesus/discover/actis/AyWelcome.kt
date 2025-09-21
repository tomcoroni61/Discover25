package trust.jesus.discover.actis

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import trust.jesus.discover.R
import trust.jesus.discover.databinding.ActivityAyWelcomeBinding
import trust.jesus.discover.little.Globus
import java.io.BufferedReader
import java.io.InputStreamReader

class AyWelcome : AppCompatActivity() {

    lateinit var binding: ActivityAyWelcomeBinding
    val gc: Globus = Globus.getAppContext() as Globus

    override fun onCreate(savedInstanceState: Bundle?) {
        gc.mainActivity?.setWelcomeTheme(this)
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityAyWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gc.mainActivity?.setWelcomeTheme(this, true)
        val text: CharSequence = read(this, R.raw.welcome)
        binding.HtmlView.movementMethod = LinkMovementMethod.getInstance()
        binding.HtmlView.text = Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_COMPACT)


    }

    fun read(context: Context, resId: Int): CharSequence {
        val text = StringBuilder()

        try {
            BufferedReader(
                InputStreamReader(
                    context.resources.openRawResource(resId)
                )
            ).use { buffer ->
                var line: String?
                while ((buffer.readLine().also { line = it }) != null) text.append(line)
                    .append(System.lineSeparator())
            }
        } catch (ignored: Exception) {
        }

        return text
    }

    fun wExitClick(view: View?) {
        finish()
    }

    fun wEineFueClick(view: View?) { //intruduce.html
        val text: CharSequence = read(this, R.raw.intruduce)
        binding.HtmlView.text = Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_COMPACT)
        binding.wtvwell.text = getString(R.string.introduction)
    }

    fun wThanksClick(view: View) {
        val text: CharSequence = read(this, R.raw.thankyou)
        binding.HtmlView.text = Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_COMPACT)
        binding.wtvwell.text = getString(R.string.thank_you)
    }
}