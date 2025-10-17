package trust.jesus.discover.dlg_data

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import trust.jesus.discover.R
import java.lang.String
import java.util.Random
import kotlin.Boolean
import kotlin.Char
import kotlin.CharArray
import kotlin.Exception
import kotlin.Int

import trust.jesus.discover.fragis.LettersFrag
import trust.jesus.discover.little.Celli
import trust.jesus.discover.little.Globus

class ItemAr(context: Context, gridView: GridView, activity: LettersFrag) :
    LvAdapt<Celli>  (context, android.R.layout.simple_list_item_2) {
    private val mContext: Context = context
    var movecnt: Int = 0
    var usermoves: Int = 0
    private var selidx = -1
    private var letterCount = -1
    private var idxRand = 0
    private val random = Random()
    private val mActivity: LettersFrag = activity
    private val mGridView: GridView = gridView
    private var MischLetters = "AH"
    private val gc: Globus = Globus.getAppContext() as Globus

    fun clearme() {
        var item: Celli? = getItem(0)
        while (item != null) {
            remove(item)
            item = getItem(0)
        }
    }

    fun loadLetters(mLetters: CharArray) {
        // ListAdapter ma = mGridView.getAdapter();
        //mGridView.setAdapter(null);

        var item: Celli?
        var c: Char
        var wordCount = 0
        for (i in mLetters.indices) {
            item = Celli()
            c = mLetters[i]
            item.CharOk = c
            item.CharVisi = c
            item.idxOk = i
            item.idxVisi = i
            item.position = i
            item.mischMoved = false
            if (c == ' ') wordCount++ else {
                item.wordID = wordCount
                letterCount++
            }
            add(item)
        }
        // mGridView.setAdapter(ma);
    }

    fun mischen() {
        if (letterCount < 10) return
        idxRand = 0
        var chrashCnt = 0
        var cix = letterCount / 8
        var loopcnt = 999
        var moves = cix + random.nextInt(1 + (letterCount / 22))
        movecnt = 0
        usermoves = 0
        //if (moves < 10) moves=11;
        MischLetters = ""
        var vonCell: Celli?
        var nachCell: Celli?
        try {
            while (moves > 0 && loopcnt > 0) {
                chrashCnt++; loopcnt--
                if (loopcnt < 222) cix = -3 else cix = -1
                cix = getRandomCellIdx(139, cix)
                vonCell = getItem(cix) //getRandomCell(9, -1);
                if (vonCell == null) continue
                //Log.d(LOG_TAG, "got .. Random idx: " + cix  )
                cix = getRandomCellIdx(146, vonCell.position)
                //Log.d(LOG_TAG, "got .. Random idx: " + cix  );
                nachCell = getItem(cix) //getRandomCell(19, vonCell.position);
                chrashCnt++
                //Log.d(LOG_TAG, " vonCell null: " + (vonCell==null) + " nachCell null: " + (nachCell==null) );
                if (nachCell != null) { // && vonCell.wordID!=nachCell.wordID
                    if (vonCell.CharOk != nachCell.CharOk) {
                        doSwitch(vonCell, nachCell)
                        if (vonCell.wordID == nachCell.wordID) nachCell.mischMoved = false
                        movecnt++
                        moves--
                    }

                    //gc.Logl("moved: " + vonCell.position + " nach: " + nachCell.position, true);
                    //MischLetters = MischLetters + nachCell.CharOk + vonCell.CharOk;
                    // Log.d(LOG_TAG, "moved: " + vonCell.position + " nach: " + nachCell.position);
                }

            }
            //Toast.makeText(getContext(), "moved: "+movecnt, Toast.LENGTH_LONG).show();
            //Log.d(LOG_TAG, "moved: " + movecnt);
        } catch (e: Exception) {
            //e.printStackTrace();
            gc.Logl("ItemArCrash: " + e.message + chrashCnt, true)
            // Toast.makeText(getContext(), "Crash: "+e.getMessage()  + chrashCnt, Toast.LENGTH_LONG).show();
        }
    }

    private fun getRandomCellIdx(tries: Int, ignore: Int): Int {
        var tries = tries
        var max = count
        var idx: Int
        var retidx = -1
        // Log.d(LOG_TAG, "Item Count: " + max); MischLetters
        if (max < 11) return -1
        if (ignore == -3 && max > 22) max = 5
        var cel: Celli?
        while (tries > 0 && retidx == -1) {
            idx = random.nextInt(max - 1)
            //Log.d(LOG_TAG, "Random idx: " + Idx  );
            if (ignore > -1 && ignore == idx) break
            cel = getItem(idx)
            //Log.d(LOG_TAG, "idx: " + Idx + "  null: "+ (cel==null) );
            if (canMove(cel)) retidx = idx
            tries--
        }
        if (retidx == -1) for (i in idxRand..<max - 1) {
            cel = getItem(i)
            if (canMove(cel)) {
                idxRand = i
                retidx = i
                break
            }
        }
        // Log.d(LOG_TAG, "idxRand: " + idxRand);
        cel = getItem(retidx)
        if (retidx != -1 && cel != null) MischLetters = MischLetters + cel.CharOk
        return retidx
    }

    private fun canMove(item: Celli?): Boolean {
        if (item == null || item.CharOk == ' ' || item.mischMoved) return false
        if (MischLetters.length < 4) return true
        var lCnt = 0
        for (i in 0..<MischLetters.length) {
            val c = MischLetters.get(i)
            if (c == item.CharOk) lCnt++
        }
        return lCnt < 4
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var citem: Celli? = getItem(position)
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (citem == null) citem = Celli()

        // if (convertView == null) { not for GridView !!
        //assert inflater != null;
        //if (inflater==null) return null;
        val gridView: View = inflater.inflate(R.layout.cell, parent, false) // pull views set value into textview
        citem.letterView =
            gridView.findViewById<View?>(R.id.grid_itemletter) as TextView? // set values into views
        citem.letterView?.tag = citem.position
        citem.cellView = mGridView.getChildAt(citem.position)

        //citem.tv2 = gridView.findViewById(R.id.grid_itemlabel);
        gridView.tag = position


        //citem.position = position;
        //citem.idxVisi = position; ist immer 0
        if (citem.CharOk == ' ') citem.letterView?.visibility = View.INVISIBLE else citem.letterView?.setOnClickListener(
            { view1 ->
                val tv = view1 as TextView
                val idx = tv.tag as Int //tv.setText("?");wordCount
                val item: Celli? = getItem(idx)
                if (item == null) return@setOnClickListener
                if (item.position == item.idxVisi) return@setOnClickListener
                if (item.selected) selidx = -1
                item.selected = !item.selected

                // Log.d(LOG_TAG, "onClick.. selected: " + item.selected + " CharOk: " + item.CharOk);
                doItemState(item)
                if (allDone()) this.mActivity.lettersDone()
            })


        //String ah = position + "/"+citem.position + " C: " + citem.CharOk;
        // citem.tv2.setText(ah);
        setVisiText(citem)
        setBackground(citem)

        return gridView
    }

    override fun getItem(position: Int): Celli? {
        if (position < 0 || position >= count) return null
        return super.getItem(position)
    }

    private fun allDone(): Boolean {
        for (i in 0..<count) {
            val item: Celli? = getItem(i)
            if (item!!.idxVisi != item.position) return false
        }
        return true
    }

    private fun doItemState(item: Celli) {
        if (item.selected) {
            if (selidx > -1) {
                val selItem: Celli? = getItem(selidx)
                if (selItem == null) return
                selItem.selected = false
                item.selected = false
                //Log.d(LOG_TAG, "doItemState -- doSwitch ")
                doSwitch(item, selItem)
                setVisiText(item)
                setVisiText(selItem)
                setBackground(item)
                setBackground(selItem)
                usermoves++
                mActivity.updateClickLabel()
                //Log.d(LOG_TAG, "doItemState -- setBackground x2")
                selidx = -1
            } else {
                selidx = item.position
                //Log.d(LOG_TAG, "doItemState -- setBackground ")
                setBackground(item)
            }
        } else setBackground(item)
    }

    private fun setVisiText(item: Celli) {
        if (item.letterView == null) return
        if (item.idxVisi == item.position) item.letterView!!.text = String.valueOf(item.CharOk)
        else {
            val v: Celli? = getItem(item.idxVisi)
            if (v != null) item.letterView!!.text = String.valueOf(v.CharOk)
        }
    }

    private fun setBackground(item: Celli) {
        if (item.letterView == null) return
        if (item.idxVisi == item.position) item.letterView!!.setBackgroundResource(R.drawable.richtigplaz)
        else {
            val visi: Celli? = getItem(item.idxVisi)
            if (visi != null) {
                if (item.selected) {
                    if (item.wordID == visi.wordID) item.letterView!!.setBackgroundResource(R.drawable.richtigsel)
                    else item.letterView!!.setBackgroundResource(
                        R.drawable.selected
                    )
                } else {
                    if (item.wordID == visi.wordID) item.letterView!!.setBackgroundResource(R.drawable.richtig) else item.letterView!!.setBackgroundResource(
                        R.drawable.rounded_corner
                    )
                }
            }
        }
    }

    private fun doSwitch(von: Celli, nach: Celli) {
        val m: Int = nach.idxVisi
        nach.idxVisi = von.idxVisi
        von.idxVisi = m
        nach.mischMoved = true
        von.mischMoved = true
    }
}

