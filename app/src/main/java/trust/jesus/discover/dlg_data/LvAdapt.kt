package trust.jesus.discover.dlg_data

import android.content.Context
import android.widget.ArrayAdapter

open class LvAdapt<T>(context: Context, resourceId: Int) :
    ArrayAdapter<T?>(context, resourceId) {
        val gc = trust.jesus.discover.little.Globus.getAppContext() as trust.jesus.discover.little.Globus
    fun set(index: Int, item: T?) {
        remove(item)
        insert(item, index)
    }

    override fun getItem(position: Int): T? {
        if (count <= position) return null
        return super.getItem(position)
    }
}

