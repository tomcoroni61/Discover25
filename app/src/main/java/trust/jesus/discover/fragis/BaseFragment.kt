package trust.jesus.discover.fragis

import androidx.fragment.app.Fragment
import trust.jesus.discover.little.Globus
import java.util.Random

open class BaseFragment: Fragment()  {
    val gc: Globus = Globus.Companion.getAppContext() as Globus
    val random: Random = Random()

    /*
    binding name ist von xml:
    return inflater.inflate(R.layout.fragment_word, container, false)
      = FragmentWordBinding.inflate(layoutInflater)

    BaseFragment(), View.OnClickListener

    "New Fragment" helpers:

    fragxx : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragLetterBinding

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        binding = FragLetterBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        // binding.wtvSpeak.setOnClickListener(this)
        // binding.btnRandomvers.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }


}

*****  fragxx  end *********


    import trust.jesus.discover.databinding.FragmentEntdeckeBinding

    private lateinit var binding: FragmentEntdeckeBinding
    createview
    binding = FragmentEntdeckeBinding.inflate(layoutInflater)

    in xml
    tools:context="trust.jesus.discover.fragis.EntdeckeFrag"
    trust.jesus.discover.little.ShTextView
    = ++ wrong "package header !!  ++
    trust.jesus.biblelearn.ui.main.adapters.ShTextView mystik??

    scrollview
    android:layout_above="@+id/wtvSpeak"
    oder/und  android:layout_below="@+id/ybtprevdat"

    2 bere löschen
    3x unten android:layout_marginBottom="35dp" ändern
     */
}