package com.local.amjbc

import android.app.Fragment
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.local.amjbc.model.JSONParser
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AddEventFragment : Fragment() {

    var submit: Button? = null
    var dp: DatePicker? = null
    var ddd: String? = null
    var mmm: String? = null
    var venue: AutoCompleteTextView? = null
    var majlis: Spinner? = null
    var success: Int = 0
    var title: EditText? = null
    var descri: EditText? = null
    var tp: TimePicker? = null
    var bigdate: String? = null
    var bigtime: String? = null

    private var pDialog: ProgressDialog? = null
    private var jsonParser = JSONParser()
    private val venues = arrayOf("Baitur Rahman", "Surrey Namaz Center")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                             savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_addevent, container, false)

        submit = rootView.findViewById<View>(R.id.submit) as Button
        dp = rootView.findViewById<View>(R.id.datePicker1) as DatePicker
        venue = rootView.findViewById<View>(R.id.venuetext) as AutoCompleteTextView
        title = rootView.findViewById<View>(R.id.titletext) as EditText
        descri = rootView.findViewById<View>(R.id.descritext) as EditText
        majlis = rootView.findViewById<View>(R.id.majlistext) as Spinner
        tp = rootView.findViewById<View>(R.id.timePicker1) as TimePicker

        venue!!.setAdapter(ArrayAdapter(activity, android.R.layout.simple_list_item_1, venues))

        tp!!.clearFocus()

        submit!!.setOnClickListener {
            val day = dp!!.dayOfMonth
            val month = dp!!.month + 1
            val year = dp!!.year

            bigdate = "$year-$month-$day"

            updateTime(tp!!.currentHour, tp!!.currentMinute)

            if (title!!.text.toString() == "" || descri!!.text.toString() == "" || venue!!.text.toString() == "") {
                Toast.makeText(activity, "Fill the required fields", Toast.LENGTH_LONG).show()
            } else {
                try {
                    CreateNewEvent().execute()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        setHasOptionsMenu(true)
        return rootView

    }


    private fun updateTime(hours: Int, mins: Int) {
        var h = hours
        var timeSet = ""
        if (h > 12) {
            h -= 12
            timeSet = "PM"
        } else if (h == 0) {
            h += 12
            timeSet = "AM"
        } else if (h == 12) timeSet = "PM" else timeSet = "AM"

        val minutes = if (mins < 10) "0$mins" else mins.toString()

        bigtime = StringBuilder().append(h).append(':')
            .append(minutes).append(" ").append(timeSet).toString()

    }


    internal inner class CreateNewEvent : AsyncTask<String?, String?, String?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            pDialog = ProgressDialog(activity)
            pDialog!!.setMessage("Creating Event..")
            pDialog!!.setIndeterminate(false)
            pDialog!!.setCancelable(true)
            pDialog!!.show()
        }

        /**
         * Creating product
         */
        override fun doInBackground(vararg args: String?): String? {

            Looper.prepare()
            // Building Parameters
            val params = ArrayList<Map.Entry<String, String>>()

            params.add(AbstractMap.SimpleEntry("e_title", title!!.text.toString()))
            params.add(AbstractMap.SimpleEntry("e_description", descri!!.text.toString()))
            params.add(AbstractMap.SimpleEntry("e_date", bigdate!!))
            params.add(AbstractMap.SimpleEntry("e_time", bigtime!!))
            params.add(AbstractMap.SimpleEntry("e_venue", venue!!.text.toString()))
            params.add(AbstractMap.SimpleEntry("e_majlis", majlis!!.selectedItem.toString()))
            val json = jsonParser.nmakeHttpRequest(url_create_event, "POST", params)

            try {
                success = json!!.getInt(TAG_SUCCESS)
                if (success == 1) {
                } else {
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }

        /**
         * After completing background task Dismiss the progress dialog
         */
        override fun onPostExecute(file_url: String?) {
            // dismiss the dialog once done
            pDialog!!.dismiss()
            if (success == 1) {
                // successfully created product
                Toast.makeText(activity, "Event Created", Toast.LENGTH_LONG).show()
                activity.onBackPressed()
            } else {
                Toast.makeText(activity, "Failed to create event", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val url_create_event = "http://www.amj-bc.com/create_event.php"
        private const val TAG_SUCCESS = "success"
    }
}
