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
        webView!!.settings.javaScriptEnabled = true
        webView!!.settings.domStorageEnabled = true

        webView!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress > 15) {
                    injectHideBannerJs(view)
                }
                super.onProgressChanged(view, newProgress)
            }
        }

        webView!!.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                injectHideBannerJs(view)
            }

            override fun onPageFinished(view: WebView, url: String) {
                injectHideBannerJs(view)
                if (pd != null && pd.isShowing) {
                    pd.dismiss()
                }
            }
        }
        webView!!.loadUrl("https://script.google.com/macros/s/AKfycbwlbP-xme8Ae4KSzgFLjoOwSCgpZAE40LfFVY7t10jaCbigAMm4lkF7RRadhufwfP1Tqw/exec?page=Calendar")

        return rootView
    }

    private fun injectHideBannerJs(view: WebView?) {
        val js = """
            (function() {
                var hideBanner = function() {
                    var style = document.getElementById('hide-gas-banner-style');
                    if (!style) {
                        style = document.createElement('style');
                        style.id = 'hide-gas-banner-style';
                        style.type = 'text/css';
                        style.innerHTML = '.script-application-sidebar-banner, .apps-script-notice, div.script-application-banner, div[role="alert"] { display: none !important; visibility: hidden !important; height: 0 !important; }';
                        (document.head || document.documentElement).appendChild(style);
                    }
                    var elements = document.querySelectorAll('div, td, table, header, section, tr');
                    for (var i = 0; i < elements.length; i++) {
                        var el = elements[i];
                        if (el.innerText && (el.innerText.indexOf('created by a Google Apps Script user') !== -1 || el.innerText.indexOf('Report abuse') !== -1)) {
                            if (el.children.length < 5) {
                                el.style.display = 'none';
                                el.style.height = '0px';
                                el.style.visibility = 'hidden';
                            }
                        }
                    }
                };
                hideBanner();
                setTimeout(hideBanner, 200);
                setTimeout(hideBanner, 500);
                setTimeout(hideBanner, 1000);
                setTimeout(hideBanner, 2000);
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.suggest_event -> {
                val url = "https://script.google.com/macros/s/AKfycbwlbP-xme8Ae4KSzgFLjoOwSCgpZAE40LfFVY7t10jaCbigAMm4lkF7RRadhufwfP1Tqw/exec"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}