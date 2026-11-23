package com.local.amjbc

import android.app.ProgressDialog
import android.graphics.Bitmap
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
            textZoom = 100
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress > 10) {
                    injectFullWidthCss(view)
                }
                super.onProgressChanged(view, newProgress)
            }

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
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                injectFullWidthCss(view)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                view?.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                injectFullWidthCss(view)
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

    private fun injectFullWidthCss(view: WebView?) {
        val js = """
            (function() {
                try {
                    var style = document.getElementById('full-width-edge-style');
                    if (!style) {
                        style = document.createElement('style');
                        style.id = 'full-width-edge-style';
                        style.type = 'text/css';
                        style.innerHTML = 'html, body, #sites-canvas, .sites-canvas, .sites-page-main, div[role="main"], section, main, .Xp439d, .o5R36e, .R61A4, .Sbh32e, .sites-layout-tile, .U39P4e, .y1eMld { margin: 0 !important; padding: 0 !important; max-width: 100vw !important; width: 100% !important; box-sizing: border-box !important; }';
                        (document.head || document.documentElement).appendChild(style);
                    }
                } catch(e) {}
            })();
        """.trimIndent()

        view?.evaluateJavascript(js, null)
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