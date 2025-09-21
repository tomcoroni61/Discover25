package trust.jesus.discover.fragis

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import trust.jesus.discover.R
import trust.jesus.discover.little.Globus

class FragAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {


    class FragiCreateHolder {
        var viewPagerPos: Int = 0
        var caption: String? = null
        var fragment: Fragment? = null

    }

    private val tfnList: MutableList<FragiCreateHolder> = ArrayList<FragiCreateHolder>()
    private val Timhandl = Handler(Looper.getMainLooper())

    var pagecount = 8
    private val home = 0
    private val clickw = 1
    private val words = 2
    private val letters = 3
    private val speech = 4
    private val discover = 5
    override fun getItemCount(): Int = pagecount //ohne LogFrag()
    val gc: Globus = Globus.getAppContext() as Globus
    val tabCaps = arrayOf(
        gc.getString(R.string.Title_home),
        gc.getString(R.string.Title_clickw),
        gc.getString(R.string.title_words),
        gc.getString(R.string.Title_letters),
        gc.getString(R.string.Title_speech),
        gc.getString(R.string.Title_entdecke),
        gc.getString(R.string.title_edit),
        "Log"
    )
    override fun createFragment(position: Int): Fragment {
        //gc.curFragment = position
        val ret: Fragment = when (position) {
            home -> HomeFrag()
            1 -> ClickWfrag()
            2 -> WordFrag()
            3 -> LettersFrag()
            speech -> SpeechFrag()
            discover -> EntdeckeFrag()
            6 -> EntriesFrag()
            7 -> LogFrag()
            else -> EntdeckeFrag()
        }// EntriesFrag
        gc.curFragment = ret.javaClass.name//tabCaps[position] LogFrag()
        doFragList(position, ret)
        return ret

    }
    fun hasNochmal(position: Int): Boolean {
        for (h in tfnList) {
            if (h.viewPagerPos == position) {
                when (position) {
                    1, 2, 3, 5 ->   return true
                }
            }
        }
        return false
    }


    fun doNochmal(position: Int) {
        for (h in tfnList) {
            if (h.viewPagerPos == position) {
                when (position) {
                    clickw ->   (h.fragment as ClickWfrag).doAgainClick()
                    words ->   (h.fragment as WordFrag).mischViews()
                    3 ->   (h.fragment as LettersFrag).newText(true)
                    discover ->   (h.fragment as EntdeckeFrag).mischViews()
                }
                //h.fragment!!.onResume()
                return
            }
        }
    }
    fun hasNewVers(position: Int): Boolean {
        for (h in tfnList) {
            if (h.viewPagerPos == position) {
                when (position) {
                    1, 2, 3, 5 ->   return true
                }
            }
        }
        return false
    }
    fun doNewVers(position: Int) {
        for (h in tfnList) {
            if (h.viewPagerPos == position) {
                when (position) {
                    1 ->   (h.fragment as ClickWfrag).doNewVersClick()
                    2 ->   (h.fragment as WordFrag).newVers()
                    3 ->   (h.fragment as LettersFrag).newText(false)
                    5 ->   (h.fragment as EntdeckeFrag).btnRandomversClick()
                }
                return
            }
        }
    }
    private fun doFragList(position: Int, fragment: Fragment) {
        for (h in tfnList) {
            if (h.viewPagerPos == position) {
                h.fragment = fragment
                return
            }
        }
        val h = FragiCreateHolder()
        h.viewPagerPos = position
        h.caption = tabCaps[position]
        h.fragment = fragment
        tfnList.add(h)

    }
    fun doBottomSheet(position: Int) {
        for (h in tfnList) {
            if (h.viewPagerPos == position) {
                when (position) {
                    home ->         (h.fragment as HomeFrag).doThemeSheet()
                    //speech ->      (h.fragment as SpeechFrag).doButtonSheet()
                    discover ->     (h.fragment as EntdeckeFrag).doButtonSheet()
                }
                //h.fragment!!.onResume()
                return
            }
        }
    }

    private val rundoMenuImg: Runnable = object : Runnable {
        //geht nur wenn handy eingeschalten ist.ttsvals.waitTime.toLong()*
        override fun run() {
            val timi = System.currentTimeMillis() - timeTextChanged
            if (timi < 200L) {
                Timhandl.postDelayed(rundoMenuImg, 100)
                return
            }
            timeTextChanged = 0L
            doMenuImg()
        }
    }

    private var timeTextChanged = 0L
    var pagePos = 0
    fun doMenuImg() {
        var kein = true
        for (h in tfnList) {
            if (h.viewPagerPos == pagePos) {
                when (pagePos) {
                    home , discover ->     kein = false
                }
            }
        }
        gc.mainActivity!!.setMenuImg(kein)
    }
    override fun getItemId(position: Int): Long {
        if (timeTextChanged==0L) Timhandl.postDelayed(rundoMenuImg, 100L)
        timeTextChanged = System.currentTimeMillis()
        gc.mainActivity?.menuTitle(tabCaps[position])
        gc.curFragment_idx = position
        pagePos = position

        return super.getItemId(position)
    }
}