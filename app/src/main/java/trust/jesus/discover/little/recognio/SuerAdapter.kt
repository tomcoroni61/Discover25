package trust.jesus.discover.little.recognio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import trust.jesus.discover.R
import trust.jesus.discover.dlg_data.LvAdapt
import trust.jesus.discover.dlg_data.SuErItem
import trust.jesus.discover.little.Globus

class SuerAdapter(context: Context) : LvAdapt<SuErItem?>(context, android.R.layout.simple_list_item_2) {
    private val mInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val gc: Globus = Globus.getAppContext() as Globus
    private class ViewHolder {
        var tvSuch: TextView? = null
        var tvErsetz: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val holder: ViewHolder
        val item: SuErItem? = getItem(position)


        if (convertView == null) {
            view = mInflater.inflate(R.layout.suerrow, parent, false)
            holder = ViewHolder()

            holder.tvSuch = view!!.findViewById<TextView?>(R.id.tvSuch)
            holder.tvErsetz = view.findViewById<TextView?>(R.id.tvErsetze)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }
        if (item != null) {
            holder.tvSuch?.text = item.suche
            holder.tvSuch?.setBackgroundColor(item.ItemBakColor)
            holder.tvErsetz?.text = item.ersetze
            holder.tvErsetz?.setBackgroundColor(item.ItemBakColor)
        }
        return view
    }

    fun loadFromFile() {
        if (!gc.dateien().openInputStream("SeekReplace.txt")) return
        this.clear()
        while (gc.dateien().readLine()) {
            val item = SuErItem()
            item.ItemBakColor
            item.suche = gc.dateien().rLine
            if (!gc.dateien().readLine()) break
            item.ersetze = gc.dateien().rLine
            this.add(item)
        }
        gc.dateien().closeInputStream()
        this.notifyDataSetChanged()
    }

    fun savetofile() {
        if (!gc.dateien().openOutputStream("SeekReplace.txt", Context.MODE_PRIVATE)) return
        for (i in 0..<this.count) {
            val item: SuErItem? = getItem(i)
            if (item == null) continue
            gc.dateien().writeLine(item.suche)
            gc.dateien().writeLine(item.ersetze)
        }
        gc.dateien().closeOutputStream()
    }
}
