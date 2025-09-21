package trust.jesus.discover.little

import android.view.View
import android.widget.TextView

class Celli {
    var letterView: TextView? = null
    var cellView: View? = null
    var CharOk: Char = 0.toChar()
    var CharVisi: Char = 0.toChar()
    var selected: Boolean = false
    var mischMoved: Boolean = false
    var wordID: Int = 0
    var idxOk: Int = 0
    var idxVisi: Int = -1
    var position: Int = 0
}
