package trust.jesus.discover.fragis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.iterator
import trust.jesus.discover.R
import trust.jesus.discover.databinding.FragWordBinding
import androidx.core.view.size

class WordFrag : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragWordBinding
    private val okList: MutableList<String?> = ArrayList()
    private val okListBak: MutableList<String?> = ArrayList()
    private val viewStartList: MutableList<View?> = ArrayList()
    private val viewMixList: MutableList<View?> = ArrayList()
    private var mischCount: Int = 0;    private var userMoves = 0;   private var okiCount = 0

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?  ): View {
        // Inflate the binding.flowLayout for this fragment
        //return inflater.inflate(R.binding.flowLayout.fragment_word, container, false)
        binding = FragWordBinding.inflate(layoutInflater)
        val rootView: View = binding.getRoot()

        binding.wtvSpeak.setOnClickListener(this)
        binding.ayheaderTextView.setOnClickListener(this)
        binding.tvMischen.setOnClickListener(this)
        binding.tvrepeat.setOnClickListener(this)
        loadText()
        return rootView
    }

    override fun onResume() {
        super.onResume()
        loadText()
    }
    private var curVers = ""//if screen is rotated, fragment will recreate

    fun loadText() {
        //gc.Logl("STARTText: $curVers", true)
        gc.lernItem.setVersTitel() //gc.setVersTitel(curVers)
        if (curVers == gc.lernItem.vers) return
        curVers = gc.lernItem.vers
        gc.lernItem.setVersTitel() //gc.setVersTitel(curVers)
        //gc.Logl("loadText: $curVers", true)
        var text: String = gc.lernItem.text
        if (gc.lernItem.partText.length>9)
            text = gc.lernItem.partText

        if (text.length < 5) return
        viewStartList.clear();      viewMixList.clear()
        binding.flowLayout.removeAllViews()
        okList.clear();     okListBak.clear()
        text = gc.formatTextUpper(text)
        gc.logl(text, false)
        var wort = StringBuilder()
        var cnt = 0
        for (i in 0..<text.length) {
            val c = text[i]
            if (c == ' ') {
                addWort(wort.toString(), cnt)//with okList.add(wd)
                //addWort(" ");
                wort = StringBuilder()
                cnt++
            } else wort.append(c)
        }
        if (wort.isNotEmpty()) {
            addWort(wort.toString(), cnt) //addWort(" ");
        }
        //doBackList()
        okListBak.addAll(okList)
        viewStartList.addAll(binding.flowLayout.children)
        mischViews()
        //binding.ayheaderTextView.text = gc.lernItem.vers
    }

    fun doCountThing() {
        val txt = "moves: $userMoves || $mischCount"
        binding.ayheaderTextView.text = txt
    }

    fun tvSpeackClick() {
        if (okList.size > 11) {
            var startidx = okiCount - 4
            if (startidx < 0) startidx = 0
            var cnt = startidx + 8
            if (cnt > okList.size) cnt = okList.size
            val txt = okList.subList(startidx, cnt).joinToString(separator = " ")
            if (txt.length < 5) gc.ttSgl()?.speak(gc.lernItem.text) else
                gc.ttSgl()?.speak(txt.lowercase())

        } else
            gc.ttSgl()!!.cleanSpeak(gc.lernItem.text)
    }

    private var mcLast = 0
    fun mischViews() {

        //okList.clear();     okList.addAll(okListBak)
        val cnt: Int = binding.flowLayout.size
        if (cnt < 5) return
        var mc = 5 + random.nextInt(cnt/5)
        for (i in 0..21) {
            if (mcLast>0 && mc==mcLast)
                mc = 5 + random.nextInt(cnt/5)
        }
        mcLast = mc
        var idx: Int
        var idx2: Int //textView.setBackgroundResource(R.drawable.rounded_corner)
        mischCount = 0
        for (item in binding.flowLayout)
            item.setBackgroundResource(R.drawable.rounded_corner)
        while (mc > 0) {
            idx = random.nextInt(cnt - 1)
            for (i in 0..21) {
                idx2 = random.nextInt(cnt - 1)
                if (idx != idx2 && idx2 < cnt - 2) {
                    val view: View = binding.flowLayout.getChildAt(idx)
                    binding.flowLayout.removeView(view)
                    binding.flowLayout.addView(view, idx2)
                    mischCount ++
                    //doCountThing()
                    break
                }
            }
            mc--
        }
        //for (item in binding.flowLayout)
          //  (item as TextView).setOnClickListener { view1: View? -> this.wordClick(view1)  }
        removeView = null
        userMoves = 0
        //doCountThing()
        doBackList(viewMixList)
        doCountThing()
    }

    private var removeView: TextView? = null

    private fun checkNewPlatz(idx: Int, view: View) {
        val vText = (view as TextView).text.toString()
        val riPl = vText == okList[idx]

        if (riPl) view.setBackgroundResource(R.drawable.richtigplaz) else
            view.setBackgroundResource( R.drawable.rounded_corner  )
    }

    private fun wordClick(view1: View?) {
        val tv = view1 as TextView
        val txt = tv.text.toString()
        val viewIdx: Int = getViewIdx(tv)
        if (viewIdx < 0 || viewIdx >=okList.size)
            return
        if (removeView != null) {
            if (removeView === tv) {
                removeView!!.setBackgroundResource(R.drawable.rounded_corner)
                removeView = null
            } else {
                binding.flowLayout.removeView(removeView)
                binding.flowLayout.addView(removeView, viewIdx)
                checkNewPlatz(viewIdx, removeView!!)
                userMoves++
                doCountThing()
                okCheck()

                removeView = null
            }
        } else {
            if (txt == okList[viewIdx])
                tv.setBackgroundResource(R.drawable.richtigplaz)
            else {
                val rid = isNext(view1)
                if (rid > -1) {
                    binding.flowLayout.removeView(view1)
                    binding.flowLayout.addView(view1, rid)
                    tv.setBackgroundResource(R.drawable.richtigplaz)
                    userMoves++
                    doCountThing()
                    okCheck()
                } else {
                    removeView = tv
                    removeView!!.setBackgroundResource(R.drawable.selected)
                }
            }
        }
    }

    private fun isNext(view: View): Int {
        val cnt: Int = binding.flowLayout.childCount
        var okcnt = -1
        val viewtxt = (view as TextView).text.toString()

        for (i in 0..<cnt) {
            val v = binding.flowLayout.getChildAt(i) as TextView
            val txt = v.text.toString()
            if (txt == okList[i]) okcnt++ else {
                return if (viewtxt == okList[i]) i
                else -1
            }
        }
        return -1
    }

    private fun doBackList(aList: MutableList<View?>) {
        aList.clear();   //okListBak.clear()
        val cnt: Int = binding.flowLayout.size
        for (i in 0..< cnt) {
            val v = binding.flowLayout.getChildAt(i)
            aList.add(v)
        }
        //okListBak.addAll(okList)
    }
    private fun loadBackList(aList: MutableList<View?>) {
        if (aList.isEmpty()) return
        okList.clear();     okList.addAll(okListBak)
        binding.flowLayout.removeAllViews()
        for (view in aList)
            binding.flowLayout.addView(view)
        userMoves = 0;
        for (item in binding.flowLayout) {
            item.setBackgroundResource(R.drawable.rounded_corner)
            item.setOnClickListener { view1: View? -> this.wordClick(view1) }
        }
         doCountThing()
    }

    private fun okCheck() {
        if (okList.isEmpty()) return
        var okcnt = 0;  okiCount=0
        val allOkayCnt: Int = binding.flowLayout.size
        for (i in 0..<allOkayCnt) {
            val v = binding.flowLayout.getChildAt(i) as TextView
            val txt = v.text.toString()
            if (txt == okList[i]) {

                okcnt++
            } else {
                if (okcnt > 0 && okiCount==0) okiCount=okcnt
                v.setBackgroundResource(R.drawable.rounded_corner)
            }
        }
        if (okcnt != allOkayCnt) {
            return
        }
        //gc.Logl("Oki: "+okcnt+ " / "+Cnt, true);
        // ..alles richtig ..
        for (i in 0..<allOkayCnt) {
            val v: View = binding.flowLayout.getChildAt(i)
            v.setBackgroundResource(R.drawable.richtigplaz)
            v.setOnClickListener(null)
        }
        okList.clear()
        //gc.globDlg().showPopupWin(gc.lernItem.Text)

    }

    private fun getViewIdx(view: View?): Int {
        for (i in 0..<binding.flowLayout.size) {
            val v: View = binding.flowLayout.getChildAt(i)
            if (v === view) return i
        }
        return -1
    }

    private fun addWort(wd: String?, idx: Int) {
        val textView = LayoutInflater.from(binding.flowLayout.context)
            .inflate(R.layout.tv_word, binding.flowLayout, false) as TextView
        textView.text = wd
        textView.tag = idx
        textView.setBackgroundResource(R.drawable.rounded_corner)
        textView.setOnClickListener { view1: View? -> this.wordClick(view1) }
        binding.flowLayout.addView(textView)
        okList.add(wd)
    }

    fun newVers() {
        gc.csvList()!!.getRandomText()
        loadText()
    }

    override fun onClick(p0: View?) {

        when (p0?.id) { //mischViews()
            R.id.ayheaderTextView -> newVers()
            R.id.wtvSpeak -> tvSpeackClick()
            R.id.tvMischen  -> {
                loadBackList(viewStartList)
                mischViews()
            }
            R.id.tvrepeat -> loadBackList(viewMixList)
        }

    }


}