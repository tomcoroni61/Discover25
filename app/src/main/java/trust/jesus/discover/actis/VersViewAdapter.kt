package trust.jesus.discover.actis

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import trust.jesus.discover.R
import trust.jesus.discover.bible.dataclasses.VersItem
import trust.jesus.discover.dlg_data.LvAdapt

class VersViewAdapter (context: Context) : LvAdapt<VersItem?>(context, R.layout.vers_item) {
//    : ArrayAdapter<VersItem>(context, 0, numbersList) {

    private var textsize = 21

    init {
       textsize = gc.appVals().valueReadInt("bibletext.size", textsize)

    }
    private class ViewHolder(view: View, pos: Int) {
        val tvVers: TextView = view.findViewById(R.id.tvVers)
        var position = pos
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView: View? = convertView
        val holder: ViewHolder

        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.vers_item, parent, false)
            holder = ViewHolder(itemView, position)
            itemView.tag = holder
            holder.tvVers.tag = holder
            holder.tvVers.setOnClickListener { view ->
                //gc.log("onClick: " + position)
                if (view.tag != null) {
                    //gc.log("tagi..: " + position)
                    val holdit = view.tag as ViewHolder
                    val item = getItem(holdit.position)
                    if (item != null) {
                        item.selected = !item.selected
                        /*if (item.selected) {//..
                            holdit.tvVers.setBackgroundResource(R.drawable.speech_sel)
                            //holder.tvVers.setBackgroundColor(Color.Blue.hashCode()) selected
                        } else holdit.tvVers.setBackgroundResource(0) *///view.isSelected = item.selected
                        notifyDataSetChanged()
                        //gc.log("selected: " + item.selected + " pos: " + holdit.position)
                    }
                }
            }

        }
        else holder = itemView.tag as ViewHolder

        val currentVers = getItem(position) ?: return itemView!!

        //update holder with Item from getItem(position) = currentVers
        holder.tvVers.text = currentVers.text
        holder.tvVers.textSize = textsize.toFloat()
        holder.position = position  //for onclick
        if (currentVers.selected) {
            holder.tvVers.setBackgroundResource(R.drawable.speech_sel)
            //holder.tvVers.setBackgroundColor(Color.Blue.hashCode()) selected
        } else holder.tvVers.setBackgroundResource(0)

        return itemView
    }

    private fun selCount(): Int {
        var cnt = 0
        for (i in 0..<count) {
            val item = getItem(i)
            if (item != null && item.selected) cnt++
        }
        return cnt
    }
    fun unSelectAll() {
        for (i in 0..<count) {
            val item = getItem(i)
            if (item != null && item.selected) item.selected = false
        }
        notifyDataSetChanged()
    }
    fun textSizePlus() {
        textsize++
        notifyDataSetChanged()
        gc.appVals().valueWriteInt("bibletext.size", textsize)
    }
    fun textSizeMinus() {
        textsize--
        notifyDataSetChanged()
        gc.appVals().valueWriteInt("bibletext.size", textsize)
    }

    fun selectedText(forSpeak: Boolean = false): String {
        var txt = ""
        var selCount = selCount()
        gc.log("selectedText count: $selCount")
        when (selCount) {
            0 -> return ""
            2 -> {
                for (i in 0..<count) {
                    val item = getItem(i)
                    if (item != null) {
                      if (item.selected)  selCount--
                      when (selCount) {
                          1 -> if (forSpeak) txt += item.vers + "\n"
                              else txt += item.text + "\n"
                          0 -> {
                              if (forSpeak) txt += item.vers + "\n"
                              else txt += item.text + "\n"
                              return txt
                          }
                      }
                    }
                }

            } //->2
            else -> for (i in 0..<count) {
                val item = getItem(i)
                if (item != null && item.selected) {
                    if (forSpeak) txt += item.vers + "\n"
                    else txt += item.text + "\n"
                }
            }

        }
        return txt
    }

}
