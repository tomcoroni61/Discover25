package trust.jesus.discover.actis

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import trust.jesus.discover.R
import trust.jesus.discover.little.Globus
import trust.jesus.discover.databinding.AyMailMeBinding
class Reportus : AppCompatActivity() {
    private val gc: Globus = Globus.getAppContext() as Globus
    private var repl = "Error"
    lateinit var binding: AyMailMeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        gc.mainActivity?.setReportusTheme(this) //ohne crash!!
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE) //comment this line if you need to show Title.
        binding = AyMailMeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //after many tries this fits R.id.main between system bars.. todo in all activities  android:id="@+id/main"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val intent = getIntent()
        if (intent.hasExtra("ErrMsg")) repl = intent.getStringExtra("ErrMsg").toString()
        binding.edMessage.setText(repl)
        gc.mainActivity?.setReportusTheme(this, true)
    }

    private fun buildInfo(): String {
        val pm = packageManager
        // Version
        val pi: PackageInfo = pm.getPackageInfo(packageName, 0)
        var info = "\n\nVersion = " + pi.versionName + "\n"
        info += "Phone = " + Build.MODEL  + "\n" +
                "AndroidVersion=" + Build.VERSION.RELEASE + "\n"
        info += "Api Version = " + Build.VERSION.SDK_INT + "\n"
        info += "Device=" + Build.DEVICE + "\n" + "Display=" + Build.DISPLAY
        return info
    }
    fun adSendMailClick(view: View?) {
        sendErrorMail(this.baseContext, repl)
    }

    private fun sendErrorMail(context: Context, errorContent: String?) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        val subject = (context.resources.getString(R.string.CrashReport_MailSubject)
                + context.resources.getString(R.string.app_name))
        val body = context.resources.getString(R.string.CrashReport_MailBody) +
                "\n\n" +
                errorContent +
                "\n\n"
        sendIntent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf<String?>(context.getString(R.string.CrashReportEmailTo))
        )
        sendIntent.putExtra(Intent.EXTRA_TEXT, body)
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sendIntent.type = "message/rfc822"

        //startActivity(Intent.createChooser(sendIntent, "Title:"));
        try {
            startActivity(
                Intent.createChooser(
                    sendIntent,
                    "Send email using..."
                )
            )
        } catch (ex: ActivityNotFoundException) {
            gc.logl("No email clients installed.", true)
        }
    }

    fun adSendExitClick(view: View?) {
        finish()
    }

    fun adAddInfoClick(view: View) {
        repl += buildInfo()
        binding.edMessage.setText(repl)
        binding.edMessage.setSelection(repl.length)
    }
    fun adShareCrashClick(view: View) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type="text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, repl);
        startActivity(Intent.createChooser(shareIntent,getString(R.string.app_name)))
    }
    fun adCopyCrashClick(view: View) {
        gc.copyTextToClipboard(repl)
    }
}
