package com.local.amjbc;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

public class RequestEvent extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle("Event Request Form");
        View rootView = inflater.inflate(R.layout.calendar_wiz,container, false);
        setHasOptionsMenu(true);

        final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Loading...",true);

        WebView webView = (WebView)rootView.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                if(pd!=null && pd.isShowing())
                {
                    pd.dismiss();
                }
            }
        });
        webView.loadUrl("https://www.calendarwiz.com/cwsuggest/cwsuggestform.php?crd=baitur-rahman");
        return rootView;
    }
}
