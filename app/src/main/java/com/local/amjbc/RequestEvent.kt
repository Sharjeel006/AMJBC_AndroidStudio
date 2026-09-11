package com.local.amjbc

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment

class RequestEvent : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                             savedInstanceState: Bundle?): View? {

        activity?.title = "Event Request Form"
        val rootView = inflater.inflate(R.layout.calendar_wiz, container, false)
        setHasOptionsMenu(true)

        val pd = ProgressDialog.show(activity, "", "Loading...", true)

        val webView = rootView.findViewById<View>(R.id.webview) as WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (pd != null && pd.isShowing) {
                    pd.dismiss()
                }
            }
        }
        webView.loadUrl("https://www.calendarwiz.com/cwsuggest/cwsuggestform.php?crd=baitur-rahman")
        return rootView
    }
}
