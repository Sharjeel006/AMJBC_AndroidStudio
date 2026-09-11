package com.local.amjbc

import android.app.AlertDialog
import android.app.Fragment
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ListView
import android.widget.TextView
import com.local.amjbc.model.JSONParser
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class EventFragment : Fragment() {

    private var pDialog: ProgressDialog? = null
    private var error: TextView? = null

    var lv: ListView? = null
    var eventList: ArrayList<HashMap<String, String>>? = null
    var sp: SharedPreferences? = null

    var jParser = JSONParser()
    var products: JSONArray? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                             savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_event, container, false)

        lv = rootView.findViewById<View>(R.id.eventlist) as ListView
        error = rootView.findViewById<View>(R.id.error) as TextView

        sp = PreferenceManager.getDefaultSharedPreferences(activity)
        eventList = ArrayList()

        if (isNetworkAvailable(activity)) {
            LoadAllProducts().execute()
        } else {
            error!!.setTextColor(Color.parseColor("#FFFFFF"))
            error!!.textSize = 20f
            error!!.setPadding(10, 10, 10, 10)
            error!!.gravity = Gravity.CENTER
            error!!.setBackgroundColor(Color.RED)
            error!!.text = "No Internet"

            AlertDialog.Builder(activity)
                .setTitle("No internet")
                .setMessage("No internet connection detected, Cannot update")
                .setPositiveButton(android.R.string.yes) { dialog, which -> System.exit(0) }
                .setNegativeButton(android.R.string.no) { dialog, which -> }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }

        setHasOptionsMenu(true)
        return rootView

    }


    internal inner class LoadAllProducts : AsyncTask<String?, String?, String?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            pDialog = ProgressDialog(activity)
            pDialog!!.setMessage("Loading events. Please wait...")
            pDialog!!.setIndeterminate(false)
            pDialog!!.setCancelable(false)
            pDialog!!.show()
        }

        override fun doInBackground(vararg args: String?): String? {
            val params = ArrayList<Map.Entry<String, String>>()
            val json = jParser.nmakeHttpRequest(url_events, "GET", params)

            try {
                val success = json!!.getInt(TAG_SUCCESS)
                if (success == 1) {
                    products = json.getJSONArray(TAG_EVENTS)

                    for (i in 0 until products!!.length()) {
                        val c = products!!.getJSONObject(i)

                        val id = c.getString(TAG_ID)
                        val title = c.getString(TAG_TITLE)
                        val description = c.getString(TAG_DESC)
                        val date = c.getString(TAG_DATE)
                        val time = c.getString(TAG_TIME)
                        val venue = c.getString(TAG_VENUE)
                        val majlis = c.getString(TAG_MAJLIS)

                        val map = HashMap<String, String>()
                        map[TAG_ID] = id
                        map[TAG_TITLE] = title
                        map[TAG_DESC] = description
                        map[TAG_DATE] = date
                        map[TAG_TIME] = time
                        map[TAG_VENUE] = venue
                        map[TAG_MAJLIS] = majlis

                        eventList!!.add(map)
                    }
                } else {
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(file_url: String?) {
            pDialog!!.dismiss()
            activity.runOnUiThread {
                val edit = sp!!.edit()
                edit.putInt("eventsize", eventList!!.size)
                edit.apply()

                val adapter = EventsAdapter(activity, R.layout.events_list_item, eventList!!)
                lv!!.adapter = adapter
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    companion object {
        private const val url_events = "http://www.amj-bc.com/get_all_events.php"
        private const val TAG_SUCCESS = "success"
        private const val TAG_EVENTS = "events"
        private const val TAG_ID = "e_id"
        private const val TAG_TITLE = "e_title"
        private const val TAG_DESC = "e_description"
        private const val TAG_DATE = "e_date"
        private const val TAG_TIME = "e_time"
        private const val TAG_VENUE = "e_venue"
        private const val TAG_MAJLIS = "e_majlis"

        fun isNetworkAvailable(context: Context?): Boolean {
            var outcome = false
            if (context != null) {
                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfos = cm.allNetworkInfo
                for (tempNetworkInfo in networkInfos) {
                    if (tempNetworkInfo.isConnected) {
                        outcome = true
                        break
                    }
                }
            }
            return outcome
        }
    }
}
