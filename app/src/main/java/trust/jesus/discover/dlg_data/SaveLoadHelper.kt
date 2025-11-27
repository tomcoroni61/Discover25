package trust.jesus.discover.dlg_data

import android.content.Context
import trust.jesus.discover.little.Globus
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStreamReader

class SaveLoadHelper {

    private var outputStream: FileOutputStream? = null
    private var inputStream: FileInputStream? = null
    private var reader: BufferedReader? = null //mit openInputstream...
    var rLine: String? = null
    var Crash: String? = " "
    val gc: Globus = Globus.Companion.getAppContext() as Globus

    private fun openInputStream(fileName: String?): Boolean {
        try {  //try .. ando will das
            if (inputStream != null) inputStream!!.close()
            inputStream = gc.openFileInput(fileName)
            if (inputStream != null) {
                val tmp = InputStreamReader(inputStream)
                if (reader != null) reader!!.close()
                reader = BufferedReader(tmp)
            } else return false
        } catch (e: FileNotFoundException) {
            inputStream = null
            return false
            // that's OK, we probably haven't created it yet
        } catch (e: Exception) {
            //e.printStackTrace();
            gc.errReport(e, "openInputStream", true)
            return false
        }
        return true
    }

    private fun readLine(): Boolean {
        try {
            rLine = reader!!.readLine()
        } catch (e: java.lang.Exception) {
            //e.printStackTrace();   Toast.makeText(aContext, "failed to read ", Toast.LENGTH_LONG).show();
            if (Crash!!.length < 3) Crash = "readLine"
            return false
        }
        return (rLine != null)
    }

    private fun writeLine(aLine: String?): Boolean {
        try {
            outputStream!!.write((aLine + "\n").toByteArray())
        } catch (e: java.lang.Exception) { //gc.iLog("failed to  write ");
            //e.printStackTrace();
            return false
        }
        return true
    }

    fun insertLine(aFileName: String?, Line1: String?, Line2: String?, maxLines: Int) {
        val list = ArrayList<String?>()
        var cnt = 0

        if (openInputStream(aFileName)) {
            while (readLine() && cnt < maxLines) {
                cnt++
                if (!list.add(rLine)) break
            }
            closeInputStream()
        }

        list.add(0, " ")
        list.add(0, Line1)
        list.add(0, Line2)
        if (!openOutputStream(aFileName, Context.MODE_PRIVATE)) return
        for (aStrLine in list) {
            if (!writeLine(aStrLine)) return
        }
        closeOutputStream()
    }

    private fun closeOutputStream() {
        try {
            if (outputStream != null) outputStream!!.close()
            outputStream = null
        } catch (e: java.lang.Exception) {
            gc.errReport(e, "failed to close ", true)

        }
    }

    private fun openOutputStream(FileName: String?, mode: Int): Boolean {
        try {
            if (outputStream != null) closeOutputStream()
            outputStream = gc.openFileOutput(FileName, mode) //outputStream.flush();
            //  gc.iLog("DataDir: "+aContext.getFilesDir());
        } catch (e: java.lang.Exception) {
            gc.errReport(e, "failed to open for write ", true)
            return false
        }
        return true
    }


    private fun closeInputStream() {
        try {
            if (inputStream != null) inputStream!!.close()
            inputStream = null
            if (reader != null) {
                reader!!.close()
                reader = null
            }
        } catch (t: Throwable) {
            gc.errReport(t as Exception, "closeInputStream", true)
           // Toast.makeText(aContext, "Exception: " + t, Toast.LENGTH_LONG).show()
        }
    }


}