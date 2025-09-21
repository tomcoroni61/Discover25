package trust.jesus.discover.actis

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
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

class Reportus : AppCompatActivity() {
    private val gc: Globus = Globus.getAppContext() as Globus
    private var repl: String? = "Error"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE) //comment this line if you need to show Title.
        setContentView(R.layout.ay_mail_me)
        //after many tries this fits R.id.main between systembars.. todo in all activities  android:id="@+id/main"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val intent = getIntent()
        if (intent.hasExtra("ErrMsg")) repl = intent.getStringExtra("ErrMsg")
        val et = findViewById<View>(R.id.edMessage) as TextView //findViewById<EditText?>(R.id.edMessage)
        et.text = repl
    }


    fun adSendMailClick(view: View?) {
        SendErrorMail(this.baseContext, repl)
    }

    private fun SendErrorMail(_context: Context, ErrorContent: String?) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        val subject = (_context.resources.getString(R.string.CrashReport_MailSubject)
                + _context.resources.getString(R.string.app_name))
        val body = _context.resources.getString(R.string.CrashReport_MailBody) +
                "\n\n" +
                ErrorContent +
                "\n\n"
        sendIntent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf<String?>(_context.getString(R.string.CrashReportEmailTo))
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
            gc.Logl("No email clients installed.", true)
        }
    }

    fun adSendExitClick(view: View?) {
        finish()
    }
}
