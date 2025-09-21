package trust.jesus.discover.little

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import trust.jesus.discover.R

class ShButton : AppCompatButton {
    //    @InspectableProperty AppCompatButton
    var hideTime: Int = 700
    //use app:hideTime="1600" in xml



    constructor(context: Context) : super(context, null, android.R.attr.buttonStyle)


    constructor(context: Context, attrs: AttributeSet?) : super( context, attrs, android.R.attr.buttonStyle ) {
        init()
        val a = context.theme.obtainStyledAttributes( attrs, R.styleable.shView, 0, 0 )
        try {
            this.hideTime = a.getInteger(R.styleable.shView_hideTime, 500)
        } finally {
            a.recycle()
        }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr ) {
       // init()
    }

    private fun init() {
        //width= resources.getDimension(R.dimen.your_dimen).toInt()       height= resources.getDimension(R.dimen.your_dimen).toInt()
        //setBackgroundColor(ContextCompat.getColor(context,R.color.yourColor)) R.color.blue_tab_selected
        this.isAllCaps = false
        try {
                val typedValue = TypedValue()
                context.theme.resolveAttribute(R.attr.btn_img_colour, typedValue, true)
            val color = ContextCompat.getColor(context, typedValue.resourceId)
            setTextColor(color)
            } catch (_: Exception) { }

    }

    /*
    constructor(context: Context) : super(context, null, android.R.attr.buttonStyle)


    constructor(context: Context, attrs: AttributeSet?) : super( context, attrs, android.R.attr.buttonStyle ){
        val a = context.theme.obtainStyledAttributes( attrs, R.styleable.shView, 0, 0 )

        try {
            this.hideTime = a.getInteger(R.styleable.shView_hideTime, 500)
            this.textColors = a.getColorStateList(R.styleable.shView_textColor)
            this.isAllCaps = false
            //this.background = a.getDrawable(R.styleable.shView_background)
            //this.background = a.getDrawable(R.drawable.popspbtn) //"@color/blue_background"
        } finally {
            a.recycle()
        }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr ) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.shView,
            defStyleAttr, 0
        )

        try {
            this.hideTime = a.getInteger(R.styleable.shView_hideTime, 500)
            this.isAllCaps = false
            //this.background = a.getDrawable(R.styleable.shView_background)
            //this.background = a.getDrawable(R.drawable.popspbtn) //"@color/blue_background"
        } finally {
            a.recycle()
        }
    }

     */

    override fun performClick(): Boolean {
        this.visibility = INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed(
            { visibility = VISIBLE },
            hideTime.toLong()
        )
        return super.performClick()
    }
}
