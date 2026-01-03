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
    //var viewPager: ViewPager2? = null

    private val tfnList: MutableList<FragiCreateHolder> = ArrayList<FragiCreateHolder>()
    private val Timhandl = Handler(Looper.getMainLooper())
    var tabPosition = 0

    var pageCount = 8 //change in MainActivity.kt doPageCount()
    val pIdxHome = 0;    val pIdxClickW = 1;    val pIdxWords = 2
    val pIdxDiscover = 3;   val pIdxLetters=4;  val pIdxSpeech = 5;
    val pIdxSpeech2 = 6;    val pIdxEntries=7;  val pIdxLog = 8

    val gc: Globus = Globus.getAppContext() as Globus
    val tabCaps = arrayOf(
        gc.getString(R.string.Title_home),
        gc.getString(R.string.Title_clickw),
        gc.getString(R.string.title_words),
        gc.getString(R.string.Title_entdecke),
        gc.getString(R.string.Title_letters),
        gc.getString(R.string.Title_speech),
        gc.getString(R.string.Title_speech2),
        gc.getString(R.string.title_edit),
        "Log"
    )
    override fun createFragment(position: Int): Fragment {
        //gc.curFragment = position
        val ret: Fragment = when (position) {
            pIdxHome -> HomeFrag()
            pIdxClickW -> ClickWfrag()
            pIdxWords -> WordFrag()
            pIdxDiscover -> EntdeckeFrag()
            pIdxLetters -> LettersFrag()
            pIdxSpeech -> SpeechFrag()
            pIdxSpeech2 -> Speech2()
            pIdxEntries -> EntriesFrag()
            pIdxLog -> LogFrag() //LogFrag()
            else -> EntdeckeFrag()
        }// EntriesFrag
        gc.curFragment = ret.javaClass.name//tabCaps[position] LogFrag()
        doFragList(position, ret)
        return ret

    }
    override fun getItemCount(): Int = pageCount ////change in MainActivity.kt doPageCount()

    fun getTitle(position: Int): String {//shure right position
        tabPosition = position
        return tabCaps[position]
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

    fun doMerkVers(position: Int, idx: Int) {
        for (h in tfnList) { //todo?
            if (h.viewPagerPos == position) {
                when (position) {
                    pIdxHome ->  (h.fragment as HomeFrag).merkToLearnVersClick(idx)
                    pIdxClickW ->   (h.fragment as ClickWfrag).doAgainClick()
                    pIdxWords ->   (h.fragment as WordFrag).mischViews()
                    3 ->   (h.fragment as LettersFrag).newText(true)
                    pIdxDiscover ->   (h.fragment as EntdeckeFrag).mischViews()
                }
                //h.fragment!!.onResume()
                return
            }
        }
    }

    fun doNochmal(position: Int) {
        for (h in tfnList) {
            if (h.viewPagerPos == position) {
                when (position) {
                    pIdxClickW ->   (h.fragment as ClickWfrag).doAgainClick()
                    pIdxWords ->   (h.fragment as WordFrag).mischViews()
                    3 ->   (h.fragment as LettersFrag).newText(true)
                    pIdxDiscover ->   (h.fragment as EntdeckeFrag).mischViews()
                }
                //h.fragment!!.onResume()
                return
            }
        }
    }

    fun showBollsPrevActi() {
        for (h in tfnList) {
            if (h.fragment is EntdeckeFrag) {
                (h.fragment as EntdeckeFrag).btnRandomObstClick()
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
    //von Mainactivity btnMainSheetClick

    private val rundoMenuImg: Runnable = object : Runnable {
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

    /*
    fun menuTitle(title: String) {
        binding.tvmenu.text = title
    }


     */
    private var timeTextChanged = 0L
    // var pagePos = 0  much to delete here....
    fun doMenuImg() {
        var kein = true
        // pagePos = viewPager!!.currentItem //try 11.25
        //gc.log( "doMenuImg $tabPosition")
        when (tabPosition) {
            pIdxHome, pIdxClickW, pIdxDiscover ->     kein = false
        }
        // gc.mainActivity!!.setMenuImg(kein)
    }
    override fun getItemId(position: Int): Long {
        if (timeTextChanged==0L) Timhandl.postDelayed(rundoMenuImg, 30L)
        timeTextChanged = System.currentTimeMillis()
        // gc.mainActivity?.menuTitle(tabCaps[position])
        gc.curFragment_idx = position
        tabPosition = position  //viewPager!!.currentItem

        return super.getItemId(position)
    }
}