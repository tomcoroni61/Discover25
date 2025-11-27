package trust.jesus.discover.little

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.Gravity
import android.view.LayoutInflater
import org.apmem.tools.layouts.FlowLayout
import android.view.View
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

    val gc: Globus = Globus.Companion.getAppContext() as Globus

    fun interface ResultListener {
        fun onChosenString(result: String?)
    }
    fun showPopupWin( txt: String?) {
        //val wid = gc.popUpWidth
        var (wid, screenHeight) = gc.getScreenDimensions()
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
        val textView = popupView.findViewById<TextView?>(R.id.tv_popup)
        if (textView == null) return
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
            gc.ttSgl()?.andoSetttings()
            //activityStart(mainActi, AyWords::class.java)
        }

        bt = popupView.findViewById(R.id.pwspeakclck)
        bt.setOnClickListener { view1: View? ->
            gc.ttSgl()?.speak(textView.text.toString() )
        }

        val bti = popupView.findViewById<ImageButton>(R.id.pwCopy)
        bti.setOnClickListener { view1: View? ->
            gc.copyTextToClipboard(txt.toString())
            gc.toast("copy done")
        }


        /*
                bt = popupView.findViewById(R.id.pwCopy)
                bt.setOnClickListener { view1: View? ->
                    val clipData = ClipData.newPlainText("text", txt.toString())
                    val clipboard: ClipboardManager? =
                        gc.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?

                    clipboard!!.setPrimaryClip(clipData)
                }

         */
        bt = popupView.findViewById(R.id.pwBible)
        bt.setOnClickListener { view1: View? ->
            gc.lernItem.chapter.clear()
            gc.activityStart(null, BibleAy::class.java)
            pw.dismiss()
        }

        bt = popupView.findViewById(R.id.pwMain)
        if (gc.curFragment_idx == 0) flowLayout.removeView(bt) else
            bt.setOnClickListener { view1: View? ->
                pw.dismiss()
                gc.mainActivity!!.viewPager!!.setCurrentItem(0, false)
            }

        bt = popupView.findViewById(R.id.pwwordclck)
        if (gc.curFragment_idx == 1) flowLayout.removeView(bt) else
            bt.setOnClickListener { view1: View? ->
                pw.dismiss()
                gc.mainActivity!!.viewPager!!.setCurrentItem(1, false)
            }

        bt = popupView.findViewById(R.id.pwwordmix)
        if (gc.curFragment_idx == 2) flowLayout.removeView(bt) else
            bt.setOnClickListener { view1: View? ->
                pw.dismiss()
                gc.mainActivity!!.viewPager!!.setCurrentItem(2, false)
            }

        bt = popupView.findViewById(R.id.pwletters)
        if (gc.curFragment_idx == 3) flowLayout.removeView(bt) else
            bt.setOnClickListener { view1: View? ->
                pw.dismiss()
                gc.mainActivity!!.viewPager!!.setCurrentItem(3, false)
            }

        bt = popupView.findViewById(R.id.pwsaylck)
        if (gc.curFragment_idx == 4) flowLayout.removeView(bt) else
            bt.setOnClickListener { view1: View? ->
                pw.dismiss()
                gc.mainActivity!!.viewPager!!.setCurrentItem(4, false)
            }


        bt = popupView.findViewById(R.id.pwDiscover)
        if (gc.curFragment_idx == 5) flowLayout.removeView(bt) else
            bt.setOnClickListener { view1: View? ->
                pw.dismiss()
                gc.mainActivity!!.viewPager!!.setCurrentItem(5, false)
            }

        bt = popupView.findViewById(R.id.pwEditclk)
        if (gc.curFragment_idx == 6) flowLayout.removeView(bt) else
            bt.setOnClickListener { view1: View? ->
                pw.dismiss()
                gc.mainActivity!!.viewPager!!.setCurrentItem(6, false)
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

    fun messageBox(txt: String) {
        val builder = AlertDialog.Builder(gc.applicationContext)
        //builder.setTitle("Title")
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
}