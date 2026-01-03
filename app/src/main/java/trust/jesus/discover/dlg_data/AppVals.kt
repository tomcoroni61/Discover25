package trust.jesus.discover.dlg_data

import android.content.Context

//statt appvals: SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this); usw
class AppVals : Dateien() {
    private val aFileName = "appVals.txt"
    private var curValueName: String? = null

    fun valueReadBool(valueName: String?, defValue: Boolean): Boolean {
        if (!openInputStream(aFileName)) return defValue
        while (readLine()) {
            if (rLine.equals(valueName)) {
                curValueName = valueName
                return readBool()
            }
        }
        closeInputStream()
        return defValue
    }

    fun valueWriteBool(valueName: String?, value: Boolean) {
        val list = ArrayList<String?>()
        var cnt = 0
        var fIdx = -1

        if (openInputStream(aFileName)) {
            while (readLine()) {
                if (!list.add(rLine)) break
                cnt++
                if (!rLine!!.isEmpty() && rLine.equals(valueName)) fIdx = cnt
            }
            closeInputStream()
        }

        if (fIdx == -1) {
            list.add(valueName)
            list.add(value.toString())
        } else list.set(fIdx, value.toString())
        //list.add(0," ");  list.add(0,Line1);   list.add(0,Line2);
        if (!openOutputStream(aFileName, Context.MODE_PRIVATE)) return
        for (aStrLine in list) {
            if (!writeLine(aStrLine)) return
        }
        closeOutputStream()
    }

    fun valueWriteString(valueName: String?, value: String?) {
        val list = ArrayList<String?>()
        var cnt = 0
        var fIdx = -1

        if (openInputStream(aFileName)) {
            while (readLine()) {
                if (!list.add(rLine)) break
                cnt++
                if (!rLine!!.isEmpty() && rLine.equals(valueName)) fIdx = cnt
            }
            closeInputStream()
        }

        if (fIdx == -1 || fIdx == list.size) {
            list.add(valueName)
            list.add(value)
        } else list.set(fIdx, value)
        //list.add(0," ");  list.add(0,Line1);   list.add(0,Line2);
        if (!openOutputStream(aFileName, Context.MODE_PRIVATE)) return
        for (aStrLine in list) {
            if (!writeLine(aStrLine)) return
        }
        closeOutputStream()
    }

    fun valueReadString(valueName: String?, defValue: String?): String? {
        if (!openInputStream(aFileName)) return defValue
        while (readLine()) {
            if (rLine.equals(valueName)) return readString()
        }
        closeInputStream()
        return defValue
    }

    fun valueWriteInt(valueName: String?, value: Int): Boolean {
        val list = ArrayList<String?>()
        var cnt = 0
        var fIdx = -1

        if (openInputStream(aFileName)) {
            while (readLine()) {
                if (!list.add(rLine)) break
                cnt++
                if (!rLine!!.isEmpty() && rLine.equals(valueName)) fIdx = cnt
            }
            closeInputStream()
        }

        if (fIdx == -1) {
            list.add(valueName)
            list.add(value.toString())
        } else list.set(fIdx, value.toString())
        //list.add(0," ");  list.add(0,Line1);   list.add(0,Line2);
        if (!openOutputStream(aFileName, Context.MODE_PRIVATE)) return false
        for (aStrLine in list) {
            if (!writeLine(aStrLine)) return false
        }
        closeOutputStream()
        return true
    }

    fun valueReadInt(valueName: String?, defValue: Int): Int {
        if (!openInputStream(aFileName)) return defValue
        while (readLine()) {
            if (rLine.equals(valueName)) {
                curValueName = valueName
                return readInt()
            }
        }
        closeInputStream()
        return defValue
    }

    fun valueWriteLong(valueName: String?, value: Long) {
         valueWriteString(valueName, value.toString())
    }

    fun valueReadLong(valueName: String?, defValue: Long): Long {
        return valueReadString(valueName, defValue.toString())!!.toLong()
    }

    private fun readBool(): Boolean {
        try {
            rLine = reader!!.readLine()
        } catch (e: Exception) {
            Crash?.length?.let { if (it < 3) Crash = "readBool $curValueName" }
            //e.printStackTrace();   Toast.makeText(aContext, "failed to read ", Toast.LENGTH_LONG).show();
            return false
        }
        return rLine.toBoolean()
    }

    private fun readString(): String? {
        try {
            rLine = reader!!.readLine()
        } catch (e: Exception) {
            Crash?.length?.let { if (it < 3) Crash = "readString $curValueName" }
            //e.printStackTrace();   Toast.makeText(aContext, "failed to read ", Toast.LENGTH_LONG).show();
            return ""
        }
        return rLine
    }

    private fun readInt(): Int {
        try {
            rLine = reader!!.readLine()
            return rLine!!.toInt()
        } catch (e: Exception) {
            Crash?.length?.let { if (it < 3) Crash = "readInt $curValueName" }
            //e.printStackTrace();   Toast.makeText(aContext, "failed to read int", Toast.LENGTH_LONG).show();
            return 0
        }
    }
}
