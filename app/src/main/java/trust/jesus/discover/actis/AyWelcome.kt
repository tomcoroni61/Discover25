package trust.jesus.discover.actis

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import trust.jesus.discover.BuildConfig
import trust.jesus.discover.R
import trust.jesus.discover.databinding.ActivityAyWelcomeBinding
import trust.jesus.discover.little.CustomWebViewClient
import trust.jesus.discover.little.Globus
import java.io.ByteArrayOutputStream
import java.io.IOException

class AyWelcome : AppCompatActivity() {

    lateinit var binding: ActivityAyWelcomeBinding
    val gc: Globus = Globus.getAppContext() as Globus

    override fun onCreate(savedInstanceState: Bundle?) {
        gc.mainActivity?.setWelcomeTheme(this)
        super.onCreate(savedInstanceState)
        ErrorHandler.toCatch(this)
        //gc.log("AyWelcome onCreate")
        enableEdgeToEdge()
        binding = ActivityAyWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.wbHtmlView.webViewClient = WebViewClient()
        //binding.wbHtmlView.settings.javaScriptEnabled = true; WebViewClient CustomWebViewClient


        gc.mainActivity?.setWelcomeTheme(this, true)
        val texti = getHtmlText( R.raw.welcome)
        binding.HtmlView.movementMethod = LinkMovementMethod.getInstance()
        val data = Html.fromHtml(texti, Html.FROM_HTML_MODE_COMPACT)
        binding.HtmlView.text = data
        //binding.HtmlView.getSettings();
        //textview html Tags: https://stackoverflow.com/questions/9754076/which-html-tags-are-supported-by-android-textview
    }
/*
binding.HtmlView.webViewClient = WebViewClient()
        binding.HtmlView.settings.javaScriptEnabled = true;
        //binding.HtmlView.movementMethod = LinkMovementMethod.getInstance()
        val data = texti.toString() //Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_COMPACT).toString()
        binding.HtmlView.loadData(data, "text/html; charset=utf-8", "UTF-8")
binding.HtmlView.setBackgroundColor(Color.LTGRAY);


    override fun onSupportNavigateUp(): Boolean {
        //finish()
        if (binding.wbHtmlView.canGoBack()) {
            binding.wbHtmlView.goBack()
            return false
        }
        return true
    }
       fun read(context: Context, resId: Int): CharSequence {
        val text = StringBuilder()//CharSequence StringBuilder
        try {
            BufferedReader(
                InputStreamReader(
                    context.resources.openRawResource(resId)
                )
            ).use { buffer ->
                var line: String?
                while ((buffer.readLine().also { line = it }) != null) text.append(line)
                    .append(System.lineSeparator()) //System.lineSeparator()
            }
        } catch (ignored: Exception) {
        }

        return text
    }

 */

    override fun onSupportNavigateUp(): Boolean {
        //finish()
        if (binding.wbHtmlView.canGoBack()) {
            binding.wbHtmlView.goBack()
            return false
        }
        return true
    }

    private fun getHtmlText(resId: Int): String {
        val inputStream = getResources().openRawResource(resId)
        val byteArrayOutputStream = ByteArrayOutputStream()
        var i: Int
        try {
            i = inputStream.read()
            while (i != -1) {
                byteArrayOutputStream.write(i)
                i = inputStream.read()
            }
            inputStream.close()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return byteArrayOutputStream.toString()
    }

    fun wExitClick(view: View?) {
        finish()
        //getExternalStoragePublicDirectory()
    }

    fun wEineFueClick(view: View?) { //intruduce.html
        binding.wbHtmlView.visibility = View.GONE
        binding.HtmlView.visibility = View.VISIBLE
        binding.scroll.scrollTo(0, 0)
        val text = getHtmlText( R.raw.intruduce)
        binding.HtmlView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        binding.wtvwell.text = getString(R.string.introduction)
    }


    fun wThanksClick(view: View) {
        //val text: CharSequence = read(this, R.raw.thankyou)
        binding.wbHtmlView.visibility = View.GONE
        binding.HtmlView.visibility = View.VISIBLE
        binding.scroll.scrollTo(0, 0)
        var htm = getHtmlText( R.raw.thankyou) //text.toString()//(Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_COMPACT)).toString()
        //throw IllegalArgumentException("Testing crash")
        /*
                val col: Int = binding.HtmlView.getSolidColor()
                val htmlSpanner: HtmlSpanner = HtmlSpanner(tv.getCurrentTextColor(), binding.HtmlView.textSize)
                htmlSpanner.setBackgroundColor(col)
                binding.HtmlView.setText(htmlSpanner.fromHtml(html))

         */
        //htm = removeLineEndings(htm)

        val idx=htm.indexOf("</body>")
        if (idx > 0) {//htm = htm.substring(0, idx-1) + versio + htm.substring(idx)
            var version = "\n\n<br> AndroidVersion = ${Build.VERSION.RELEASE}  <br> <br> Version Check:<br>\n"
            //version += "V12 12.25  here <br> \n"
            version += "${BuildConfig.VERSION_NAME}\n    =VERSION_NAME by BuildConfig <br>"
            version += "${BuildConfig.VERSION_CODE}\n    =VERSION_CODE by BuildConfig <br> <br>"
            htm = htm.take(idx-1) + version + htm.substring(idx)
        }
        binding.HtmlView.text = Html.fromHtml(htm, Html.FROM_HTML_MODE_LEGACY)
        binding.wtvwell.text = getString(R.string.thank_you)
    }

    fun wwwClick(view: View) {
        try {
            binding.scroll.scrollTo(0, 0)
            var htm =
                getHtmlText(R.raw.thankyou) //text.toString()//(Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_COMPACT)).toString()
            val idx = htm.indexOf("</body>")
            if (idx > 0) {//htm = htm.substring(0, idx-1) + versio + htm.substring(idx)
                var versio = "\n\n<br> <br> <br> Version Check:<br>\n"
                versio += "V11 12.25  here <br> \n"
                versio += "${BuildConfig.VERSION_NAME}\n    =VERSION_NAME by BuildConfig <br>"
                versio += "${BuildConfig.VERSION_CODE}\n    =VERSION_CODE by BuildConfig <br> <br>"
                htm = htm.take(idx - 1) + versio + htm.substring(idx)
            }

            binding.HtmlView.visibility = View.GONE
            binding.wbHtmlView.visibility = View.VISIBLE
            binding.wbHtmlView.loadData(htm, "text/html; charset=utf-8", "UTF-8")
            binding.wbHtmlView.setBackgroundColor(Color.LTGRAY);

            binding.wtvwell.text = getString(R.string.thank_you)
        } catch (ex: Exception) {
            // ex.printStackTrace();
            gc.crashLog(ex, 119)
        }
    }
}
/*
if (android.os.Build.VERSION.SDK_INT <= 26) {//30=11 28=9
            binding.HtmlView.visibility = View.GONE
            binding.wbHtmlView.visibility = View.VISIBLE
            //binding.HtmlView.movementMethod = LinkMovementMethod.getInstance()
            val data = htm.toString() //Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_COMPACT).toString()
            binding.wbHtmlView.loadData(data, "text/html; charset=utf-8", "UTF-8")
            binding.wbHtmlView.setBackgroundColor(Color.LTGRAY);
        } else {//android.os.Build.VERSION_CODES  Android 10 (API level 29)
            //htm = Html.fromHtml(htm, Html.FROM_HTML_MODE_COMPACT)
            binding.HtmlView.text = Html.fromHtml(htm, Html.FROM_HTML_MODE_COMPACT)
        }
Others:
when updating from Narval to Otter and agp 12 to 8.13
apk file is wrong made
 */