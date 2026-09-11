package com.local.amjbc.chandacal

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient

class SyncService : Service() {

    var sp: SharedPreferences? = null

    override fun onCreate() {
        super.onCreate()
        sp = PreferenceManager.getDefaultSharedPreferences(baseContext)

        val server = AccessServer()
        server.execute()

    }

    private inner class AccessServer : AsyncTask<String?, Int?, Int?>() {
        override fun doInBackground(vararg params: String?): Int? {

            if (NetworkStatus.getInstance(this@SyncService).isOnline(this@SyncService)) {
                try {
                    val searchUrl = "http://deltatechglobal.ca/ami-capp/?"

                    val client = DefaultHttpClient()
                    val get = HttpGet(searchUrl + getDataToSync())

                    val responseHandler: ResponseHandler<String> = BasicResponseHandler()

                    var responseBody: String? = null
                    responseBody = client.execute(get, responseHandler)

                    Log.d("service", responseBody!!)

                    if (responseBody == null) {
                        val edit = sp!!.edit()
                        edit.putString("response", "false")
                        edit.apply()
                    } else if (responseBody.contains("ok")) {
                        val edit = sp!!.edit()
                        edit.putString("response", "true")
                        edit.apply()

                    } else {
                        val edit = sp!!.edit()
                        edit.putString("response", "false")
                        edit.apply()

                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                val i = Intent("broadcast")
                sendBroadcast(i)

                stopSelf()
            } else {
                stopSelf()
            }

            return null
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun getDataToSync(): String {
        return ("aam=" + sp!!.getString("main", "0") +
                "&j.salana=" + sp!!.getString("jsalana", "0") +
                "&org=" + sp!!.getString("orgz", "0") +
                "&tj=" + sp!!.getInt("tj", 0) +
                "&wj=" + sp!!.getInt("wj", 0) +
                "&ef=" + sp!!.getInt("ef", 0) +
                "&fit=" + sp!!.getInt("fit", 0) +
                "&sad=" + sp!!.getInt("sad", 0))

    }

}
