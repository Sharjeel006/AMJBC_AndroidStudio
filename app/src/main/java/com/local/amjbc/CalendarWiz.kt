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
            loadWithOverviewMode = true
            useWideViewPort = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress > 50) {
                    injectHideBannerJs(view)
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
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                view?.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                injectHideBannerJs(view)
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

        webView!!.loadUrl("https://script.google.com/macros/s/AKfycbwlbP-xme8Ae4KSzgFLjoOwSCgpZAE40LfFVY7t10jaCbigAMm4lkF7RRadhufwfP1Tqw/exec?page=Calendar")

        return rootView
    }

    private fun injectHideBannerJs(view: WebView?) {
        val js = """
            (function() {
                try {
                    var style = document.getElementById('hide-gas-banner-style');
                    if (!style) {
                        style = document.createElement('style');
                        style.id = 'hide-gas-banner-style';
                        style.type = 'text/css';
                        style.innerHTML = '.script-application-sidebar-banner, .apps-script-notice, div.script-application-banner, table.script-application-banner { display: none !important; visibility: hidden !important; height: 0 !important; }';
                        (document.head || document.documentElement).appendChild(style);
                    }
                    var links = document.getElementsByTagName('a');
                    for (var i = 0; i < links.length; i++) {
                        var a = links[i];
                        var text = a.innerText || a.textContent || '';
                        var href = a.href || '';
                        if (href.indexOf('ReportAbuse') !== -1 || href.indexOf('report-abuse') !== -1 || text.indexOf('Report abuse') !== -1) {
                            var parent = a.parentElement;
                            while (parent && parent.tagName !== 'BODY' && parent.tagName !== 'HTML') {
                                if (parent.offsetHeight > 0 && parent.offsetHeight < 120) {
                                    parent.style.display = 'none';
                                    parent.style.visibility = 'hidden';
                                    parent.style.height = '0px';
                                    break;
                                }
                                parent = parent.parentElement;
                            }
                        }
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