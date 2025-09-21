package trust.jesus.discover.dlg_data

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import org.apmem.tools.layouts.FlowLayout
import trust.jesus.discover.R
import trust.jesus.discover.little.Globus
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.Objects

class FileDlg (
    a: Context, fileselecttype: String, filefilter: String?,
    simpleFileDialogListener: SimpleFileDialogListener?
) : Dialog(a), View.OnClickListener {
    //var c: Activity?
    var d: Dialog? = null
    var yes: Button? = null
    var no: Button? = null

    private var m_dir = ""
    private var m_subdirs: MutableList<String>? = null
    private var m_goToUpper = false
    var ChoosenDir: String? = null
    var default_file_name: String = "BackUp.csv"
    var selected_file_name: String = default_file_name

    // private static final int FolderChoose = 2;
    // TODO Auto-generated constructor stub tvTitel
    private val m_context: Context = a
    private var m_sdcardDirectory: String?
    private val m_fileFilter: String
    private val Select_type: Int
    private var fileList: FlowLayout? = null
    private val m_SimpleFileDialogListener: SimpleFileDialogListener?
    private val gc = Globus.getAppContext() as Globus
    private var edFile: EditText? = null
    private var tvDir: TextView? = null

    init {
        if (filefilter != null) m_fileFilter =
            filefilter.lowercase(Locale.getDefault()) else m_fileFilter = ""
        when (fileselecttype) {
            "FileSave" -> Select_type = FileSave
            "FileOpen.." -> {
                Select_type = FileOpen
                m_goToUpper = true
            }

            "FileSave.." -> {
                Select_type = FileSave
                m_goToUpper = true
            }

            else -> Select_type = FileOpen
        }
        m_SimpleFileDialogListener = simpleFileDialogListener
        m_sdcardDirectory = Environment.getExternalStorageDirectory().absolutePath
        try {
            m_sdcardDirectory = File(m_sdcardDirectory.toString()).canonicalPath
        } catch (ignored: IOException) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.filedlg)
        yes = findViewById<Button>(R.id.btn_yes)
        no = findViewById<Button>(R.id.btn_no)
        yes!!.setOnClickListener(this)
        no!!.setOnClickListener(this)
        fileList = findViewById<FlowLayout?>(R.id.flFileList)
        edFile = findViewById<EditText>(R.id.edFileName)
        tvDir = findViewById<TextView>(R.id.tvDirName)
        findViewById<View?>(R.id.btn_delete)?.setOnClickListener(this)

        val mtitleView1 = findViewById<TextView>(R.id.tvTitel)
        if (Select_type == FileOpen) mtitleView1.text = gc.getString(R.string.open)
        if (Select_type == FileSave) mtitleView1.text = gc.getString(R.string.save_as)

        //if (Select_type == FolderChoose) m_titleView1.setText("Folder Select:");

        //fileList.removeAllViews();btn_delete
        // ((Button) findViewById(R.id.btn_delete)).setOnClickListener(this);
        //Ne: TextView wi = findViewById(R.id.tvBottom);        wi.setWidth(gc.getPopUpWidth()*2);
        Objects.requireNonNull<Window>(window).setLayout(gc.popUpWidth, RelativeLayout.LayoutParams.WRAP_CONTENT)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_yes -> if (m_SimpleFileDialogListener != null) {
                run {
                    if (Select_type == FileOpen || Select_type == FileSave) {
                        selected_file_name = edFile!!.text.toString() + "" //m_dir + "/" +
                        ChoosenDir = m_dir
                        m_SimpleFileDialogListener!!.onChosenDir(selected_file_name)
                    } else {
                        m_SimpleFileDialogListener!!.onChosenDir(m_dir)
                    }
                }
            }

            R.id.btn_delete -> {
                askDelete()
                return
            }

            else -> {}
        }
        dismiss()
    }


    // ***********  from SimpleFileDialog  *********************
    private fun getDirectories(dir: String): MutableList<String> {
        val dirs: MutableList<String> = ArrayList<String>()
        try {
            val dirFile = File(dir)

            // if directory is not the base sd card directory add ".." for going up one directory
            if ((m_goToUpper || m_dir != m_sdcardDirectory)
                && "/" != m_dir
            ) {
                dirs.add("..")
            }
            Log.d("~~~~", "m_dir=" + m_dir)
            if (!dirFile.exists() || !dirFile.isDirectory) {
                return dirs
            }

            /* OK:         File[] files = dirFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return (pathname.getName().endsWith(".img"));
                    or ?
                    return (file.getPath().endsWith(".jpg")||file.getPath().endsWith(".jpeg"));
                }
            });
 */

//org
            for (file in dirFile.listFiles()!!) {
                if (file.isDirectory) {
                    // Add "/" to directory names to identify them in the list
                    dirs.add(file.name + "/")
                } else if (Select_type == FileSave || Select_type == FileOpen) {
                    // Add file names to the list if we are doing a file save or file open operation
                    val fname = file.name
                    if (m_fileFilter.isEmpty() || fname.lowercase(Locale.getDefault())
                            .endsWith(m_fileFilter)
                    ) dirs.add(fname)
                }
            }
        } catch (ignored: Exception) {
        }

        dirs.sortWith(Comparator.naturalOrder())
        return dirs
    }

    fun chooseFileOrDir(dir: String?) {
        var dir = dir
        var dirFile = File(dir!!)
        while (!dirFile.exists() || !dirFile.isDirectory) {
            dir = dirFile.parent
            checkNotNull(dir)
            dirFile = File(dir)
            //Log.d("~~~~~","dir="+dir);
        }
        // Log.d("~~~~~","dir="+dir);
        //m_sdcardDirectory
        try {
            dir = File(dir).canonicalPath
        } catch (ioe: IOException) {
            return
        }
        //fileList = dlg.findViewById(R.id.flFileList);
        m_dir = dir
        m_subdirs = getDirectories(dir)
        create()

        fillFileList()
        show()
    }

    private fun onTextViewClick(view: View?) {
        val m_dir_old = m_dir
        val textView = view as TextView
        var sel = textView.text
            .toString() // ((AlertDialog) dialog).getListView().getAdapter().getItem(item);
        if (sel.get(sel.length - 1) == '/') sel = sel.substring(0, sel.length - 1)

        // Navigate into the sub-directory
        if (sel == "..") {
            m_dir = m_dir.substring(0, m_dir.lastIndexOf("/"))
            if (m_dir.isEmpty()) {
                m_dir = "/"
            }
        } else {
            m_dir += "/" + sel
        }
        selected_file_name = default_file_name

        if ((File(m_dir).isFile))  // If the selection is a regular file
        {
            m_dir = m_dir_old
            selected_file_name = sel
        }

        updateDirectory()
    }

    private fun deleteFile(df: String): Boolean {
        val deleFile = File(df)
        if (!deleFile.exists()) return false
        return deleFile.delete()
    }

    private fun createSubDir(newDir: String): Boolean {
        val newDirFile = File(newDir)
        if (!newDirFile.exists()) return newDirFile.mkdir()
        else return false
    }

    private fun updateDirectory() {
        m_subdirs!!.clear()
        m_subdirs!!.addAll(getDirectories(m_dir))
        // todo m_titleView.setText(m_dir);
        //m_listAdapter.notifyDataSetChanged();
        //#scorch
        if (Select_type == FileSave || Select_type == FileOpen) {
            edFile!!.setText(selected_file_name)
        }
        fillFileList()
    }

    private fun isDir(aNme: String): Boolean {
        return (aNme.contains("..") || aNme.indexOf('/') > -1)
    }

    private fun fillFileList() {
        if (fileList == null) return
        fileList!!.removeAllViews()
        if (m_subdirs == null) return
        tvDir!!.text = m_dir
        for (s in m_subdirs) {
            if (isDir(s)) addFile(s)
        }
        for (s in m_subdirs) {
            if (!isDir(s)) addFile(s)
        }
    }

    private fun addFile(aWord: String?) {
        if (aWord == null) return
        val textView: TextView
        //if (fileList==null) textView = new TextView(m_context); else
        if (!isDir(aWord)) textView = LayoutInflater.from(fileList!!.context)
            .inflate(R.layout.dlgfile, fileList, false) as TextView else textView =
            LayoutInflater.from(
                fileList!!.context
            ).inflate(R.layout.dlgfolder, fileList, false) as TextView
        textView.text = aWord
        //textView.setTextSize(12);
        textView.gravity = Gravity.CENTER
        // textView.setTag(idx);
        //textView.setBackgroundResource(R.drawable.rounded_corner);
        textView.setOnClickListener { view: View? -> this.onTextViewClick(view) }
        fileList!!.addView(textView)
    }

    private fun askDelete() {
        val input = EditText(m_context)
        input.setText(selected_file_name)
        AlertDialog.Builder(m_context).setTitle ("File to delete:").setView (input).setPositiveButton(
            "OK"
        ) { dialog: DialogInterface?, whichButton: Int ->
            val newDir = input.text
            val newDirName = newDir.toString()
            // Create new directory
            if (deleteFile("$m_dir/$newDirName")) {
                updateDirectory()
            }
        }.setNegativeButton("Cancel", null).show()
    }


    fun interface SimpleFileDialogListener {
        fun onChosenDir(chosenDir: String)
    }

    companion object {
        private const val FileOpen = 0
        private const val FileSave = 1
    }
} //end class

