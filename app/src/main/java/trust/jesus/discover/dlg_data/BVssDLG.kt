package trust.jesus.discover.dlg_data

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.ListView
import trust.jesus.discover.dlg_data.BibleVersionsDLG.SimpleListener

class BVssDLG (a: Context, simpleListener: SimpleListener?) : Dialog(a), View.OnClickListener{
    //private lateinit var binding: BibleVersionsDLG
    var msimpleListener: SimpleListener? = simpleListener
    var listView: ListView? = null




    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }

}