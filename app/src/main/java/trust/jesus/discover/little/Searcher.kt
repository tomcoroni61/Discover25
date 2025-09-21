package trust.jesus.discover.little

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class Searcher(
    sv: ScrollView,
    private val textField: TextView,
    private val searchEdi: EditText
) : View.OnKeyListener {
    var searchword: String = "nIxi"

    private var logText: String? = null
    var foundIdx: Int = 0
    var lineNumber: Int = 0

    //private Global_Class gc;
    private val ctx: Context?
    private val notySV: ScrollView
    var prevpressed: Boolean = false
    var nextpressed: Boolean = false
    var keyEnter: Boolean = false

    override fun onKey(view: View?, keyCode: Int, event: KeyEvent): Boolean {
        //EditText myEditText = (EditText) view;

        if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE || event.action == KeyEvent.ACTION_DOWN &&
            event.keyCode == KeyEvent.KEYCODE_ENTER
        ) {
            if (!event.isShiftPressed) {
                searchNext(true)
                nextpressed = true
                keyEnter = true
                return true
            }
        }
        return false
    }

    fun searchBack(checkhighlightString: Boolean) {
        if (checkhighlightString && highlightallFoundStrings(
                searchEdi.text.toString().uppercase(
                    Locale.getDefault()
                )
            )
        ) {
            Handler(Looper.getMainLooper()).postDelayed({
                // btVor.performClick(); braucht "Pause" nach markieren, damit getLayout geht
                foundIdx = logText!!.lastIndexOf(searchword)
                searchBack(false)
            }, 100)
            return
        }
        if (nextpressed) {
            nextpressed = false
            foundIdx = logText!!.lastIndexOf(searchword, foundIdx - 1)
            if (foundIdx > 0) foundIdx = logText!!.lastIndexOf(searchword, foundIdx - 1)
            if (foundIdx < 1) foundIdx = logText!!.lastIndexOf(searchword)
            //btBack.setText("<")
        }
        if (foundIdx < 1) return
        scrolltoIdx()
        //btVor.setText(lineNumber.toString())
        foundIdx = logText!!.lastIndexOf(searchword, foundIdx - 1)
        if (foundIdx < 1) foundIdx = logText!!.lastIndexOf(searchword)
    }

    fun searchNext(checkhighlightString: Boolean) {
        //gc.HS().doLog("---------- searchNext start ----------");
        if (checkhighlightString && highlightallFoundStrings(
                searchEdi.text.toString().uppercase(
                    Locale.getDefault()
                )
            )
        ) {
            Handler(Looper.getMainLooper()).postDelayed({
                // btVor.performClick(); braucht "Pause" nach markieren, damit getLayout geht
                searchNext(false)
            }, 120)
            return
        }
        if (prevpressed) {
            prevpressed = false
            foundIdx = logText!!.indexOf(searchword, foundIdx + searchword.length)
            if (foundIdx > 0) foundIdx = logText!!.indexOf(searchword, foundIdx + searchword.length)
            if (foundIdx < 1) foundIdx = logText!!.indexOf(searchword)
            //btVor.setText(">")
        }
        //1. foundIdx von highlightallFoundStrings
        if (foundIdx < 1) return
        scrolltoIdx() //= Timhandl.postDelayed(rehighlightFoundWort, 111);
        //btBack.setText(lineNumber.toString())
        foundIdx = logText!!.indexOf(searchword, foundIdx + searchword.length)
        if (foundIdx < 1) foundIdx = logText!!.indexOf(searchword)
    }

    fun scrollToIndex(idx: Int): Boolean {
        try {
            val lt = textField.layout
            if (lt == null) return false
            lineNumber = lt.getLineForOffset(idx)
            if (lineNumber < 3) return false
            //highlightFoundWort(foundIdx);
            lineNumber = lineNumber - 2
            val scrollY = lt.getLineTop(lineNumber)
            if (scrollY > 3) notySV.scrollTo(0, scrollY)
            return true
        } catch (e: Exception) {
            Toast.makeText(ctx, "Crash by Searcher: " + e.message, Toast.LENGTH_LONG).show()
            //gc.HS().internLog("Crash on sturtup: " + chrashCnt + " msg: " + e.getMessage());
        }
        return false
    }

    private fun scrolltoIdx() {
        if (scrollToIndex(foundIdx)) highlightFoundWort(foundIdx)
    }

    private var lastfndIdx = -1

    init {
        searchEdi.setOnKeyListener(this)
        notySV = sv
        ctx = Globus.getAppContext()
        //logSV=sv; , ScrollView sv
    }

    private fun highlightFoundWort(fndIdx: Int) {
        val spannableString = SpannableString(textField.text)
        //BackgroundColorSpan[] backgroundSpans = spannableString.getSpans(0, spannableString.length(), BackgroundColorSpan.class);
        if (lastfndIdx > -1) spannableString.setSpan(
            BackgroundColorSpan(Color.LTGRAY),
            lastfndIdx,
            lastfndIdx + searchword.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        lastfndIdx = fndIdx
        spannableString.setSpan(
            BackgroundColorSpan(Color.GREEN),
            fndIdx,
            fndIdx + searchword.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textField.text = spannableString
    }

    /*

    private void setColor(TextView view, String fulltext, String subtext, int color) {
        view.setText(fulltext, TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable) view.getText();
        int i = fulltext.indexOf(subtext);
        str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

 */
    private fun highlightallFoundStrings(input: String): Boolean { //by https://stackoverflow.com/questions/38509419/highlight-text-inside-a-textview
        if (input == searchword) return false
        searchword = input
        foundIdx = 0
        lastfndIdx = -1
        logText = textField.text.toString().uppercase(Locale.getDefault())
        val spannableString = SpannableString(textField.text)
        //Get the previous spans and remove them
        val backgroundSpans = spannableString.getSpans<BackgroundColorSpan?>(
            0,
            spannableString.length,
            BackgroundColorSpan::class.java
        )

        for (span in backgroundSpans) {
            spannableString.removeSpan(span)
        }

        //Search for all occurrences of the keyword in the string
        var indexOfKeyword =
            spannableString.toString().uppercase(Locale.getDefault()).indexOf(input)
        if (indexOfKeyword > 0) foundIdx = indexOfKeyword

        while (indexOfKeyword > 0) {
            //Create a background color span on the keyword Color.BLUE=zu Blau ne: LTGRAY
            spannableString.setSpan(
                BackgroundColorSpan(Color.LTGRAY),
                indexOfKeyword,
                indexOfKeyword + input.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            //Get the next index of the keyword
            indexOfKeyword = spannableString.toString().uppercase(Locale.getDefault())
                .indexOf(input, indexOfKeyword + input.length)
        }

        //Set the final text on TextView
        textField.text = spannableString

        return true
    }
}
