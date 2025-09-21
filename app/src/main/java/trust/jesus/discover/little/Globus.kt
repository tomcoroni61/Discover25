package trust.jesus.discover.little

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.preference.PreferenceManager
import trust.jesus.discover.bible.online.Kbolls
import trust.jesus.discover.MainActivity
import trust.jesus.discover.R
import trust.jesus.discover.actis.BibleAy
import trust.jesus.discover.actis.Reportus
import trust.jesus.discover.bible.BblParseBook
import trust.jesus.discover.dlg_data.AppVals
import trust.jesus.discover.dlg_data.CsvData
import trust.jesus.discover.dlg_data.CsvList
import trust.jesus.discover.dlg_data.Dateien
import trust.jesus.discover.dlg_data.ErrorReporter
import trust.jesus.discover.dlg_data.SaveLoadHelper
import trust.jesus.discover.little.FixStuff.Filenames.Companion.LogMaxLines
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Random
import kotlin.system.exitProcess
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowMetrics


class Globus: Application() {


    //public lateinit var sharedPrefs: SharedPreferences is line 135

    //Achtung classen mit gc=... immer über fun xxx
    fun parseBook(): BblParseBook? {
        if (mBblParseBook == null) mBblParseBook = BblParseBook()
        return mBblParseBook
    }
    private var mBblParseBook: BblParseBook? = null

    fun jsonList(): JsonList? {
        if (mJsonList == null) mJsonList = JsonList()
        return mJsonList
    }
    private var mJsonList: JsonList? = null

    //private var mjsonList = JsonList()

    fun bolls(): Kbolls? {
        if (mKbolls == null) mKbolls = Kbolls()
        return mKbolls
    }
    private var mKbolls: Kbolls? = null

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
            val fname = spruchFileName()
            if (!mCsvList!!.readFromPrivate(fname, '#')) {
                if (appVals().valueReadBool(
                        "welcome",
                        false
                    )
                ) Logl(fname + " not found, read Org now", true)
                mCsvList!!.readFromAssets(getString(R.string.spruch_csv), "#")
                mCsvList!!.saveToPrivate(fname, '#')
            }
            //mCsvList!!.readFromAssets(getString(R.string.spruch_csv), "#")
        }
        return mCsvList
    }
    private var mCsvList: CsvList? = null

    fun spruchFileName(): String? {
        log("spruchFileName")
        return appVals().valueReadString("eCurDataFile", getString(R.string.spruch_csv))

    }

    @JvmField
    val versHistory = VersHistory()

    @JvmField
    val lernItem = CsvData() //must val!!
    var lernDataIdx = 0

    fun setVersTitel(versTitel: String?) {
        mainActivity!!.binding.tvVersTop.text = versTitel
    }
    var sharedText: String? = null
    @JvmField
    val random = Random()
    @JvmField
    var mainActivity: MainActivity? = null
    var sdf: SimpleDateFormat = SimpleDateFormat(
        "EE dd-MM-yyyy HH:mm:ss",
        Locale.GERMANY
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
    fun Logl(msg: String, mitToast: Boolean) {
        Log.d(LOG_TAG, msg)
        if (mitToast) toast(msg)
    }
    fun log(msg: String) {
        Logl(msg, false)
    }
    fun toast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
    fun CrashLog(msg: String, curLineNum: Int) {
        Logl(msg, false)
        SLH()!!.insertLine(
            FixStuff.Filenames.log, "at Line " +
                    curLineNum + "   " + sdf.format(System.currentTimeMillis()),
            "Crasherl: $msg", LogMaxLines
        )
//        toast("Crasherl: $msg")
        crashcnt++
    }
    var crashcnt = 0

    fun checkPermissions(askUser: Boolean, vararg permissionsId: String): Boolean {
        var permissions = true
        for (p in permissionsId) { //permissions &&
            permissions = ContextCompat.checkSelfPermission(
                mainActivity!!,
                p
            ) == PermissionChecker.PERMISSION_GRANTED

            val callbackId = 0 //42 falls user ablehnt, hier egal requestPermissions
            if (askUser && !permissions) {
                //if (permissions)                Toast.makeText(mainActi, Arrays.toString(permissionsId) +" ok, change in Settings", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(mainActivity!!, permissionsId, callbackId)
                //permissions = false;
            }
        }

        return permissions
    }


    fun formatTextUpper(str: String): String {
        return formatText(str).uppercase(Locale.getDefault())
    }

    fun formatText(strIn: String): String {
        var str = strIn.trim { it <= ' ' }
        val sz = StringBuilder()

        //str=str.toUpperCase(Locale.getDefault());
        for (i in 0..<str.length) {
            val c = str.get(i)
            if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                sz.append(c)
            } else when (c) {
                'ö', 'Ö', 'ä', 'Ä', 'ü', 'Ü', 'ß' -> sz.append(c)
                ' ' -> if (sz.length > 1 && sz.get(sz.length - 1) != ' ') sz.append(c)
                '\n' -> sz.append(' ')
                else -> sz.append(' ')
            }
        }
        str = sz.toString().replace(" {2}".toRegex(), " ")
        return str
    }

    fun interface AskDlgOkEve {
        fun onOkClick()
    }

    fun askDlg(ask: String?, askDlgOkEve: AskDlgOkEve?) {
        askDlg(getString(R.string.app_name), ask, askDlgOkEve)
    }

    fun askDlg(title: String?, ask: String?, askDlgOkEve: AskDlgOkEve?) {
        var ask = ask
        val tv = TextView(mainActivity!!)
        ask = "\n" + ask
        tv.text = ask
        tv.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        tv.gravity = Gravity.CENTER
        AlertDialog.Builder(mainActivity!!)
            .setTitle(title)
            .setView(tv)
            .setPositiveButton(
                "Ok"
            ) { dialog: DialogInterface?, id: Int ->
                //finish();
                askDlgOkEve!!.onOkClick()
            }
            .setNegativeButton("No", null).show()
        //d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        //alertDialog.show();
    }

    val popUpWidth: Int
        get() {
            var (wid, screenHeight) = getScreenDimensions()
            wid = wid - (wid / 15)
            return wid
        }
    fun getScreenDimensions(): Pair<Int, Int> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            // Get the window metrics
            val windowMetrics: WindowMetrics = mainActivity!!.windowManager.currentWindowMetrics
            // Get the insets and bounds of the window
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            // Calculate the width and height of the screen
            val bounds = windowMetrics.bounds
            val width = bounds.width() - insets.left - insets.right
            val height = bounds.height() - insets.top - insets.bottom
            // Return the width and height
            Pair(width, height)
        } else {
            // For Android 10 and below
            // Get the display metrics
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            // Get the default display metrics
            mainActivity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
            // Return the width and height
            Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
    }
  /*  @JvmOverloads
    fun showPopupWin(v: View?, txt: String?, askDlgOkEve: AskDlgOkEve? = null) {
        val wid = this.popUpWidth
        if (wid == 0) return
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.wpopup, null)
        popupView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val pw = PopupWindow(
            popupView, wid,  //MATCH_PARENT geht
            RelativeLayout.LayoutParams.WRAP_CONTENT, true
        )
        //final PopupWindow pup = new PopupWindow(pupLayout, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
// android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        val textView = popupView.findViewById<TextView?>(R.id.tv_popup)
        if (textView == null) return
        textView.text = txt

        var bt = popupView.findViewById<Button>(R.id.pwspeakOptclck)
        bt.setOnClickListener(View.OnClickListener { view1: View? ->
            // Toast.makeText(this, " clicked", Toast.LENGTH_LONG).show();
            pw.dismiss()
            ttSgl()?.andoSetttings()
            //activityStart(mainActi, AyWords::class.java)
        })

        //textView.setOnClickListener(view1 -> { keine wirkung pw.setHeight
        bt = popupView.findViewById<Button>(R.id.pwwordmix)
        if (curFragment.indexOf("AyWords") > 0) bt.setVisibility(View.INVISIBLE)
        bt.setOnClickListener(View.OnClickListener { view1: View? ->
            // Toast.makeText(this, " clicked", Toast.LENGTH_LONG).show();
            pw.dismiss()
            mainActivity!!.viewPager!!.setCurrentItem(2, true)
        })

        bt = popupView.findViewById<Button>(R.id.pwwordclck)
        if (curFragment.indexOf("AyClickW") > 0) bt.setVisibility(View.INVISIBLE)
        bt.setOnClickListener(View.OnClickListener { view1: View? ->
            // Toast.makeText(this, " clicked", Toast.LENGTH_LONG).show();
            pw.dismiss()
            mainActivity!!.viewPager!!.setCurrentItem(3, true)
        })

        bt = popupView.findViewById<Button>(R.id.pwMain)
        if (curFragment.indexOf("MainActivity") > 0) bt.setVisibility(View.INVISIBLE)
        bt.setOnClickListener(View.OnClickListener { view1: View? ->
            // Toast.makeText(this, " clicked", Toast.LENGTH_LONG).show();
            pw.dismiss()
            //activityStart(mainActi, MainActivity::class.java)
        })

        bt = popupView.findViewById<Button>(R.id.pwspeakclck)
        bt.setOnClickListener(View.OnClickListener { view1: View? ->
            ttSgl()?.speak(txt.toString())
        })

        bt = popupView.findViewById<Button>(R.id.pwworkclck)
        if (curFragment.indexOf("AyEntries") > 0) bt.setVisibility(View.INVISIBLE)
        bt.setOnClickListener(View.OnClickListener { view1: View? ->
            pw.dismiss()
            //activityStart(mainActi, AyEntries::class.java)
        })

        bt = popupView.findViewById<Button>(R.id.pwsaylck)
        if (curFragment.indexOf("AySpeech") > 0) bt.setVisibility(View.INVISIBLE)
        bt.setOnClickListener(View.OnClickListener { view1: View? ->
            pw.dismiss()
            mainActivity!!.viewPager!!.setCurrentItem(1, true)
        })

        bt = popupView.findViewById<Button>(R.id.pwletters)
        if (curFragment.indexOf("AyLetters") > 0) bt.setVisibility(View.INVISIBLE)
        bt.setOnClickListener(View.OnClickListener { view1: View? ->
            pw.dismiss()
            //activityStart(mainActi, AyLetters::class.java)
        })

        bt = popupView.findViewById<Button>(R.id.pwretry)
        if (askDlgOkEve == null) bt.setVisibility(View.INVISIBLE) else bt.setOnClickListener(View.OnClickListener { view1: View? ->
            pw.dismiss()
            askDlgOkEve.onOkClick()
        })

        pw.setAnimationStyle(android.R.anim.fade_in)
        val relativeLayout = popupView.findViewById<RelativeLayout>(R.id.rlpop)
        val hei: Int = getHeight(relativeLayout, wid) + 20
        pw.setHeight(hei) //+ (pw.getWidth()/4)
        pw.showAtLocation(v, Gravity.CENTER, 0, 0)
    }
*/
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
            errReport(e, "Start Bug ", true)
        }
    }
    fun startBibleActivity(startVers: Int = 0) {
        val intent = Intent(mainActivity, BibleAy::class.java)
        if (startVers > 0) intent.putExtra("startVers", startVers)
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mainActivity!!.startActivity(intent)
        //gc.activityStart(activity, BibleAy::class.java)
    }

    var errorReporter: ErrorReporter? = null

    fun errReport(e: Exception, msg: String?, doToast: Boolean) {
        if (msg != null) Logl(msg + " " + e.message, doToast)
        if (errorReporter != null) {
            //errorReporter.
            errorReporter!!.doException(e)
        }
    }

    private var oldHandler: Thread.UncaughtExceptionHandler? = null
    private fun globalExceptionHandler() {
        //if (!BuildConfig.DEBUG)            return;

        oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t: Thread?, e: Throwable? ->
            try {
                val sw = StringWriter()
                e?.printStackTrace(PrintWriter(sw))
                // !! in manifest eintragen...
                val intent = Intent(this, Reportus::class.java)
                intent.putExtra("ErrMsg", sw.toString()) //or..Intent.EXTRA_TEXT
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                CrashLog("ex: " + e!!.message, 286)
            } catch (ex: Exception) {
                // ex.printStackTrace();
                CrashLog("ex: " + ex.message, 295)
            } finally {
                if (oldHandler != null) oldHandler!!.uncaughtException(t!!, e!!)
                else exitProcess(1)
                //System.exit();
            }
        }
    }

    private var startCount = 0
    override fun onCreate() {
        super.onCreate() //Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        appContext = applicationContext
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        startCount++
        globalExceptionHandler()
        //new Reportus("Nice to meet you");
        if (startCount > 1) Logl("app starts: $startCount", true)
        appContext?.cacheDir?.deleteOnExit() // .deleteRecursively()

    }
}