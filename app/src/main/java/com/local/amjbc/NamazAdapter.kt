package com.local.amjbc

import android.app.Activity
import android.app.Notification
import android.content.Context
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat

class NamazAdapter(private val context: Context, private val resource: Int, private val namaz: List<String>, private val timings: List<String>) : ArrayAdapter<String>(context, resource, namaz) {

    private val reminderManager = PrayerReminderManager(context)

    override fun getView(position: Int, convert: View?, parent: ViewGroup): View {
        val row = convert ?: (context as Activity).layoutInflater.inflate(resource, parent, false)!!

        val namaztext = row.findViewById<View>(R.id.textView1) as? TextView
        val timingtext = row.findViewById<View>(R.id.textView2) as? TextView
        val prayerIcon = row.findViewById<ImageView>(R.id.prayer_icon)
        val reminderToggle = row.findViewById<SwitchCompat>(R.id.reminder_toggle)

        val prayerName = namaz[position]
        val prayerTime = timings[position]

        namaztext?.text = prayerName
        timingtext?.text = prayerTime

        reminderToggle?.setOnCheckedChangeListener(null)
        reminderToggle?.isChecked = reminderManager.isReminderEnabled(prayerName)
        reminderToggle?.setOnCheckedChangeListener { _, isChecked ->
            reminderManager.setReminderEnabled(prayerName, isChecked)
            if (isChecked) {
                reminderManager.scheduleReminder(prayerName, prayerTime)
            } else {
                reminderManager.cancelReminder(prayerName)
            }
        }

        // Clear any black tints to preserve original asset colors
        prayerIcon?.imageTintList = null

        when (namaz[position]) {
            "Fajr" -> {
                prayerIcon?.setImageResource(R.drawable.ic_sunrise)
            }
            "Dhuhr" -> {
                prayerIcon?.setImageResource(R.drawable.ic_sun)
            }
            "Asr" -> {
                prayerIcon?.setImageResource(R.drawable.ic_suncloud)
            }
            "Maghrib" -> {
                prayerIcon?.setImageResource(R.drawable.ic_sunset)
            }
            "Isha" -> {
                prayerIcon?.setImageResource(R.drawable.ic_moon)
            }
            else -> {
                prayerIcon?.setImageResource(R.drawable.ic_mosque)
            }
        }

        row.tag = namaz[position]

        return row
    }

    private fun getNotification(content: String): Notification {
        val mBuilder = Notification.Builder(getContext())

        mBuilder.setSmallIcon(R.drawable.ic_islam_2).setTicker("1").setWhen(0)
            .setAutoCancel(true)
            .setContentTitle("Title")
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_islam_2))
            .setContentText(content)

        return mBuilder.build()
    }

    private fun getNotification2(content: String): Notification {
        val builder = Notification.Builder(getContext())
        builder.setContentTitle("Scheduled Notification")
        builder.setContentText(content)
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        builder.setSmallIcon(R.drawable.ic_launcher)
        return builder.build()
    }
}
