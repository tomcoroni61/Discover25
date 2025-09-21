package trust.jesus.discover

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import trust.jesus.discover.databinding.ActivityMainBinding
import trust.jesus.discover.fragis.FragAdapter
import trust.jesus.discover.little.Globus
import trust.jesus.discover.actis.AyWelcome
import trust.jesus.discover.actis.BibleAy

class MainActivity : AppCompatActivity() {


    val Default = 0
    val GREY = 1
    val DARK = 2
    val BLUE = 3
    val CYAN: Int = 4
    val GREEN: Int = 5
    val OCHER: Int = 6
    val ORANGE: Int = 7
    val PURPLE: Int = 8
    val RED: Int = 9
    val YELLOW: Int = 10
    val NIGHT = 11

    lateinit var binding: ActivityMainBinding
    val gc: Globus = Globus.getAppContext() as Globus
    var viewPager: ViewPager2? = null
    var sectionsPagerAdapter: FragAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) MODE_NIGHT_FOLLOW_SYSTEM MODE_NIGHT_NO
        // geht zu res/values/ (night) themes.xml
        setAppTheme()
        super.onCreate(savedInstanceState)
        // Standardmäßig macht enableEdgeToEdge() die Systemleisten transparent.
        binding = ActivityMainBinding.inflate(layoutInflater)
        setAppTheme(false, true)
        enableEdgeToEdge()
        setContentView(binding.root)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        //after many tries this fits R.id.main between systembars.. todo in all activities  android:id="@+id/main"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        //setTheme(R.style.Theme_Discover)
        //val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        sectionsPagerAdapter = FragAdapter(this)
        doPageCount()
        viewPager = binding.viewPager
        viewPager!!.adapter = sectionsPagerAdapter

        if (viewPager != null) {
            TabLayoutMediator(binding.tabs, viewPager!!, true, false) { tab, position ->
                tab.text =  sectionsPagerAdapter?.tabCaps[position]//"OBJECT ${(position + 1)}"
            }.attach()
            /*
            viewPager!!.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {var b = true
                    if(b){
                        if(position > 0 && position < 15){
                            //jetzt scrolled richtig mit "dumycode"
                           // viewPager!!.setCurrentItem(position,false)
                            b = false
                        }
                    }
                }
            })
            */

            //pagechange also in adapter getItemId(position: Int): Long {
        }


        //tabs.setupWithViewPager(viewPager)
        //supportActionBar?.hide() ne..
        gc.ttSgl()?.speak(" ")
        gc.mainActivity = this
        //gc.log("MainActivity onCreate")
        handleReceiveShare()
        gc.lernItem.Text = gc.csvList()!!.getRandomText()
        //gc.log("MainActivity onCreate  222")
    }
    fun doPageCount() {
        if (gc.appVals().valueReadBool("dolog", false))
            sectionsPagerAdapter!!.pagecount = 8 else sectionsPagerAdapter!!.pagecount = 7
    }
    override fun onResume() {
        gc.mainActivity = this
        super.onResume()
    }

    fun menuTitle(title: String) {
        binding.tvmenu.text = title
    }

    private var displayName: String? = null
    private var imageUri: Uri? = null

    private fun handleReceiveShare() {
        val intent = getIntent()
        val action =
            intent.action // https://developer.android.com/training/sharing/receive?hl=de
        /* if (Intent.ACTION_SEND_MULTIPLE.equals(action)
        for multiple files ..in Manifest add:
        <intent-filter>
                <action android:name="android.intent.action.SEND_MULTIPLE"/>
                <data android:mimeType="* __/ *"/>
                <category android:name="android.intent.category.DEFAULT"/>
            </intent-filter>
        single: <action android:name="android.intent.action.SEND" />

         */
        if (Intent.ACTION_SEND != action) return

        //  MIME-Typen:  text/*  alle wie text/plain .. rtf,html .. text/json
//        String type = intent.getType(); // image/*
//        if ("text/plain".equals(type)) {
        gc.sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (gc.sharedText != null) {
            gc.askDlg(
                getString(R.string.add_to_your_bibleverses), gc.sharedText
            ) { viewPager!!.setCurrentItem(6, false) }
        }

        val uri = intent.data
        if (uri != null) gc.askDlg("not supported", "uri found: $uri", null)

        val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_STREAM)
        }
        //imageUri = intent.getParcelableExtra<Uri?>(Intent.EXTRA_STREAM)
        if (imageUri != null) {
            displayName = gc.dateien().getDisplayName(imageUri)
            gc.askDlg(getString(R.string.copy_to_app_file_dir) + "\n" + displayName
            ) {
                if (gc.dateien().hasPrivateFile(displayName!!)) gc.askDlg(
                    getString(R.string.overwrite) + displayName
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
                ) gc.askDlg(getString(R.string.overwrite) + displayName) {
                    gc.dateien().copyFileToPrivate(uri!!, displayName)
                } else gc.dateien().copyFileToPrivate(uri!!, displayName)
            }

            23 -> {}
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    fun btnSpeekClick(view: View) {//muß view: View!!
        gc.ttSgl()?.speak(gc.lernItem.Text)
    }

    fun btnMainDlgClick(view: View) {//muß view: View!!
        gc.globDlg().showPopupWin( gc.lernItem.Text)
    }

    fun setBibleTheme(activity: BibleAy, setColors: Boolean = false) {
        setActivityTheme(activity)
        if (setColors) {
            val typedValue = TypedValue()
            activity.theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
            var color = ContextCompat.getColor(this, typedValue.resourceId)
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

    fun setActivityTheme(activity: AppCompatActivity) {
        val themeIdi = gc.appVals().valueReadInt("theme", ORANGE)//read only here
        when (themeIdi) {
            Default -> {
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

                theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
                color = ContextCompat.getColor(this, typedValue.resourceId)
                theme.resolveAttribute(R.attr.tabSelected_colour, typedValue, true)
                val selcolor = ContextCompat.getColor(this, typedValue.resourceId)
                binding.tabs.setTabTextColors(color, selcolor)

                binding.tabs.setSelectedTabIndicatorColor(selcolor)
                binding.tvmenu.setTextColor(color)
                binding.btnNavDlg.setTextColor(color)
                binding.btnSpeak.setTextColor(color)
                txtColor = color

            }
            //gc.Logl( "setAppTheme $theme", true)
            if (doRecreate) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        } catch (e: Exception) {
            gc.Logl(e.toString(), true)
        }
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

    */
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
    fun btnMainSheetClick(view: View) {//muß view: View!!
        sectionsPagerAdapter!!.doBottomSheet(viewPager!!.currentItem)

    }
}