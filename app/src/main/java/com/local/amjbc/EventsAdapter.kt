package com.local.amjbc

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.provider.CalendarContract.Attendees
import android.provider.CalendarContract.Events
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EventsAdapter(private val context: Context, private val resource: Int, private val eventList: ArrayList<HashMap<String, String>>) : ArrayAdapter<HashMap<String, String>>(context, resource, eventList) {

    var animation: Animation? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var event = convertView

        val id: TextView?
        val title: TextView?
        val desc: TextView?
        val date: TextView?
        val time: TextView?
        val venue: TextView?
        val majlis: TextView?
        val addToCalendar: ImageView?

        val hm = eventList[position]

        if (event == null) {
            val inflater = (context as Activity).layoutInflater
            event = inflater.inflate(resource, parent, false)
            event!!.tag = eventList[position]
        }

        id = event!!.findViewById<View>(R.id.eid) as TextView
        title = event.findViewById<View>(R.id.etitle) as TextView
        desc = event.findViewById<View>(R.id.edesc) as TextView
        date = event.findViewById<View>(R.id.edate) as TextView
        time = event.findViewById<View>(R.id.etiming) as TextView
        venue = event.findViewById<View>(R.id.evenue) as TextView
        majlis = event.findViewById<View>(R.id.emajlis) as TextView
        addToCalendar = event.findViewById<View>(R.id.addtocalendar) as ImageView

        desc.isSelected = true

        val inputTimeStamp = hm[TAG_DATE]

        val inputFormat = "yyyy-MM-dd"
        val outputFormat = "EEE MMM dd, yyyy"
        var dddd = ""

        try {
            dddd = TimeStampConverter(inputFormat, inputTimeStamp, outputFormat)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        id.text = hm[TAG_ID]
        title.text = hm[TAG_TITLE]
        desc.text = hm[TAG_DESC]
        date.text = dddd
        time.text = hm[TAG_TIME]
        venue.text = hm[TAG_VENUE]
        majlis.text = hm[TAG_MAJLIS]

        addToCalendar.setTag(R.id.key_1, dddd)
        addToCalendar.setTag(R.id.key_2, hm[TAG_TITLE])
        addToCalendar.setTag(R.id.key_3, hm[TAG_MAJLIS])
        addToCalendar.setTag(R.id.key_4, hm[TAG_DESC])



        try {
            animation = AnimationUtils.loadAnimation(context, R.anim.pushleftin)
            animation!!.duration = 500
            event.startAnimation(animation)
            animation = null
        } catch (ex: Exception) {

        }

        addToCalendar.setOnClickListener { v ->
            val timeZone = TimeZone.getDefault()
            val strThatDay = v.getTag(R.id.key_1).toString()
            val formatter = SimpleDateFormat("EEE MMM dd, yyyy")
            var d: Date? = null
            try {
                d = formatter.parse(strThatDay) //catch exception
            } catch (e: Exception) {

            }

            val cal = Calendar.getInstance()
            cal.time = d
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val calID: Long = 1
            var startMillis: Long = 0
            var endMillis: Long = 0
            val beginTime = Calendar.getInstance()
            beginTime.set(2014, month, day)
            startMillis = beginTime.timeInMillis
            val endTime = Calendar.getInstance()
            endTime.set(2014, month, day)
            endMillis = endTime.timeInMillis

            val cr = getContext().contentResolver
            val values = ContentValues()
            values.put(Events.DTSTART, startMillis)
            values.put(Events.DTEND, endMillis)
            values.put(Events.TITLE, v.getTag(R.id.key_2).toString())
            values.put(Events.DESCRIPTION, v.getTag(R.id.key_4).toString())
            values.put(Events.CALENDAR_ID, calID)
            values.put(Events.EVENT_TIMEZONE, timeZone.id)
            val uri = cr.insert(Events.CONTENT_URI, values)

            // get the event ID that is the last element in the Uri
            val eventID = uri!!.lastPathSegment!!.toLong()

            val values1 = ContentValues()
            values1.put(Attendees.ATTENDEE_NAME, v.getTag(R.id.key_3).toString())
            values1.put(Attendees.ATTENDEE_EMAIL, "trevor@example.com")
            values1.put(Attendees.ATTENDEE_RELATIONSHIP, Attendees.RELATIONSHIP_ATTENDEE)
            values1.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_OPTIONAL)
            values1.put(Attendees.ATTENDEE_STATUS, Attendees.ATTENDEE_STATUS_INVITED)
            values1.put(Attendees.EVENT_ID, eventID)
            val uri2 = cr.insert(Attendees.CONTENT_URI, values1)

            //Toast.makeText(getContext(), "Event Added to your Calendar ", Toast.LENGTH_LONG).show();

            val cdd = CustomDialogClass(getContext())
            cdd.show()
            cdd.setCancelable(true)
            cdd.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        return event
    }

    companion object {
        private const val TAG_SUCCESS = "success"
        private const val TAG_EVENTS = "events"
        private const val TAG_ID = "e_id"
        private const val TAG_TITLE = "e_title"
        private const val TAG_DESC = "e_description"
        private const val TAG_DATE = "e_date"
        private const val TAG_TIME = "e_time"
        private const val TAG_VENUE = "e_venue"
        private const val TAG_MAJLIS = "e_majlis"

        @Throws(ParseException::class)
        private fun TimeStampConverter(inputFormat: String,
                                       inputTimeStamp: String?, outputFormat: String): String {
            return SimpleDateFormat(outputFormat).format(SimpleDateFormat(
                inputFormat).parse(inputTimeStamp))
        }
    }
}
