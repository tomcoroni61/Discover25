package trust.jesus.discover.little

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import trust.jesus.discover.R

class ShImageButton: androidx.appcompat.widget.AppCompatImageButton  {
    private var hideTime = 400

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        val a = context.theme.obtainStyledAttributes( attrs, R.styleable.shView, 0, 0 )
        try {
            this.hideTime = a.getInteger(R.styleable.shView_hideTime, 500)
        } finally {
            a.recycle()
        }
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    override fun performClick(): Boolean {
        this.visibility = INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed(
            { visibility = VISIBLE },
            hideTime.toLong()
        )
        return super.performClick()
    }
}