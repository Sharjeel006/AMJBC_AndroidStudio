package com.local.amjbc;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class RamazanFragment extends Fragment {
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	
		View rootView = inflater.inflate(R.layout.fragment_ramazan2,container, false);
		setHasOptionsMenu(true);

		final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Loading...",true);

		WebView webView = (WebView)rootView.findViewById(R.id.webview2);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				if(pd!=null && pd.isShowing())
				{
					pd.dismiss();
				}
			}
		});
		webView.loadUrl("http://amjbccom.ipage.com/amjbc/show_images.php");
		webView.setDownloadListener(new DownloadListener()
		{
			@Override
			public void onDownloadStart(String url, String userAgent,
										String contentDisposition, String mimeType,
										long contentLength) {

				if(checkpermission2()) {
					DownloadManager.Request request = new DownloadManager.Request(
							Uri.parse(url));
					request.setMimeType(mimeType);
					String cookies = CookieManager.getInstance().getCookie(url);
					request.addRequestHeader("cookie", cookies);
					request.addRequestHeader("User-Agent", userAgent);
					request.setDescription("Downloading File...");
					request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
					request.allowScanningByMediaScanner();
					request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
					request.setDestinationInExternalPublicDir(
							Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
									url, contentDisposition, mimeType));
					DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
					dm.enqueue(request);
					Toast.makeText(getActivity(), "Downloading File", Toast.LENGTH_LONG).show();
				} else
				{
					return;
				}
			}});
		return rootView;
	}


	public  boolean checkpermission2()
	{
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
					== PackageManager.PERMISSION_DENIED) {
				Log.d("permission", "permission denied to WRITE_EXTERNAL_STORAGE - requesting it");
				String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
				requestPermissions(permissions, 1);
				return false;
			}
			else {
				return true;
			}
		}
		else {
			return true;
		}
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

    }




}
