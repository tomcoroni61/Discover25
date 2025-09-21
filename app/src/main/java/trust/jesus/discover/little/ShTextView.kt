package trust.jesus.discover.little

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import trust.jesus.discover.R

class ShTextView : AppCompatTextView {
    private var mHideTime = 700

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.shView,
            0, 0
        )

        try {
            mHideTime = a.getInteger(R.styleable.shView_hideTime, 500)
        } finally {
            a.recycle()
        }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun performClick(): Boolean {
        this.visibility = INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed(
            { visibility = VISIBLE },
            mHideTime.toLong()
        )
        return super.performClick()
    }
}