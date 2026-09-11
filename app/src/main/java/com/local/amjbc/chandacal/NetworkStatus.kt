package com.local.amjbc.chandacal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

class NetworkStatus : BroadcastReceiver() {

    var connectivityManager: ConnectivityManager? = null
    var wifiInfo: NetworkInfo? = null
    var mobileInfo: NetworkInfo? = null
    var connected = false

    override fun onReceive(context: Context, intent: Intent) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected) {
            //Send a broadcast to your service or activity that you have network
            //...
        } else {
            //Log.v("OS", "Network UNAVAILABLE");
        }
    }

    fun isOnline(con: Context): Boolean {
        try {
            connectivityManager = con.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager!!.activeNetworkInfo
            connected = networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
            return connected
        } catch (e: Exception) {
            //Log.v("connectivity", e.toString());
        }
        return connected
    }

    companion object {
        private val instance = NetworkStatus()
        var context: Context? = null

        fun getInstance(ctx: Context?): NetworkStatus {
            context = ctx
            return instance
        }
    }
}
