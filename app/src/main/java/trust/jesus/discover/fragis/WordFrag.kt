package trust.jesus.discover.fragis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.iterator
import trust.jesus.discover.R
import trust.jesus.discover.databinding.FragmentWordBinding
import androidx.core.view.size

class WordFrag : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentWordBinding
    private val okList: MutableList<String?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the binding.flowLayout for this fragment
        //return inflater.inflate(R.binding.flowLayout.fragment_word, container, false)
        binding = FragmentWordBinding.inflate(layoutInflater)
        val rootView: View = binding.getRoot()

        binding.wtvSpeak.setOnClickListener(this)
        binding.ayheaderTextView.setOnClickListener(this)
        binding.etvTxtShow.setOnClickListener(this)
        binding.tvagain.setOnClickListener(this)

        loadText()
        return rootView
    }

    override fun onResume() {
        super.onResume()
        loadText()
    }
    private var curVers = ""

    fun loadText() {
        gc.setVersTitel(curVers)
        if (curVers == gc.lernItem.vers) return
        curVers = gc.lernItem.vers
        gc.setVersTitel(curVers)
        var text: String = gc.lernItem.Text
        if (gc.lernItem.partText.length>9)
            text = gc.lernItem.partText

        if (text.length < 5) return
        binding.flowLayout.removeAllViews()
        text = gc.formatTextUpper(text)
        gc.Logl(text, false)
        var wort = StringBuilder()
        var cnt = 0
        okList.clear()
        for (i in 0..<text.length) {
            val c = text[i]
            if (c == ' ') {
                addWort(wort.toString(), cnt)
                //addWort(" ");
                wort = StringBuilder()
                cnt++
            } else wort.append(c)
        }
        if (wort.isNotEmpty()) {
            addWort(wort.toString(), cnt) //addWort(" ");
        }
        mischViews()
        binding.ayheaderTextView.text = gc.lernItem.vers
    }

    fun txtClick() {
        gc.globDlg().showPopupWin(gc.lernItem.Text)

    }

    fun tvSpeackClick() {
        gc.ttSgl()!!.speak(gc.lernItem.Text)
    }

    fun mischViews() {
        val cnt: Int = binding.flowLayout.size
        if (cnt < 5) return
        var mc = 5 + random.nextInt(cnt)
        var idx: Int
        var idx2: Int //textView.setBackgroundResource(R.drawable.rounded_corner)
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
                    break
                }
            }
            mc--
        }
        removeView = null
    }

    private var removeView: TextView? = null

    private fun checkNewPlatz(idx: Int, view: View) {
        val vText = (view as TextView).text.toString()
        val riPl = vText == okList[idx]

        if (riPl) view.setBackgroundResource(R.drawable.richtigplaz) else view.setBackgroundResource(
            R.drawable.rounded_corner
        )
    }

    private fun wordClick(view1: View?) {
        val tv = view1 as TextView
        val txt = tv.text.toString()
        val viewIdx: Int = getViewIdx(tv)
        if (removeView != null) {
            if (removeView === tv) {
                removeView!!.setBackgroundResource(R.drawable.rounded_corner)
                removeView = null
            } else {
                if (viewIdx > -1) {
                    binding.flowLayout.removeView(removeView)
                    binding.flowLayout.addView(removeView, viewIdx)
                    checkNewPlatz(viewIdx, removeView!!)
                    OkCheck()
                }

                removeView = null
            }
        } else {
            if (txt == okList.get(viewIdx)) tv.setBackgroundResource(R.drawable.richtigplaz)
            else {
                val rid = isNext(view1)
                if (rid > -1) {
                    binding.flowLayout.removeView(view1)
                    binding.flowLayout.addView(view1, rid)
                    tv.setBackgroundResource(R.drawable.richtigplaz)
                    OkCheck()
                } else {
                    removeView = tv
                    removeView!!.setBackgroundResource(R.drawable.selected)
                }
            }
        }
    }

    private fun isNext(view: View): Int {
        val Cnt: Int = binding.flowLayout.childCount
        var okcnt = -1
        val viewtxt = (view as TextView).text.toString()

        for (i in 0..<Cnt) {
            val v = binding.flowLayout.getChildAt(i) as TextView
            val txt = v.text.toString()
            if (txt == okList.get(i)) okcnt++ else {
                if (viewtxt == okList.get(i)) return i
                else return -1
            }
        }
        return -1
    }

    private fun OkCheck() {
        var okcnt = 0
        val Cnt: Int = binding.flowLayout.size
        for (i in 0..<Cnt) {
            val v = binding.flowLayout.getChildAt(i) as TextView
            val txt = v.text.toString()
            if (txt == okList[i]) okcnt++ else v.setBackgroundResource(R.drawable.rounded_corner)
        }
        if (okcnt != Cnt) {
            return
        }
        //gc.Logl("Oki: "+okcnt+ " / "+Cnt, true);
        for (i in 0..<Cnt) {
            val v: View = binding.flowLayout.getChildAt(i)
            v.setBackgroundResource(R.drawable.richtigplaz)
        }
        gc.globDlg().showPopupWin(gc.lernItem.Text)

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
            R.id.etvTxtShow -> txtClick()
            R.id.wtvSpeak -> tvSpeackClick()
            R.id.tvagain  -> mischViews()
        }

    }


}