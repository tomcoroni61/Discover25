package trust.jesus.discover.little.recognio

import android.widget.TextView

open class VallsBase {

    var usePartWord = false;    var withEndCheck = false
    var partWordProzent = 0;    var partWordFoundProzent=0
    var logTv: TextView? = null
    enum class WordArt {RightSpoken, WrongSpoken, PartSpoken, AutoAdd, NextFound, InMissedList}
    var wa: WordArt = WordArt.RightSpoken

}