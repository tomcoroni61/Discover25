package trust.jesus.discover.dlg_data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import trust.jesus.discover.R

class TtsAdapter (context: Context) : LvAdapt<TtsItem?>(context, android.R.layout.simple_list_item_2) {

    private val mInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private class ViewHolder {
        var tvName: TextView? = null
        var tvLabel: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val holder: ViewHolder
        val item: TtsItem? = getItem(position)


        if (convertView == null) {
            view = mInflater.inflate(R.layout.tts_row, parent, false)
            holder = ViewHolder()

            holder.tvName = view!!.findViewById(R.id.tvRName)
            holder.tvLabel = view.findViewById(R.id.tvRLabel)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }
        if (item != null) {
            holder.tvName?.text = item.name
            holder.tvName?.setBackgroundColor(item.itemBakColor)
            holder.tvLabel?.text = item.label
            holder.tvLabel?.setBackgroundColor(item.itemBakColor)
        }
        return view
    }

    fun loadEngines() {
        val engList = gc.ttSgl()!!.ttobj?.engines
        for (engine in engList!!) {
            //gc.log( "engine: ${engine.name}  ${engine.label}  ${engine.icon}")
            val item = TtsItem()
            item.name = engine.name
            item.label = engine.label
            //gc.log("load engine: ${engine.toString()} -- ${engine.name}  ${engine.label}")
            this.add(item)
        }
        this.notifyDataSetChanged()

    }

}