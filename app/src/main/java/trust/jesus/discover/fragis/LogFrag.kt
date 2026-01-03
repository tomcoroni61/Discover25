package trust.jesus.discover.fragis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import trust.jesus.discover.databinding.FragmentLogBinding
import trust.jesus.discover.little.FixStuff.Filenames.Companion.logName

class LogFrag : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentLogBinding

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? ): View {
        binding = FragmentLogBinding.inflate(inflater, container, false)
        loadLog()

        return binding.root

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_log, container, false)
    }

    fun loadLog() {
        //        gc.HS().doLog("loadlog");
        gc.log("loadLog")
        if (!gc.dateien().openInputStream(logName)) return
        binding.tvLogText.text = ""
        gc.log("loadLog  2")
        while (gc.dateien().readLine()) {
            binding.tvLogText.append(gc.dateien().rLine + "\n")
            gc.log("loadLog --")
        }
        gc.dateien().closeInputStream()

    }

    override fun onClick(p0: View?) {

    }


}