package trust.jesus.discover.little.recognio

import trust.jesus.discover.R
import trust.jesus.discover.dlg_data.SuErItem
import trust.jesus.discover.fragis.BaseFragment

class SpeechEx(speechFrag: BaseFragment) {
    //common things for speechrecognitian
    private val mSpeechFrag=speechFrag
    val suerAdapter = SuerAdapter(mSpeechFrag.requireContext())
    var valBase = VallsBase()

    init {
        suerAdapter.loadFromFile()
        var i = 0
        val suer = arrayOf<String?>("dass", "das", "daß", "das", " {2}", " ")
        if (suerAdapter.isEmpty) {
            while (i < 6) {
                val item = SuErItem()
                item.suche = suer[i]
                i++
                item.ersetze = suer[i]
                i++
                suerAdapter.add(item)
            }
            suerAdapter.savetofile()
            //suerAdapter!!.notifyDataSetChanged()
        }

    }
    fun suerByList(text: String): String {
        var text = text
        for (i in 0..< suerAdapter.count) {
            val item: SuErItem = suerAdapter.getItem(i) ?: continue
            text = text.replace(item.suche!!.toRegex(), item.ersetze!!)
        }
        return text
    }
    fun getEndDoneText(level: Int): String {
        var txt = mSpeechFrag.getString(R.string.bad)
        if (level==100) txt = mSpeechFrag.getString(R.string.okay_verry_nice) else
            if (level >= 90) txt = mSpeechFrag.getString(R.string.okay_nice) else
                if (level >= 60) txt = mSpeechFrag.getString(R.string.nice)
        return txt
    }

    fun sameWord(word1: String, word2: String): Boolean {
        if (word1.isEmpty() || word2.isEmpty()) return false
        if (word1 == word2) {
            valBase.wa = VallsBase.WordArt.RightSpoken
            return true
        }//math.abs
        //mSpeechFrag.gc.log( "sameWord: $word1  $word2  ${vallsTts.usePartWord}\n\n" )
        if ( !valBase.usePartWord ) return false

        var sword = ""
        var lword = ""
        if (word1.length < word2.length) {
            sword = word1//dl = word2.length-word1.length
            lword = word2
        }
        else {
            lword = word1
            sword = word2
        }
        val wpa =  (sword.length * 100 / lword.length)
        if (wpa > valBase.partWordProzent) {
            var okicnt = 0
            for (char in sword) {
                val idx = lword.indexOf(char)
                if (idx > -1) {
                    lword = lword.removeRange(idx, idx + 1)
                    okicnt++
                    //binding.tvAllPartText.append("\npartWord: $sword  in $lword  oki: $okicnt\n\n")
                }
            }
            val level = (okicnt * 100 / sword.length)

            if (level > valBase.partWordFoundProzent) {
                valBase.wa = VallsBase.WordArt.PartSpoken
                valBase.logTv?.append("\npartWord: $sword  in $word2 ($word1)  level: $level\n\n")
                //binding.tvAllPartText.append("\npartWord: $sword  in $word2 ($word1)  level: $level\n\n")
                return true
            }
        }
        return false
    }

}