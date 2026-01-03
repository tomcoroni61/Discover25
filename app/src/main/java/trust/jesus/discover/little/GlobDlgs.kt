package trust.jesus.discover.little

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.DialogInterface
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import org.apmem.tools.layouts.FlowLayout
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowMetrics
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import trust.jesus.discover.R
import trust.jesus.discover.actis.BibleAy
import trust.jesus.discover.little.Globus.Companion.getHeight

class GlobDlgs {

    val gc: Globus = Globus.getAppContext() as Globus

    fun showPopupWin( txt: String?) {
        //val wid = gc.popUpWidth
        var (wid, screenHeight) = getScreenDimensions()
        wid -= wid/15
        if (wid < 22) return
        val inflater = gc.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
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
// android.view.ViewGroup.LayoutParams.WRAP_CONTENT); tv_calc
        val textView = popupView.findViewById<TextView?>(R.id.tv_popup) ?: return
        textView.text = txt
        val tvcalc = popupView.findViewById<TextView?>(R.id.tv_calc)
        tvcalc?.text = txt
        val tvVersVersion = popupView.findViewById<TextView?>(R.id.tvVersVersion)
        val vtext = gc.lernItem.vers + " " + gc.lernItem.translation
        tvVersVersion?.text = vtext
        tvVersVersion?.setOnClickListener { view1: View? ->
            if (gc.lernItem.setCurHistory()) {
                val vtext = gc.lernItem.vers + " " + gc.lernItem.translation
                tvVersVersion.text = vtext
                textView.text = gc.lernItem.text
                if (pw.height<screenHeight-222) pw.height += 10
            }

        }
        var tvData = popupView.findViewById<TextView?>(R.id.tvprevData)
        tvData!!.setOnClickListener { view1: View? ->
            val vers = gc.versHistory.previousVers()
            if (vers != null) {
                val vtext = vers.vers + " " + vers.translation
                tvVersVersion?.text = vtext
                textView.text = vers.Text
                if (pw.height<screenHeight-222) pw.height += 10
            }
        }
        tvData = popupView.findViewById(R.id.tvnextData)
        tvData.setOnClickListener { view1: View? ->
            val vers = gc.versHistory.nextVers()
            if (vers != null) {
                val vtext = vers.vers + " " + vers.translation
                tvVersVersion?.text = vtext
                textView.text = vers.Text
                if (pw.height<screenHeight-222) pw.height += 10
            }
        }

        val flowLayout = popupView.findViewById<FlowLayout>(R.id.acflowLayout)
        var bt = popupView.findViewById<Button>(R.id.pwspeakOptclck)
        bt.setOnClickListener { view1: View? ->
            // Toast.makeText(this, " clicked", Toast.LENGTH_LONG).show();
            pw.dismiss()
            gc.ttSgl()?.andoSettings()
            //activityStart(mainActi, AyWords::class.java)
        }

        bt = popupView.findViewById(R.id.pwspeakclck)
        bt.setOnClickListener { view1: View? ->
            gc.ttSgl()?.cleanSpeak(textView.text.toString() )
        }

        val bti = popupView.findViewById<ImageButton>(R.id.pwCopy)
        bti.setOnClickListener { view1: View? ->
            gc.copyTextToClipboard(txt.toString())
            gc.toast("copy done")
        }

        val adapter = gc.mainActivity!!.sectionsPagerAdapter
        if (adapter==null) return
        bt = popupView.findViewById(R.id.pwBible)
        bt.setOnClickListener { view1: View? ->
            gc.lernItem.chapter.clear()
            gc.activityStart(null, BibleAy::class.java)
            pw.dismiss()
        }

        bt = popupView.findViewById(R.id.pwMain)
        if (gc.curFragment_idx == adapter.pIdxHome) flowLayout.removeView(bt) else
            bt.setOnClickListener { view1: View? ->
                pw.dismiss()
                gc.mainActivity!!.viewPager!!.setCurrentItem(adapter.pIdxHome, false)
            }

        bt = popupView.findViewById(R.id.pwwordclck)
        if (gc.curFragment_idx == adapter.pIdxClickW) flowLayout.removeView(bt) else
            bt.setOnClickListener { view1: View? ->
                pw.dismiss()
                gc.mainActivity!!.viewPager!!.setCurrentItem(adapter.pIdxClickW, false)
            }

        bt = popupView.findViewById(R.id.pwwordmix)
        if (gc.curFragment_idx == adapter.pIdxWords) flowLayout.removeView(bt) else
            bt.setOnClickListener { view1: View? ->
                pw.dismiss()
                gc.mainActivity!!.viewPager!!.setCurrentItem(adapter.pIdxWords, false)
            }

        bt = popupView.findViewById(R.id.pwletters)
        if (gc.curFragment_idx == adapter.pIdxLetters || adapter.pIdxLetters >= adapter.pageCount) flowLayout.removeView(bt) else
            bt.setOnClickListener { view1: View? ->
                pw.dismiss()
                gc.mainActivity!!.viewPager!!.setCurrentItem(adapter.pIdxLetters, false)
            }

        bt = popupView.findViewById(R.id.pwsaylck)
        if (gc.curFragment_idx == adapter.pIdxSpeech || adapter.pIdxSpeech >= adapter.pageCount) flowLayout.removeView(bt) else
            bt.setOnClickListener { view1: View? ->
                pw.dismiss()
                gc.mainActivity!!.viewPager!!.setCurrentItem(adapter.pIdxSpeech, false)
            }


        bt = popupView.findViewById(R.id.pwDiscover)
        if (gc.curFragment_idx == adapter.pIdxDiscover || adapter.pIdxDiscover >= adapter.pageCount) flowLayout.removeView(bt) else
            bt.setOnClickListener { view1: View? ->
                pw.dismiss()
                gc.mainActivity!!.viewPager!!.setCurrentItem(adapter.pIdxDiscover, false)
            }

        bt = popupView.findViewById(R.id.pwEditclk)
        if (gc.curFragment_idx == adapter.pIdxEntries || adapter.pIdxEntries >= adapter.pageCount) flowLayout.removeView(bt) else
            bt.setOnClickListener { view1: View? ->
                pw.dismiss()
                gc.mainActivity!!.viewPager!!.setCurrentItem(adapter.pIdxEntries, false)
            }

        bt = popupView.findViewById(R.id.pwretry) //
        if (gc.mainActivity?.sectionsPagerAdapter?.hasNochmal(gc.curFragment_idx) == false)
            bt.visibility = View.INVISIBLE

        bt.setOnClickListener { view1: View? ->
            pw.dismiss()
            gc.mainActivity?.sectionsPagerAdapter?.doNochmal(gc.curFragment_idx)

        }

        bt = popupView.findViewById(R.id.pwnewVers)
        if (gc.mainActivity?.sectionsPagerAdapter?.hasNewVers(gc.curFragment_idx) == false)
            bt.visibility = View.INVISIBLE
        bt.setOnClickListener { view1: View? ->
            gc.mainActivity?.sectionsPagerAdapter?.doNewVers(gc.curFragment_idx)
            pw.dismiss()
        }

        pw.animationStyle = android.R.anim.fade_in
        val relativeLayout = popupView.findViewById<RelativeLayout>(R.id.rlMain)
        val h1 = getHeight(tvcalc!!, wid-2)
        val h2 = getHeight(flowLayout!!, wid-5)//je row 150
        val spaces = 230
        var hei: Int = h1 + h2 + spaces
              //  getHeight(tvcalc!!, wid) + getHeight(flowLayout!!, wid)+50 getHeight(textView, wid) +
        //getHeight is okay but spaces was to little
        //tvVersVersion?.text = "TextHei "+h1+ " lc "+ tvcalc.lineCount +" FlowHei "+h2
        val sp = 120
        if (hei > screenHeight-sp) hei = screenHeight-sp

        pw.height = hei//+ (pw.getWidth()/4)
        tvcalc.visibility = View.INVISIBLE
        pw.showAtLocation(gc.mainActivity?.viewPager, Gravity.CENTER, 0, 0)
    }

    fun messageBox(txt: String, context: Context) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogCustom)
        builder.setTitle(gc.getString(R.string.app_name)) //AlertDialogTheme  R.style.AlertDialogCustom
        builder.setMessage(txt)

        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            dialog.dismiss()
        }
        /*builder.setNegativeButton("Cancel") { dialog, which ->
            // handle Cancel button click
            Builder(this.mSpeechFrag.requireContext())
                    .setTitle("Speech Recognizer unavailable")
                    .setMessage("Your device does not support Speech Recognition. Sorry!")
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
        }*/
        val dialog = builder.create()
        dialog.show()
    }

    fun interface AskDlgOkEve {
        fun onOkClick()
    }

    fun askDlg(ask: String?, context: Context, askDlgOkEve: AskDlgOkEve?) {
        askDlg(gc.getString(R.string.app_name), ask, context, askDlgOkEve)
    }

    fun askDlg(title: String?, ask: String?, context: Context, askDlgOkEve: AskDlgOkEve?) {
        var ask = ask
        val tv = TextView(context)
        ask = "\n" + ask
        tv.text = ask
        tv.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        tv.gravity = Gravity.CENTER
        AlertDialog.Builder(context, R.style.AlertDialogCustom)
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
            wid -= (wid / 15)
            return wid
        }
    fun getScreenDimensions(): Pair<Int, Int> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            // Get the window metrics
            val windowMetrics: WindowMetrics = gc.mainActivity!!.windowManager.currentWindowMetrics
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
            gc.mainActivity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
            // Return the width and height
            Pair(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
    }

}