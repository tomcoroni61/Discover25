package trust.jesus.discover.dlg_data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Environment
import android.os.StatFs
import trust.jesus.discover.R
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FilenameFilter
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.util.Date
import java.util.Random

class ErrorReporter : Thread.UncaughtExceptionHandler {
    var VersionName: String? = null
    var PackageName: String? = null
    var FilePath: String? = null
    var PhoneModel: String? = null
    var AndroidVersion: String? = null
    var Board: String? = null
    var Brand: String? = null
    var Device: String? = null
    var Display: String? = null
    var FingerPrint: String? = null
    var Host: String? = null
    var ID: String? = null
    var Model: String? = null
    var Product: String? = null
    var Tags: String? = null
    var Time: Long = 0
    var Type: String? = null
    var User: String? = null
    var CustomParameters: HashMap<String?, String?> = HashMap()

    private var PreviousHandler: Thread.UncaughtExceptionHandler? = null
    private var CurContext: Context? = null

    fun AddCustomData(Key: String?, Value: String?) {
        CustomParameters.put(Key, Value)
    }

    private fun CreateCustomInfoString(): String {
        var CustomInfo = ""
        for (CurrentKey in CustomParameters.keys) {
            val CurrentVal = CustomParameters.get(CurrentKey)
            CustomInfo += CurrentKey + " = " + CurrentVal + "\n"
        }
        return CustomInfo
    }

    fun Init(context: Context) {
        PreviousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        CurContext = context
    }

    val availableInternalMemorySize: Long
        get() {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val availableBlocks = stat.availableBlocksLong
            return availableBlocks * blockSize
        }

    val totalInternalMemorySize: Long
        get() {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            return totalBlocks * blockSize
        }

    fun recoltInformations(context: Context) {
        try {
            val pm = context.packageManager
            // Version
            val pi: PackageInfo = pm.getPackageInfo(context.packageName, 0)
            VersionName = pi.versionName
            // Package name
            PackageName = pi.packageName
            // Device model
            PhoneModel = Build.MODEL
            // Android version
            AndroidVersion = Build.VERSION.RELEASE

            Board = Build.BOARD
            Brand = Build.BRAND
            Device = Build.DEVICE
            Display = Build.DISPLAY
            FingerPrint = Build.FINGERPRINT
            Host = Build.HOST
            ID = Build.ID
            Model = Build.MODEL
            Product = Build.PRODUCT
            Tags = Build.TAGS
            Time = Build.TIME
            Type = Build.TYPE
            User = Build.USER
        } catch (ignored: Exception) {
        }
    }

    fun CreateInformationString(): String {
        recoltInformations(CurContext!!)

        var ReturnVal = ""
        ReturnVal += "Version : " + VersionName
        ReturnVal += "\n"
        ReturnVal += "Package : " + PackageName
        ReturnVal += "\n"
        ReturnVal += "FilePath : " + FilePath
        ReturnVal += "\n"
        ReturnVal += "Phone Model" + PhoneModel
        ReturnVal += "\n"
        ReturnVal += "Android Version : " + AndroidVersion
        ReturnVal += "\n"
        ReturnVal += "Board : " + Board
        ReturnVal += "\n"
        ReturnVal += "Brand : " + Brand
        ReturnVal += "\n"
        ReturnVal += "Device : " + Device
        ReturnVal += "\n"
        ReturnVal += "Display : " + Display
        ReturnVal += "\n"
        ReturnVal += "Finger Print : " + FingerPrint
        ReturnVal += "\n"
        ReturnVal += "Host : " + Host
        ReturnVal += "\n"
        ReturnVal += "ID : " + ID
        ReturnVal += "\n"
        ReturnVal += "Model : " + Model
        ReturnVal += "\n"
        ReturnVal += "Product : " + Product
        ReturnVal += "\n"
        ReturnVal += "Tags : " + Tags
        ReturnVal += "\n"
        ReturnVal += "Time : " + Time
        ReturnVal += "\n"
        ReturnVal += "Type : " + Type
        ReturnVal += "\n"
        ReturnVal += "User : " + User
        ReturnVal += "\n"
        ReturnVal += "Total Internal memory : " + this.totalInternalMemorySize
        ReturnVal += "\n"
        ReturnVal += "Available Internal memory : " + this.availableInternalMemorySize
        ReturnVal += "\n"

        return ReturnVal
    }

    fun doException(e: Throwable) {
        var Report: String? = ""
        val CurDate = Date()
        Report += "Error Reportus collected on : " + CurDate
        Report += "\n"
        Report += "\n"
        Report += "Informations :"
        Report += "\n"
        Report += "=============="
        Report += "\n"
        Report += "\n"
        Report += CreateInformationString()

        Report += "Custom Informations :\n"
        Report += "=====================\n"
        Report += CreateCustomInfoString()

        Report += "\n\n"
        Report += "Stack : \n"
        Report += "======= \n"
        val result: Writer = StringWriter()
        val printWriter = PrintWriter(result)
        e.printStackTrace(printWriter)
        val stacktrace: String? = result.toString()
        Report += stacktrace

        Report += "\n"
        Report += "Cause : \n"
        Report += "======= \n"

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        var cause = e.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            Report += result.toString()
            cause = cause.cause
        }
        printWriter.close()
        Report += "****  End of current Reportus ***"
        SaveAsFile(Report)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        doException(e)
        //SendErrorMail( Reportus );
        PreviousHandler!!.uncaughtException(t, e)
    }

    private fun SendErrorMail(_context: Context, ErrorContent: String?) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        val subject = (_context.resources.getString(R.string.CrashReport_MailSubject)
                + _context.resources.getString(R.string.app_name))
        val body = _context.resources.getString(R.string.CrashReport_MailBody) +
                "\n\n" +
                ErrorContent +
                "\n\n"
        sendIntent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf<String?>(_context.getString(R.string.CrashReportEmailTo))
        )
        sendIntent.putExtra(Intent.EXTRA_TEXT, body)
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sendIntent.type = "message/rfc822"
        _context.startActivity(Intent.createChooser(sendIntent, "Title:"))
    }

    private fun SaveAsFile(ErrorContent: String) {
        try {
            val generator = Random()
            val random = generator.nextInt(99999)
            val FileName = "stack-" + random + ".stacktrace"
            val trace = CurContext!!.openFileOutput(FileName, Context.MODE_PRIVATE)
            trace.write(ErrorContent.toByteArray())
            trace.close()
        } catch (e: Exception) {
            // ...
        }
    }

    private fun getErrorFileList(): Array<String?>? {
        val dir = File("$FilePath/")
        // Try to create the files folder if it doesn't exist
        dir.mkdir()
        // Filter for ".stacktrace" files
        val filter = FilenameFilter { dir1: File?, name: String? -> name!!.endsWith(".stacktrace") }
        return dir.list(filter)
    }

    fun bIsThereAnyErrorFile(): Boolean {
        return getErrorFileList()?.size!! > 0
    }

    fun CheckErrorAndSendMail(_context: Context) {
        try {
            FilePath = _context.filesDir.absolutePath
            if (bIsThereAnyErrorFile()) {
                var WholeErrorText = ""
                // on limite à N le nombre d'envois de rapports ( car trop lent )
                val errorFileList = getErrorFileList()
                var curIndex = 0
                val maxSendMail = 5
                for (curString in errorFileList!!) {
                    if (curIndex++ <= maxSendMail) {
                        WholeErrorText += "New Trace collected :\n"
                        WholeErrorText += "=====================\n "
                        val filePath = FilePath + "/" + curString
                        val input = BufferedReader(FileReader(filePath))
                        var line: String?
                        while ((input.readLine().also { line = it }) != null) {
                            WholeErrorText += line + "\n"
                        }
                        input.close()
                    }

                    // DELETE FILES !!!!
                    val curFile = File(FilePath + "/" + curString)
                    curFile.delete()
                }
                SendErrorMail(_context, WholeErrorText)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private var S_mInstance: ErrorReporter? = null
        val instance: ErrorReporter
            get() {
                if (S_mInstance == null) S_mInstance = ErrorReporter()
                return S_mInstance!!
            }
    }
}

