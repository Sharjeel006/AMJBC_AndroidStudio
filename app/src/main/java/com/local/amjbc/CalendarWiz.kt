package com.local.amjbc

import android.app.ProgressDialog
import android.content.Intent
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
        webView!!.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (pd != null && pd.isShowing) {
                    pd.dismiss()
                }
            }
        }
        webView!!.loadUrl("https://www.calendarwiz.com/mobile.html?crd=baitur-rahman")

        val btnRequestBooking = rootView.findViewById<View>(R.id.btn_request_booking)
        btnRequestBooking?.setOnClickListener {
            val url = "https://script.google.com/macros/s/AKfycbwlbP-xme8Ae4KSzgFLjoOwSCgpZAE40LfFVY7t10jaCbigAMm4lkF7RRadhufwfP1Tqw/exec"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

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