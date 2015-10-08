package com.local.amjbc.chandacal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatus extends BroadcastReceiver {

    private static NetworkStatus instance = new NetworkStatus();
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    static Context context;
    boolean connected = false;

    public static NetworkStatus getInstance(Context ctx) {
      context = ctx;
      return instance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            //Send a broadcast to your service or activity that you have network
            //...
        } else {
            //Log.v("OS", "Network UNAVAILABLE");
        }
    }
    
    public Boolean isOnline(Context con) {
    	try {
    		connectivityManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
    		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    		connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    		return connected;
		} catch (Exception e) {
			//Log.v("connectivity", e.toString());
		}
	return connected;
    }
}