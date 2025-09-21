package trust.jesus.discover.dlg_data

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.FileProvider
import trust.jesus.discover.BuildConfig
import trust.jesus.discover.little.Globus
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

open class Dateien {
    // ohne ? bei String? wird crashi 31.8 bei rLine weg und hobs musste backup nehmen
    var rLine: String? = null
    var Crash: String? = " "

    fun assetFileToPrivate(filename: String): Boolean {
        if (!openAssetInputStream(filename)) return false
        if (!openOutputStream(filename, Context.MODE_PRIVATE)) return false

        while (readLine()) {
            if (!writeLine(rLine)) break
        }
        closeOutputStream()
        closeInputStream()
        return true
    }

    fun copyFileToPrivate(uri: Uri, saveName: String?) {
        /*File yourFile = ...; ContentResolver contentResolver  String filePath = uri.getPath();
FileOutputStream outputStream = (FileOutputStream) contentResolver.openOutputStream(destination);
        uri.getLastPathSegment();
        if (openInputStream(uri.getPath())) {
            if (!openOutputStream(uri.getLastPathSegment(), Context.MODE_PRIVATE)) return;

        }

         */
        var saveName = saveName
        try {
            if (saveName == null) saveName = "unknown.txt"
            val contentResolver = aContext.contentResolver
            val inputStream =
                contentResolver.openInputStream(uri) as FileInputStream? //(FileInputStream) new FileInputStream(uri.getPath());
            val outputStream = aContext.openFileOutput(saveName, Context.MODE_PRIVATE)
            if (outputStream != null && inputStream != null) {
                val buffer = ByteArray(1024)
                var length: Int
                while ((inputStream.read(buffer).also { length = it }) > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.close()
                inputStream.close()
            }
            gc.Logl("copy file done", true)
        } catch (e: IOException) {
            gc.Logl("failed: " + e.message, true)
        }
    }

    fun hasPrivateFile(aFileName: String): Boolean {
        val privateRootDir = aContext.filesDir
        val file = File(privateRootDir, aFileName)
        return file.exists()
    }

    //done by KI:
    fun writePrivateFile(aFileName: String, aText: String) {
        try {
            val privateRootDir = aContext.filesDir
            val file = File(privateRootDir, aFileName)
            file.writeText(aText)
        } catch (e: IOException) {}
    }
    fun backUpPrivateTextFile(aFileName: String, backUpName: String) {
        val privateRootDir = aContext.filesDir
        val readFile = File(privateRootDir, aFileName)
        val writeFile = File(privateRootDir, backUpName)
        writeFile.writeText(readFile.readText())
    }

//for dir: https://stackoverflow.com/questions/45193941/how-to-read-and-write-txt-files-in-android-in-kotlin

    fun readLine(): Boolean {
        try {
            rLine = reader!!.readLine()
        } catch (e: Exception) {
            //e.printStackTrace();   Toast.makeText(aContext, "failed to read ", Toast.LENGTH_LONG).show();
            if (Crash!!.length < 3) Crash = "readLine"
            return false
        }
        return (rLine != null)
    }


    private val gc: Globus = Globus.getAppContext() as Globus
    private var outputStream: FileOutputStream? = null
    private var inputStream: FileInputStream? = null
    var reader: BufferedReader? = null //mit openInputstream...
    private val aContext: Context


    init {
        aContext = gc.applicationContext
    }

    private fun openAssetInputStream(fileName: String): Boolean {
        try {  //try .. ando will das
            val assetManager = aContext.assets
            val `is` = assetManager.open(fileName)
            val tmp = InputStreamReader(`is`, StandardCharsets.UTF_8)
            if (reader != null) reader!!.close()
            reader = BufferedReader(tmp)
            //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (e: FileNotFoundException) {
            return false
            // that's OK, we probably haven't created it yet
        } catch (e: Exception) {
            //e.printStackTrace();
            Toast.makeText(aContext, "failed to open for load ", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    fun openInputStream(FileName: String?): Boolean {
        try {  //try .. ando will das
            if (inputStream != null) inputStream!!.close()
            inputStream = aContext.openFileInput(FileName)
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
            Toast.makeText(aContext, "failed to open for load ", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    fun closeOutputStream() {
        try {
            if (outputStream != null) outputStream!!.close()
            outputStream = null
        } catch (e: Exception) {
            //e.printStackTrace();
            gc.Logl("failed to close ", false)
        }
    }

    fun openOutputStream(FileName: String?, mode: Int): Boolean {
        try {
            if (outputStream != null) closeOutputStream()
            outputStream = aContext.openFileOutput(FileName, mode) //outputStream.flush();
            //  gc.iLog("DataDir: "+aContext.getFilesDir());
        } catch (e: Exception) {
            //e.printStackTrace();
            Toast.makeText(aContext, "failed to open for write " + FileName, Toast.LENGTH_LONG)
                .show()
            //            gc.iLog("failed to open for write "+ FileName);
            return false
        }
        return true
    }

    fun closeInputStream() {
        try {
            if (inputStream != null) inputStream!!.close()
            inputStream = null
            if (reader != null) {
                reader!!.close()
                reader = null
            }
        } catch (t: Throwable) {
            Toast.makeText(aContext, "Exception: " + t, Toast.LENGTH_LONG).show()
        }
    }

    fun writeLine(aLine: String?): Boolean {
        try {
            outputStream!!.write((aLine + "\n").toByteArray())
        } catch (e: Exception) { //gc.iLog("failed to  write ");
            //e.printStackTrace();
            return false
        }
        return true
    }

    fun getDisplayName(uri: Uri?): String? {
        var displayName: String? = "?"
        if (uri == null) return displayName
        val uriString = uri.toString()
        val myFile = File(uriString)


        if (uriString.startsWith("content://")) {
            val cursor: Cursor?
            cursor = gc.contentResolver.query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val cidx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cidx > -1) displayName = cursor.getString(cidx)
                cursor.close()
            }
        } else if (uriString.startsWith("file://")) {
            displayName = myFile.name
        }
        return displayName
    }

    fun shareFile(filePath: String?, context: Context) {
        //needs provider and shar intent in manifest + xml/path.xml
        val privateRootDir = context.filesDir
        val file = File(privateRootDir, filePath.toString())

        try {
            val share = Intent(Intent.ACTION_SEND)

            //share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (file.exists()) {
                share.type = "text/plain" //.. text/*

                //gc.Logl(" getUriForFile " + file.getAbsolutePath(), true);

                //Oks
                val uri = FileProvider.getUriForFile(
                    (context),
                    BuildConfig.APPLICATION_ID + ".provider", file
                )


                //Uri uri =FileProvider.getUriForFile(this,  BuildConfig.APPLICATION_ID + ".provider", file);


                //Uri uri = FileProvider.getUriForFile(gc.getApplicationContext(),"com.e.versmix.fileprovider",file);
                share.putExtra(Intent.EXTRA_STREAM, uri)
                //gc.Logl(" shareFile startActivity ", true);
                context.startActivity(Intent.createChooser(share, "Share table"))
            } else gc.Logl("$filePath not found", true)
        } catch (e: Exception) {
            gc.Logl("ShareFile_Crash: " + e.message, true)
        }
    } /*
    public void toDownloadFolder(String url) {
        DownloadManager downloadManager = (DownloadManager) aContext.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url)) // 5.
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // 6.
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "filename_to_save"); // 7.
        downloadManager.enqueue(request); // 8.
        Toast.makeText(aContext, "Download started", Toast.LENGTH_SHORT).show(); // 9.

    }

    private static class downloadPDFTask extends BaseTask<Long> {

        final Context context;
        final String urlString

        public downloadPDFTask(Context context, String urlString) {
            this.context = context;
            this.urlString = urlString;
        }

        @Override
        public Long doInBackground() {
            DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(urlString);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("Java_Programming.pdf");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Java_Programming.pdf");
            return downloadmanager.enqueue(request);
        }

        @Override
        public void onPostExecute(Long result) {
            context.receiveDownloadId(result);
            Toast.makeText(context, "Dowload completed!", Toast.LENGTH_SHORT).show();
        }
    }
*/
}

