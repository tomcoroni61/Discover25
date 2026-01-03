package trust.jesus.discover.little

import android.app.Application
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import trust.jesus.discover.MainActivity
import trust.jesus.discover.R
import trust.jesus.discover.actis.BibleAy
import trust.jesus.discover.actis.Reportus
import trust.jesus.discover.bible.BblParseBook
import trust.jesus.discover.bible.online.Bss
import trust.jesus.discover.bible.online.Kbolls
import trust.jesus.discover.bible.online.Votd
import trust.jesus.discover.dlg_data.AppVals
import trust.jesus.discover.dlg_data.CsvList
import trust.jesus.discover.dlg_data.Dateien
import trust.jesus.discover.dlg_data.SaveLoadHelper
import trust.jesus.discover.little.FixStuff.Filenames.Companion.crashLogName
import trust.jesus.discover.little.FixStuff.Filenames.Companion.logMaxLines
import trust.jesus.discover.little.FixStuff.Filenames.Companion.logName
import trust.jesus.discover.little.recognio.SpeechEx
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Random
import kotlin.toString


class Globus: Application() {


    //public lateinit var sharedPrefs: SharedPreferences is line 135

    //Achtung classen mit gc=... immer über fun xxx
    fun bBlparseBook(): BblParseBook? {
        if (mBblParseBook == null) mBblParseBook = BblParseBook()
        return mBblParseBook
    }
    private var mBblParseBook: BblParseBook? = null
    var mSpeechEx: SpeechEx? = null
    fun vtdo(): Votd? {
        if (mvtdo==null) mvtdo = Votd()
        return mvtdo
    }
    private var mvtdo: Votd? = null

    //private var mjsonList = JsonList()

    fun bolls(): Kbolls? {
        if (mKbolls == null) mKbolls = Kbolls()
        return mKbolls
    }
    private var mKbolls: Kbolls? = null

    fun bibleSuperSearch(): Bss? {
        if (mBss == null) mBss = Bss()
        return mBss

    }
    private var mBss: Bss? = null


    fun ttSgl(): TTSgl? {
        if (mTTSgl == null) mTTSgl = TTSgl()
        return mTTSgl
    }
    private var mTTSgl: TTSgl? = null

    fun SLH(): SaveLoadHelper? {
        if (slh == null) {
            slh = SaveLoadHelper()
        }
        return slh
    }
    private var slh: SaveLoadHelper? = null

    fun dateien(): Dateien {
        if (mDateien == null)  mDateien = Dateien()
        return mDateien!!
    }
    private var mDateien: Dateien? = null

    fun appVals(): AppVals {
        if (mAppVals == null)  mAppVals = AppVals()
        return mAppVals!!
    }
    private var mAppVals: AppVals? = null

    fun globDlg(): GlobDlgs {
        if (mGlobDlgs == null)  mGlobDlgs = GlobDlgs()
        return mGlobDlgs!!
    }
    private var mGlobDlgs: GlobDlgs? = null

    fun seekList(): SeekList {
        if (mSeekList == null)  mSeekList = SeekList()
        return mSeekList!!
    }
    private var mSeekList: SeekList? = null

    fun csvList(): CsvList? {
        if (mCsvList == null) {
            mCsvList = CsvList()
            val fName = spruchFileName()
            //log("load spruch: $fName")
            if (!mCsvList!!.readFromPrivate(fName, '#')) {
                if (appVals().valueReadBool("welcome",false))
                    logl("$fName not found, read Org now", true)
                dateien().assetFileToPrivate(fName.toString())
                //mCsvList!!.readFromAssets(getString(R.string.spruch_csv), "#")
                //mCsvList!!.saveToPrivate(fName, '#')
                if (!mCsvList!!.readFromPrivate(fName, '#')) {
                    //globDlg().messageBox("failed to load example verses")
                    globDlg().messageBox("!!failed to load verslist: $fName !!", this)
                }
            }
            //mCsvList!!.readFromAssets(getString(R.string.spruch_csv), "#")
        }
        return mCsvList
    }
    private var mCsvList: CsvList? = null

    fun spruchFileName(): String? {
        //log("spruchFileName")
        return appVals().valueReadString("eCurDataFile", getString(R.string.spruch_csv))

    }

    @JvmField
    val versHistory = VersHistory()

    //lazy ging "halb", dann doch crash
    val lernItem = GlobVers() //10.25 klasse.. : GlobVers by lazy { GlobVers() }
    var lernDataIdx = 0

    var sharedText: String? = null
    @JvmField
    val random = Random()
    @JvmField
    var mainActivity: MainActivity? = null
    var sdf: SimpleDateFormat = SimpleDateFormat(
        "EE dd-MM-yyyy HH:mm:ss",
        Locale.getDefault()
    ) //sdf.format .. Ok for till today, but +1hour ab tomorrow ne,ne genau Sommerzeit erwischt.. Anzeige nur diesen Tag 'falsch'

    lateinit var sharedPrefs: SharedPreferences
//lateinit' is great for non-nullable variables.
    // 👉 'lazy' is perfect when you're dealing with a complex or expensive initialization
// @JvmField Instructs the Kotlin compiler not to generate getters/setters for this property and expose it as a field





    var curFragment: String = "AyWords"
    var curFragment_idx = 0
    companion object {
        @JvmStatic
        fun getAppContext(): Context {
            return appContext!!
        }

        fun getHeight(t: View, w: Int): Int {
            //t.setWidth(w);AT_MOST
            val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.AT_MOST)
            val heightMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            t.measure(widthMeasureSpec, heightMeasureSpec)
            return t.measuredHeight
        }

        private var appContext: Context? = null
        private const val LOG_TAG = "guide me"
    }

    //var sdf: SimpleDateFormat = SimpleDateFormat("EE dd-MM-yyyy HH:mm:ss", Locale.GERMANY)
    fun logl(msg: String, mitToast: Boolean) {
        Log.d(LOG_TAG, msg)
        if (mitToast) toast(msg)
    }
    fun logIntern(msg: String, mitToast: Boolean) {
        SLH()?.insertLine(logName, msg, "    " + sdf.format(System.currentTimeMillis()), logMaxLines)
        logl(msg, mitToast)
    }
    fun log(msg: String) {
        logl(msg, false)
    }
    fun toast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
    fun crashLog(ex: Exception, curLineNum: Int) {//msg: String
        val sw = StringWriter()
        ex.printStackTrace(PrintWriter(sw))
        val msg = ex.message.toString()
        logl(msg, true)
        SLH()!!.insertLine(
            logName, "id " +
                    curLineNum + "  c: " + crashCnt + sdf.format(System.currentTimeMillis()),
            "Crasherl: $msg", logMaxLines
        )
//        toast("Crasherl: $msg") e: Throwable
        crashCnt++
        startErrorReporter(sw.toString())
    }
    var canLogCrash = true
    fun crashLog(msg: String, curLineNum: Int) {//
        logl(msg, true)
        try {
            if (!canLogCrash) return
            SLH()!!.insertLine(
                logName, "id " +
                        curLineNum + "  c: " + crashCnt + sdf.format(System.currentTimeMillis()),
                "Crasherl: $msg", logMaxLines
            )
        } catch (e: Exception) {
            canLogCrash = false
        }
//        toast("Crasherl: $msg") e: Throwable
        crashCnt++
    }
    var crashCnt = 0  //about crash:
    //https://stackoverflow.com/questions/32229170/catch-all-possible-android-exception-globally-and-reload-application
    //https://medium.com/@hiren6997/effective-error-handling-in-android-from-try-catch-hell-to-modern-solutions-4fe5836c419c
    fun handleUncaughtException(e: Throwable) {
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        crashLog("handleUncaughtException: " + e.message, 352)
        var msg = "short: " + e.message.toString()
        msg += "\n full: \n$sw"
        dateien().writePrivateFile(crashLogName, msg)
        // !! in manifest eintragen...
        //startErrorReporter(sw.toString())
    }

    fun startErrorReporter(msg: String) {
        try {
            //log("startErrorReporter: " + msg.substring(44))
            val intent = Intent(this, Reportus::class.java)
            intent.putExtra("ErrMsg", msg) //or..Intent.EXTRA_TEXT
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }  catch (ex: Exception) {
        //Log.e(mPackageName, ex.message!!)
        log("ex: " + ex.message)
    }
    }

    fun formatTextUpper(str: String): String {
        return formatText(str).uppercase(Locale.getDefault())
    }

    fun doSpeaktext(txt: String): String {
        val sz = StringBuilder()
        var ignore = false;
        for (i in 0..<txt.length) {
            val c = txt[i]
            when (c) {
                '(', '[', '<', '{' -> ignore = true
                ')', ']', '>', '}' -> {
                    ignore = false
                    continue
                }
            }
            if (ignore) continue
            sz.append(c)
        }
        return if (sz.isNotEmpty()) {
            sz.toString()
        } else {
            txt
        }
    }


    fun formatText(strIn: String): String {
        var str = strIn.trim { it <= ' ' }
        val sz = StringBuilder()

        //str=str.toUpperCase(Locale.getDefault());
        for (i in 0..<str.length) {
            val c = str[i]
            if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                sz.append(c)
            } else when (c) {
                'ö', 'Ö', 'ä', 'Ä', 'ü', 'Ü', 'ß' -> sz.append(c)
                //' ' -> if (sz.length > 1 && sz.get(sz.length - 1) != ' ') sz.append(c)
                '\n' -> sz.append(' ')
                else -> sz.append(' ')
            }
        }
        str = sz.toString()
        while (str.indexOf("  ") >= 0) str=str.replace("  ", " ")
        return str
    }

    fun copyTextToClipboard(text: String?) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("text", text)
        clipboard.setPrimaryClip(clip)
        toast("text copied to clipboard")
    }



    fun doKeepScreenOn(keepOn: Boolean) {
        if (keepOn) mainActivity!!.window.addFlags(
            android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        ) else mainActivity!!.window.clearFlags(
            android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
    }
    fun activityStart(context: Context?, cls: Class<*>?) {
        try {

            var ctx = context
            if (ctx == null) ctx = applicationContext
            val intent = Intent(ctx, cls)
            //if (context==null) //immer neu damit back to main .. hilft nicht finishLastActivity
            //always FLAG_ACTIVITY_NEW_TASK for vivo crashes + autoclose last activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(intent)
        } catch (e: Exception) {
            crashLog("Start Bug ex: " + e.message, 352)//errReport(e, " ", true)
        }
    }
    fun startBibleActivity(startVers: Int = 0) {
        val intent = Intent(mainActivity, BibleAy::class.java)
        if (startVers > 0) intent.putExtra("startVers", startVers)
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mainActivity!!.startActivity(intent)
        //gc.activityStart(activity, BibleAy::class.java)
    }

    fun isScreenOn(): Boolean {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isInteractive
    }
    /*
    public boolean isScreenOn(Context context) {
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
        DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        boolean screenOn = false;
        for (Display display : dm.getDisplays()) {
            if (display.getState() != Display.STATE_OFF) {
                screenOn = true;
            }
        }
        return screenOn;
    } else {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //noinspection deprecation
        return pm.isScreenOn();
    }
}
     */

    /*
    https://stackoverflow.com/questions/32229170/catch-all-possible-android-exception-globally-and-reload-application
        private var oldHandler: Thread.UncaughtExceptionHandler? = null
        private fun globalExceptionHandler() { //try in MainActivity attachUnhandledExceptionHandler
            //if (!BuildConfig.DEBUG)            return;
            if (oldHandler != null) return Throwable to Exception
            oldHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { t: Thread?, e: Throwable? ->
                try {
                    handleUncaughtException(e!!)
                } catch (ex: Exception) {
                    // ex.printStackTrace();
                    crashLog("ex: " + ex.message, 295)
                } finally {
                    if (oldHandler != null) oldHandler!!.uncaughtException(t!!, e!!)
                    else exitProcess(1)
                    //System.exit();
                }
            }
        }

     */
    private var startCount = 0
    override fun onCreate() {
        super.onCreate() //Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        appContext = applicationContext
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        startCount++
        //globalExceptionHandler()
        //new Reportus("Nice to meet you");
        logIntern("app starts: $startCount time(s)", false) //if (startCount > 1)
        appContext?.cacheDir?.deleteOnExit() // .deleteRecursively()

    }
}