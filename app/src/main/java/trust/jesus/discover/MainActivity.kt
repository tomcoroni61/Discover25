package trust.jesus.discover

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.sidesheet.SideSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import trust.jesus.discover.databinding.ActivityMainBinding
import trust.jesus.discover.fragis.FragAdapter
import trust.jesus.discover.little.Globus
import trust.jesus.discover.actis.AyWelcome
import trust.jesus.discover.actis.BibleAy
import trust.jesus.discover.actis.ErrorHandler
import trust.jesus.discover.actis.Reportus
import trust.jesus.discover.databinding.SheetMainBinding
import trust.jesus.discover.dlg_data.TtsDlg
import trust.jesus.discover.little.FixStuff.Filenames.Companion.merkVers

class MainActivity : AppCompatActivity() {


    val default = 0;    val GREY = 1;    val DARK = 2;    val BLUE = 3
    val CYAN: Int = 4;    val GREEN: Int = 5;    val OCHER: Int = 6
    val ORANGE: Int = 7;    val PURPLE: Int = 8;    val RED: Int = 9
    val YELLOW: Int = 10;    val NIGHT = 11

    lateinit var binding: ActivityMainBinding
    val gc: Globus = Globus.getAppContext() as Globus
    var viewPager: ViewPager2? = null
    var sectionsPagerAdapter: FragAdapter? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) MODE_NIGHT_FOLLOW_SYSTEM MODE_NIGHT_NO
        // geht zu res/values/ (night) themes.xml
        //attachUnhandledExceptionHandler()
        // Thread.setDefaultUncaughtExceptionHandler(MyUncaughtExceptionHandler(applicationContext));
        setAppTheme()
        super.onCreate(savedInstanceState)
        ErrorHandler.toCatch(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        // or lock it to landscape
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        binding = ActivityMainBinding.inflate(layoutInflater)
        setAppTheme(false, true)
        // Standardmäßig macht enableEdgeToEdge() die Systemleisten transparent.
        enableEdgeToEdge()
        setContentView(binding.root)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        //after many tries this fits R.id.main between systembars.. todo in all activities  android:id="@+id/main"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        throw IllegalArgumentException("Crashtest in WordFrag")


        //setTheme(R.style.Theme_Discover)
        //val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        sectionsPagerAdapter = FragAdapter(this)
        doPageCount()
        viewPager = binding.viewPager
        viewPager!!.adapter = sectionsPagerAdapter
        //sectionsPagerAdapter!!.viewPager = viewPager
        //viewPager!!.currentItem = 0 viewPager!!.setCurrentItem(6, false)

        if (viewPager != null) {
            TabLayoutMediator(binding.tabs, viewPager!!, true, false) { tab, position ->
                tab.text =  sectionsPagerAdapter?.getTitle(position) //"OBJECT ${(position + 1)}"
            }.attach()


            //pagechange also in adapter getItemId(position: Int): Long {
        }


        //tabs.setupWithViewPager(viewPager)
        //supportActionBar?.hide() ne..
        gc.ttSgl()?.speak(" ")
        val ce = gc.appVals().valueReadString("currentEngine", "n")
        if (ce != "n") {
            gc.ttSgl()?.defEngine = ce.toString()
            gc.ttSgl()!!.restart()
        }
        gc.mainActivity = this
        //gc.log("MainActivity onCreate")
        handleReceiveShare()
        if (gc.lernItem.text.length < 9)
            gc.csvList()!!.getRandomText()
        //gc.log("MainActivity onCreate  222")
        val idi = gc.appVals().valueReadInt("restartFrag", viewPager!!.currentItem)
        if (idi>-1) viewPager!!.setCurrentItem(idi, false)
        gc.appVals().valueWriteInt("restartFrag", -1)
    }
    override fun onResume() {
        gc.mainActivity = this
        super.onResume()
    }

    /*
        private var oldHandler: Thread.UncaughtExceptionHandler? = null

        private val exceptionHandler =
            Thread.UncaughtExceptionHandler { t: Thread, e: Throwable ->

                try {
                    gc.handleUncaughtException(e)
                } catch (ex: Exception) {
                    // ex.printStackTrace();
                    gc.crashLog("ex: " + ex.message, 119)
                } finally {
                    if (oldHandler != null) oldHandler!!.uncaughtException(t, e)
                    else exitProcess(1)
                    //System.exit();
                }
            }

        private fun attachUnhandledExceptionHandler() {
            if (BuildConfig.DEBUG.not()) {
                gc.log("linking to UncaughtExceptionHandler oldHandler " + (oldHandler!=null))
                if (oldHandler != null) return
                oldHandler = Thread.getDefaultUncaughtExceptionHandler()
                Thread.setDefaultUncaughtExceptionHandler(exceptionHandler)
            }
        }


     */

    fun doPageCount() {
        val level = gc.appVals().valueReadString("appLevel", "base")
        when (level) {
            "base" -> sectionsPagerAdapter!!.pageCount = 4
            "mid"  -> sectionsPagerAdapter!!.pageCount = 6 //5=Letters
            "max"  -> {
                sectionsPagerAdapter!!.pageCount = 8
                if (gc.appVals().valueReadBool("LogFrag", false))
                    sectionsPagerAdapter!!.pageCount = 9
            } //9=Log
        }

        // sectionsPagerAdapter!!.pageCount = 8 else sectionsPagerAdapter!!.pageCount = 7
    }
    private var displayName: String? = null

    private fun handleReceiveShare() {
        val intent = getIntent()
        val action =  intent.action // https://developer.android.com/training/sharing/receive?hl=de
        /* if (Intent.ACTION_SEND_MULTIPLE.equals(action)
        for multiple files ..in Manifest add:
        <intent-filter>
                <action android:name="android.intent.action.SEND_MULTIPLE"/>
                <data android:mimeType="* __/ *"/>
                <category android:name="android.intent.category.DEFAULT"/>
            </intent-filter>
        single: <action android:name="android.intent.action.SEND" />
Thread.setDefaultUncaughtExceptionHandler(MyUncaughtExceptionHandler(applicationContext));

         */
        if (Intent.ACTION_SEND != action) return

        //  MIME-Typen:  text/*  alle wie text/plain .. rtf,html .. text/json
//        String type = intent.getType(); // image/*
//        if ("text/plain".equals(type)) {
        gc.sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (gc.sharedText != null) {
            gc.globDlg().askDlg(
                getString(R.string.add_to_your_bibleverses), gc.sharedText, this
            ) { gc.appVals().valueWriteString("appLevel", "max")
                doPageCount()
                viewPager!!.setCurrentItem(sectionsPagerAdapter?.pIdxEntries!!, false) }
        }

        val uri = intent.data
        if (uri != null) gc.globDlg().askDlg("not supported", "uri found: $uri", this,null)

        val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_STREAM)
        }
        //imageUri = intent.getParcelableExtra<Uri?>(Intent.EXTRA_STREAM)
        if (imageUri != null) {
            displayName = gc.dateien().getDisplayName(imageUri)
            gc.globDlg().askDlg(getString(R.string.copy_to_app_file_dir) + "\n" + displayName, this
            ) {
                if (gc.dateien().hasPrivateFile(displayName!!)) gc.globDlg().askDlg(
                    getString(R.string.overwrite) + displayName, this
                ) {
                    gc.dateien().copyFileToPrivate(imageUri, displayName)
                } else gc.dateien().copyFileToPrivate(imageUri, displayName)
            }
        }
    }
    private val PICKFILE_RESULT_CODE = 123
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICKFILE_RESULT_CODE -> if (resultCode == RESULT_OK) {
                val uri = data?.data
                val displayName = gc.dateien().getDisplayName(uri)

                if (gc.dateien()
                        .hasPrivateFile(displayName!!)
                ) gc.globDlg().askDlg(getString(R.string.overwrite) + displayName, this) {
                    gc.dateien().copyFileToPrivate(uri!!, displayName)
                } else gc.dateien().copyFileToPrivate(uri!!, displayName)
            }

            23 -> {}
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    fun btnSpeakClick(view: View) {//muß view: View!!
        gc.ttSgl()?.cleanSpeak(gc.lernItem.text)
    }


    fun setBibleTheme(activity: BibleAy, setColors: Boolean = false) {
        setActivityTheme(activity)
        if (setColors) {
            val typedValue = TypedValue()
            activity.theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
            val color = ContextCompat.getColor(this, typedValue.resourceId)
            activity.binding.main.setBackgroundColor(color)
        }

    }
    fun setWelcomeTheme(activity: AyWelcome, setColors: Boolean = false) {
        setActivityTheme(activity)
        if (setColors) {
            val typedValue = TypedValue()
            activity.theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
            val color = ContextCompat.getColor(this, typedValue.resourceId)
            activity.binding.main.setBackgroundColor(color)
        }
    }

    fun setReportusTheme(activity: Reportus, setColors: Boolean = false) {
        setActivityTheme(activity)
        if (setColors) {
            val typedValue = TypedValue()
            activity.theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
            val color = ContextCompat.getColor(this, typedValue.resourceId)
            activity.binding.main.setBackgroundColor(color)
        }
    }
    fun setActivityTheme(activity: AppCompatActivity) {
        val themeIdi = gc.appVals().valueReadInt("theme", BLUE)//read only here OCHER
        when (themeIdi) {
            default -> {
                activity.setTheme(R.style.AppLightTheme)
                //ret = false
            }
            GREY -> activity.setTheme(R.style.AppGrayTheme)
            DARK -> activity.setTheme(R.style.AppDarkTheme) //=BLACK -> setTheme(R.style.AppBlackTheme)
            BLUE -> activity.setTheme(R.style.AppBlueTheme)
            CYAN -> activity.setTheme(R.style.AppCyanTheme)
            GREEN -> activity.setTheme(R.style.AppGreenTheme)
            OCHER -> activity.setTheme(R.style.AppOcherTheme)
            ORANGE -> activity.setTheme(R.style.AppOrangeTheme)
            PURPLE -> activity.setTheme(R.style.AppPurpleTheme)
            RED -> activity.setTheme(R.style.AppRedTheme)
            YELLOW -> activity.setTheme(R.style.AppYellowTheme)
            NIGHT -> activity.setTheme(R.style.AppThemeNight)
            else -> activity.setTheme(R.style.AppThemeNight)
        }

    }
    fun setAppTheme(doRecreate: Boolean = false, setColors: Boolean = false) {
        try {
            setActivityTheme(this)
            if (  setColors) {
                val typedValue = TypedValue()
                theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
                var color = ContextCompat.getColor(this, typedValue.resourceId)
                binding.main.setBackgroundColor(color)
                binding.tabs.setBackgroundColor(color)
                //binding.btnMenu.setBackgroundColor(color)

                theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
                color = ContextCompat.getColor(this, typedValue.resourceId)
                theme.resolveAttribute(R.attr.tabSelected_colour, typedValue, true)
                val selcolor = ContextCompat.getColor(this, typedValue.resourceId)
                binding.tabs.setTabTextColors(color, selcolor)

                binding.tabs.setSelectedTabIndicatorColor(selcolor)
                //binding.tvmenu.setTextColor(color)
                //binding.btnNavDlg.setTextColor(color)
                binding.btnSpeak.setTextColor(color)
                //txtColor = color

            }
            //gc.Logl( "setAppTheme $theme", true)
            if (doRecreate) {
                restartApp()
            }
        } catch (e: Exception) {
            gc.logl(e.toString(), true)
        }
    }

    private fun restartApp() { //viewPager!!.currentItem = 0 viewPager!!.setCurrentItem(6, false)
        gc.appVals().valueWriteInt("restartFrag", viewPager!!.currentItem)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finishAffinity()
    }

/*    private val RECORD_AUDIO_REQUEST_CODE = 101
val unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.baseline_more_vert_36)
                val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
                DrawableCompat.setTint(wrappedDrawable, color )
                binding.tvmenu.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, null, wrappedDrawable, null)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RECORD_AUDIO_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // startRecognition()
                }
            }
        }
    }


    fun openActivityForResult() {
        startForResult.launch(Intent(this, MainActivity::class.java))
    }


    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            // Handle the Intent
            //do stuff here
        }
    }

var txtColor = 0
    fun setMenuImg(kein: Boolean) {
        if (kein) {
            binding.tvmenu.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        } else {
            val unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.baseline_more_vert_36)
            val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
            DrawableCompat.setTint(wrappedDrawable, txtColor )
            binding.tvmenu.setCompoundDrawablesWithIntrinsicBounds(wrappedDrawable, null, wrappedDrawable, null)
        }
    }
    */
fun doThemeSheet() {
    val themeNames: Array<String> = arrayOf(
        getString(R.string.default_light_theme),
        getString(R.string.grey),
        getString(R.string.dark),
        getString(R.string.blue),
        getString(R.string.cyan),
        getString(R.string.green),
        getString(R.string.ocher),
        getString(R.string.orange),
        getString(R.string.purple),
        getString(R.string.red),
        getString(R.string.yellow),
        getString(R.string.default_night_theme),



        )
    val dialog = SideSheetDialog(this)
    val view = layoutInflater.inflate(R.layout.sheet_themes, null)
    val list: ListView = view.findViewById(R.id.listView)
    val arr: ArrayAdapter<String> =
        ArrayAdapter(this, android.R.layout.simple_list_item_1, themeNames)
    list.adapter = arr

    list.setOnItemClickListener { parent, view, position, id ->
        gc.appVals().valueWriteInt("theme", position)
        view.setBackgroundColor(android.graphics.Color.GREEN)
        gc.mainActivity?.setAppTheme(true)
        // dialog.dismiss()
    }

    dialog.setContentView(view)
    //ne list.setItemChecked(gc.appVals().valueReadInt("theme", 0), true)
    //list.setSelection( gc.appVals().valueReadInt("theme", 0))
    dialog.show()
    //list.setSelection( gc.appVals().valueReadInt("theme", 0))

    Handler(Looper.getMainLooper()).postDelayed({
        val item = list[gc.appVals().valueReadInt("theme", 6)] //= OCHER = def 12.25
        val typedValue = TypedValue()
        gc.mainActivity!!.theme.resolveAttribute(R.attr.tabSelected_colour, typedValue, true)
        val color = ContextCompat.getColor(this, typedValue.resourceId)
        item.setBackgroundColor(color)
    }, 100)
}

    private lateinit var sheetMain: SheetMainBinding;


    private fun doMainSheet() {
        val dialog = SideSheetDialog(this)
        val inflater = LayoutInflater.from(this)

        sheetMain = SheetMainBinding.inflate(inflater)
        dialog.setContentView(sheetMain.root)
        dialog.setSheetEdge(Gravity.START)
        sheetMain.tvTheme.setOnClickListener {
            doThemeSheet()
            dialog.dismiss()
        }
        sheetMain.tvVersHistory.setOnClickListener {
            gc.globDlg().showPopupWin( gc.lernItem.text)
            dialog.dismiss()
        }
        sheetMain.tvheidi.setOnClickListener {
            this.finishAffinity()
        }

        val level = gc.appVals().valueReadString("appLevel", "base")
        when (level) {
            "base" -> sheetMain.scBase.isChecked = true
            "mid"  -> sheetMain.scMid.isChecked = true
            "max"  -> sheetMain.scMax.isChecked = true
        }

        sheetMain.scBase.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) gc.appVals().valueWriteString("appLevel", "base")
            restartApp()
        }
        sheetMain.scMid.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) gc.appVals().valueWriteString("appLevel", "mid")
            restartApp()
        }
        sheetMain.scMax.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) gc.appVals().valueWriteString("appLevel", "max")
            restartApp()
        }
        sheetMain.chipLog.isChecked = gc.appVals().valueReadBool("LogFrag", false)
        sheetMain.chipLog.setOnCheckedChangeListener{ _, isChecked ->
            gc.appVals().valueWriteBool("LogFrag", isChecked)
            restartApp()
        }

        readMerkvers()
        sheetMain.btnGet.setOnClickListener{
            val vers = sheetMain.tvMerkVers.text.toString()
            val idx: Int = gc.csvList()!!.hasBibleVers(vers)
            if (idx < 0) {//matvLerni
                val txt = "$vers nicht gefunden"
                sheetMain.tvMerkVers.text = txt
                return@setOnClickListener
            }
            sectionsPagerAdapter?.doMerkVers(0, idx)
            //sheetMain.tvGlobalVers.text = gc.lernItem.vers
            dialog.dismiss()
        }
        sheetMain.btnSet.setOnClickListener{
            gc.appVals().valueWriteString(merkVers, gc.lernItem.vers)
            readMerkvers()
            dialog.dismiss()
        }
        sheetMain.tvWelcome.setOnClickListener{
            gc.activityStart(this, AyWelcome::class.java)
            dialog.dismiss()
        }
        sheetMain.tvBible.setOnClickListener{
            gc.lernItem.chapter.clear()
            gc.activityStart(this, BibleAy::class.java)
            dialog.dismiss()
        }
        sheetMain.tvChangeLoge.setOnClickListener{
            val cc = gc.dateien().getAssetText("changelog.txt")
            gc.globDlg().messageBox(cc, this)
            dialog.dismiss()
        }
        sheetMain.tvErrorReporter.setOnClickListener{
            gc.startErrorReporter("Please insert Your message here")
            dialog.dismiss()
        }
        sheetMain.ivVoices.setOnClickListener{
            val ttsDlg = TtsDlg(this, R.style.AlertDialogCustom)
            ttsDlg.show()
            //gc.ttSgl()?.andoSettings()
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun readMerkvers() {
        val vers = gc.appVals().valueReadString(merkVers, "joh 3:16")
        sheetMain.tvMerkVers.text = vers
        sheetMain.tvGlobalVers.text = gc.lernItem.vers
    }
    fun btnMainMenuClick(view: View) {//muß view: View!!
        doMainSheet()
           // setCurrentItem(3, false)
            //sectionsPagerAdapter!!.doBottomSheet(sectionsPagerAdapter!!.tabPosition)
    }
}