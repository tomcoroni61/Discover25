package trust.jesus.discover.dlg_data

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.RelativeLayout
import trust.jesus.discover.R
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import trust.jesus.discover.databinding.DlgTtsxBinding
import trust.jesus.discover.little.Globus
import java.util.Locale
import java.util.Objects

class TtsDlg(a: Context, themeResId: Int): AppCompatDialog(a, themeResId), View.OnClickListener {

    lateinit var binding: DlgTtsxBinding
    private var gc = Globus.getAppContext() as Globus
    private var ttsAdapter: TtsAdapter? = null
    private var itemClickIdx = 0
    private var itemNormalColor = Color.MAGENTA //Black
    private var itemChooseColor = Color.BLUE
    private var ignoreChange=false


    override fun onCreate(savedInstanceState: Bundle?) {
        window?.setBackgroundDrawableResource(android.R.color.transparent);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        //window?.setBackgroundDrawableResource(android.R.color.transparent);

        super.onCreate(savedInstanceState)
        binding = DlgTtsxBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnChangeTts.setOnClickListener(this)
        binding.btnTest.setOnClickListener(this)
        binding.btnSpeak.setOnClickListener(this)

        ttsAdapter = TtsAdapter(context)
        binding.lvTtsEngines.adapter = ttsAdapter
        ttsAdapter!!.loadEngines()

        binding.chipLog.setOnCheckedChangeListener{ _, isChecked ->
            if (ignoreChange) return@setOnCheckedChangeListener
            val ne = ttsAdapter!!.getItem(itemClickIdx)?.name
            gc.appVals().valueWriteBool(ne+"_restart", isChecked)
        }
        binding.lvTtsEngines.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                itemClickIdx = position
                val ne = ttsAdapter!!.getItem(itemClickIdx)?.name
                val bb = gc.appVals().valueReadBool(ne+"_restart", false)
                ignoreChange = true
                binding.chipLog.isChecked = bb
                ignoreChange = false
                handleItemClick()
            }
        val typedValue = TypedValue()
        gc.mainActivity!!.theme.resolveAttribute(R.attr.sysbar_colour, typedValue, true)
        val color = ContextCompat.getColor(this.context, typedValue.resourceId)
        itemNormalColor = binding.lvTtsEngines.solidColor
        itemChooseColor = color
        binding.tvCurrent.text = gc.ttSgl()?.currentEngine()

        Objects.requireNonNull<Window>(window).setLayout(gc.globDlg().popUpWidth, RelativeLayout.LayoutParams.WRAP_CONTENT)
    }

    private fun doVoices() {
        val voices = gc.ttSgl()!!.ttobj?.voices
        if (voices == null) return
        for (voice in voices) {
            gc.log( "voice: ${voice.name}")
            if (voice.locale == Locale.LanguageRange("de"))
                gc.logl("found german voice: ${voice.name}", true)
        }

    }
    private fun handleItemClick() {
        for (i in 0..<ttsAdapter!!.count) {
            val item = ttsAdapter!!.getItem(i) ?: continue
            item.itemBakColor = itemNormalColor
            if (i == itemClickIdx) {
                item.itemBakColor = itemChooseColor
                //binding.tvCurrent.setText(item.suche)
                //binding.edReplace.setText(item.ersetze)
            }
            //if (!item.itemBakColor.equals(itemChooseColor))
        }

        ttsAdapter!!.notifyDataSetChanged()
        //ArrowButs()
    }

    private fun btnTestClick() {
        binding.tvCurrent.text = gc.ttSgl()!!.currentEngine()
    }

    private fun btnChangeTtsClick() {
        //gc.ttSgl()!!.ttobj?.Eng //= ttsAdapter!!.getItem(itemClickIdx)?.name
        val ne = ttsAdapter!!.getItem(itemClickIdx)?.name
        gc.ttSgl()!!.defEngine = ne.toString()
        gc.ttSgl()!!.restart()
        binding.tvCurrent.text = gc.ttSgl()!!.currentEngine()
        gc.appVals().valueWriteString("currentEngine", ne.toString())
        doVoices()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnChangeTts -> btnChangeTtsClick()
            R.id.btnTest -> btnTestClick()
            R.id.btnSpeak -> gc.ttSgl()!!.speak(binding.sedVersTxt.text.toString())
        }

    }


}