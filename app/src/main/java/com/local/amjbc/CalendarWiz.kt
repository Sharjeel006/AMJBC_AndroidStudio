package com.local.amjbc

import android.app.ProgressDialog
import android.os.Bundle
import android.view.*
import android.webkit.*
import androidx.fragment.app.Fragment

class CalendarWiz : Fragment() {

    var suggest_event: MenuItem? = null
    var webView: WebView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                             savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.calendar_wiz, container, false)
        setHasOptionsMenu(true)

        val pd = ProgressDialog.show(activity, "", "Loading...", true)

        webView = rootView.findViewById<View>(R.id.webview) as WebView

        webView!!.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(false)
            allowFileAccess = true
            allowContentAccess = true
            loadWithOverviewMode = false
            useWideViewPort = false
            builtInZoomControls = false
            displayZoomControls = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView!!.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: android.os.Message?
            ): Boolean {
                val newWebView = WebView(requireContext())
                newWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        val url = request?.url?.toString() ?: return false
                        webView?.loadUrl(url)
                        return true
                    }
                }
                val transport = resultMsg?.obj as? WebView.WebViewTransport
                transport?.webView = newWebView
                resultMsg?.sendToTarget()
                return true
            }
        }

        webView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                view?.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                if (pd != null && pd.isShowing) {
                    pd.dismiss()
                }
            }
        }

        webView!!.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP && webView!!.canGoBack()) {
                webView!!.goBack()
                return@OnKeyListener true
            }
            false
        })

        webView!!.loadUrl("https://sites.google.com/ahmadiyya.ca/vancouverimaratcalendar")

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.events_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        if (menu.findItem(R.id.suggest_event) != null) {
            menu.findItem(R.id.suggest_event).isVisible = false
        }
        if (menu.findItem(R.id.datepicker) != null) {
            menu.findItem(R.id.datepicker).isVisible = false
        }
        super.onPrepareOptionsMenu(menu)
    }
}