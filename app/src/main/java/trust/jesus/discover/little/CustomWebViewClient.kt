package trust.jesus.discover.little

// Source - https://stackoverflow.com/a/38484061
// Posted by Henry
// Retrieved 2025-12-19, License - CC BY-SA 3.0

import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class CustomWebViewClient : WebViewClient() {
    val gc: Globus = Globus.getAppContext() as Globus

/*
    @Suppress("DEPRECATION")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        val uri = Uri.parse(url)
        return handleUri(uri)
    }

    @TargetApi(Build.VERSION_CODES.N)

 */

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val uri = request.url
        return handleUri(uri)
    }

    private fun handleUri(uri: Uri): Boolean {
        //Log.i(TAG, "Uri = $uri")
        /* any condition
        *
        val host = uri.host
        val scheme = uri.scheme
        // Based on some condition you need to determine if you are going to load the url
        // in your web view itself or in a browser.
        // You can use `host` or `scheme` or any part of the `uri` to decide.
        return if () {
            // Returning false means that you are going to load this url in the webView itself
            false
        } else {
            // Returning true means that you need to handle what to do with the url
            // e.g. open web page in a Browser
            val intent = Intent(Intent.ACTION_VIEW, uri)
            gc.mainActivity!!.startActivity(intent)
            true
        }
        */
        val intent = Intent(Intent.ACTION_VIEW, uri)
        gc.mainActivity!!.startActivity(intent)
        return true
    }

    companion object {
        //private const val TAG = "CustomWebViewClient"
    }
}