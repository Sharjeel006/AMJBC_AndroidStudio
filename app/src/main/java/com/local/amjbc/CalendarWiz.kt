package com.local.amjbc

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
            setSupportMultipleWindows(true)
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
                if (newProgress > 5) {
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
                        handleUrlLoading(url)
                        return true
                    }

                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        if (url == null) return false
                        handleUrlLoading(url)
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
                return handleUrlLoading(url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url == null) return false
                return handleUrlLoading(url)
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

    private fun handleUrlLoading(url: String): Boolean {
        if (url.startsWith("https://sites.google.com/")) {
            webView?.loadUrl(url)
            return true
        } else {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                webView?.loadUrl(url)
            }
            return true
        }
    }

    private fun injectFullWidthCss(view: WebView?) {
        val js = """
            (function() {
                try {
                    var enforceFullWidth = function() {
                        var style = document.getElementById('full-width-edge-style');
                        if (!style) {
                            style = document.createElement('style');
                            style.id = 'full-width-edge-style';
                            style.type = 'text/css';
                            style.innerHTML = '* { box-sizing: border-box !important; } html, body, #sites-canvas, .sites-canvas, .sites-page-main, div[role="main"], section, main, iframe, div, article { margin-left: 0 !important; margin-right: 0 !important; padding-left: 0 !important; padding-right: 0 !important; max-width: 100vw !important; width: 100% !important; }';
                            (document.head || document.documentElement).appendChild(style);
                        }
                        var elements = document.querySelectorAll('div, section, main, iframe, article, body');
                        for (var i = 0; i < elements.length; i++) {
                            var el = elements[i];
                            el.style.setProperty('margin-left', '0px', 'important');
                            el.style.setProperty('margin-right', '0px', 'important');
                            el.style.setProperty('padding-left', '0px', 'important');
                            el.style.setProperty('padding-right', '0px', 'important');
                            el.style.setProperty('max-width', '100vw', 'important');
                            el.style.setProperty('width', '100%', 'important');
                        }
                    };

                    enforceFullWidth();
                    if (!window.__fullWidthObserver) {
                        window.__fullWidthObserver = true;
                        setInterval(enforceFullWidth, 400);
                        if (window.MutationObserver) {
                            var observer = new MutationObserver(enforceFullWidth);
                            observer.observe(document.documentElement, { childList: true, subtree: true, attributes: true });
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