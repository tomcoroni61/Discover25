package trust.jesus.discover.dlg_data

import android.content.Context
import android.widget.ArrayAdapter

open class LvAdapt<T>(context: Context, textViewResourceId: Int) :
    ArrayAdapter<T?>(context, textViewResourceId) {
    fun set(index: Int, item: T?) {
        remove(item)
        insert(item, index)
    }
}

