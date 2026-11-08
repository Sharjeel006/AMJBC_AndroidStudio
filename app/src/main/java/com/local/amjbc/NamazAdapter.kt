package com.local.amjbc

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.card.MaterialCardView

class NamazAdapter(
    private val context: Context,
    private val resource: Int,
    private val namaz: List<String>,
    private val timings: List<String>
) : ArrayAdapter<String>(context, resource, namaz) {

    private val reminderManager = PrayerReminderManager(context)
    var activePrayerName: String = ""

    fun setActivePrayer(prayerName: String) {
        activePrayerName = prayerName
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convert: View?, parent: ViewGroup): View {
        val row = convert ?: (context as Activity).layoutInflater.inflate(resource, parent, false)!!

        val cardRoot = row.findViewById<MaterialCardView>(R.id.card_item_root)
        val namaztext = row.findViewById<TextView>(R.id.textView1)
        val alarmSubtitle = row.findViewById<TextView>(R.id.tv_alarm_subtitle)
        val timingtext = row.findViewById<TextView>(R.id.textView2)
        val prayerIcon = row.findViewById<ImageView>(R.id.prayer_icon)
        val reminderToggle = row.findViewById<SwitchCompat>(R.id.reminder_toggle)

        val prayerName = namaz[position]
        val prayerTime = timings[position]

        namaztext?.text = prayerName
        timingtext?.text = prayerTime

        val isEnabled = reminderManager.isReminderEnabled(prayerName)
        alarmSubtitle?.text = if (isEnabled) "Alarm 15 min before" else "Alarm off"

        reminderToggle?.setOnCheckedChangeListener(null)
        reminderToggle?.isChecked = isEnabled
        reminderToggle?.setOnCheckedChangeListener { _, isChecked ->
            reminderManager.setReminderEnabled(prayerName, isChecked)
            alarmSubtitle?.text = if (isChecked) "Alarm 15 min before" else "Alarm off"
            if (isChecked) {
                reminderManager.scheduleReminder(prayerName, prayerTime)
            } else {
                reminderManager.cancelReminder(prayerName)
            }
        }

        // Highlight active prayer card with white background and elevation
        if (prayerName.equals(activePrayerName, ignoreCase = true)) {
            cardRoot?.setCardBackgroundColor(Color.WHITE)
            cardRoot?.cardElevation = 4f
        } else {
            cardRoot?.setCardBackgroundColor(Color.TRANSPARENT)
            cardRoot?.cardElevation = 0f
        }

        when (prayerName) {
            "Fajr" -> prayerIcon?.setImageResource(R.drawable.ic_sehr_sunrise)
            "Dhuhr" -> prayerIcon?.setImageResource(R.drawable.ic_sun)
            "Asr" -> prayerIcon?.setImageResource(R.drawable.ic_suncloud)
            "Maghrib" -> prayerIcon?.setImageResource(R.drawable.ic_sunset)
            "Isha" -> prayerIcon?.setImageResource(R.drawable.ic_moon)
            else -> prayerIcon?.setImageResource(R.drawable.ic_mosque)
        }

        row.tag = prayerName
        return row
    }
}