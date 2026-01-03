package trust.jesus.discover.fragis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import trust.jesus.discover.R
import trust.jesus.discover.databinding.FragLetterBinding
import trust.jesus.discover.dlg_data.ItemAr

class LettersFrag: BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragLetterBinding
    private var adapter: ItemAr? = null

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        binding = FragLetterBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        binding.tvbottomLeft.setOnClickListener(this) //alRetryClick
        binding.tvMoves.setOnClickListener(this) //letxtClick
        binding.tvbottomLefttw.setOnClickListener(this) //letxtClick

        checkVers()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        checkVers()
    }

    fun updateClickLabel() { //oki by online
        // csvList.RandomItem = csvList.dataList[csvList.RandomText_Idx]
        val ht =  "  ${adapter?.usermoves} | ${adapter?.movecnt} "
        //tvHeader.text = gc.LernItem.Vers gc.lernItem.vers +
        binding.tvMoves.text = ht
    }
    /*fun updateClickLabel() { wrong by Studio
        //csvList.RandomItem = csvList.dataList.get(csvList.RandomText_Idx);
        if (adapter == null) return
        var ht: String = adapter!!.usermoves + " | " + adapter!!.movecnt
        //binding.tvbottomLeft.setText(ht) tvHeader.setText(gc.LernItem.Vers)
        binding.tvMoves.setText(ht)
    } */

    fun lettersDone() {
        val ht = " Done " + adapter?.usermoves + " <> " + adapter?.movecnt
        //tvHeader.setText(csvData.Vers)
        binding.tvMoves.text = ht
        // gc.globDlg().showPopupWin(gc.lernItem.text)
        //gc.showPopupWin(binding.basiGridView, csvData.Text, { newText(true) })
    }

    private var curVers = ""
    fun checkVers() {
        gc.lernItem.setVersTitel() //gc.setVersTitel(curVers)
        if (curVers == gc.lernItem.vers) return
        curVers = gc.lernItem.vers
        newText(true)
    }
    fun newText(nurMisch: Boolean) {
        var nurMisch = nurMisch
        var crashcnt = 0
        try {
            if (nurMisch && gc.lernItem.text.length < 22) nurMisch = false
            var str: String
            crashcnt = 1
            if (nurMisch)
                str = gc.lernItem.text
                    else str = gc.csvList()!!.getRandomText() // "Es ist aber der Glaube eine feste Zuversicht dessen, was man hofft, und ein Nichtzweifeln an dem, was man nicht sieht.";

            if (gc.lernItem.partText.length>9)
                str = gc.lernItem.partText
            str = gc.formatTextUpper(str)
            gc.lernItem.setVersTitel()
            binding.basiGridView.adapter = null
            if (adapter != null) {
                adapter!!.clearme()
                adapter!!.clear()
                adapter = null
            }
            //gridView.get
            adapter = ItemAr(this.requireContext(), binding.basiGridView, this)
            binding.basiGridView.adapter = adapter
            crashcnt = 2
            val letters = str.toCharArray()
            adapter?.loadLetters(letters)
            adapter?.mischen()
            crashcnt = 9
            if (adapter?.count != letters.size) gc.logl(
                "Error load TextLen: " + letters.size + " ListLen: " + adapter?.count,
                true
            )
            updateClickLabel()
        } catch (e: Exception) {
            gc.logl("MA_Crash Nr: " + crashcnt + " Msg: " + e.message, true)
        }
    }


    fun letxtClick() {
        gc.globDlg().showPopupWin(gc.lernItem.text)
        //gc.showPopupWin(binding.basiGridView, gc.LernItem.Text, { newText(true) })
    }


    fun alRetryClick() {
        newText(true)
    }

    fun speackClick() {
        gc.ttSgl()!!.cleanSpeak(gc.lernItem.text)
    }
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvbottomLefttw ->  speackClick()
            R.id.tvbottomLeft -> alRetryClick()

            R.id.tvMoves -> newText(false)

        }

    }
    /*
        binding.tvbottomLeft.setOnClickListener(this) //alRetryClick
        binding.tvFragezeichen.setOnClickListener(this) //letxtClick
        binding.tvMoves.setOnClickListener(this) //letxtClick

     */

}