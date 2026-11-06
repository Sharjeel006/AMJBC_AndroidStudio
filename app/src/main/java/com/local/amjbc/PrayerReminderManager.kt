package com.local.amjbc

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class PrayerReminderManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("prayer_reminders", Context.MODE_PRIVATE)
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setReminderEnabled(prayerName: String, enabled: Boolean) {
        prefs.edit().putBoolean("enabled_$prayerName", enabled).apply()
    }

    fun isReminderEnabled(prayerName: String): Boolean = prefs.getBoolean("enabled_$prayerName", false)

    fun scheduleReminder(prayerName: String, timeStr: String) {
        if (!isReminderEnabled(prayerName) || timeStr == "...") {
            cancelReminder(prayerName)
            return
        }

        val triggerTime = calculateTriggerTime(prayerName, timeStr) ?: return
        
        if (triggerTime <= System.currentTimeMillis()) {
            // If already passed for today, don't schedule.
            // When prayer times are refreshed, they usually contain today's times.
            return
        }

        // For Jumu'ah, ensure it's Friday
        if (prayerName.equals("Jumu'ah", ignoreCase = true)) {
            val cal = Calendar.getInstance()
            if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
                return
            }
        }

        val intent = Intent(context, PrayerReminderReceiver::class.java).apply {
            putExtra("prayer_name", prayerName)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getNotificationId(prayerName),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
            Log.d("PrayerReminder", "Scheduled $prayerName for ${Date(triggerTime)}")
        } catch (e: Exception) {
            Log.e("PrayerReminder", "Failed to schedule alarm", e)
        }
    }

    fun cancelReminder(prayerName: String) {
        val intent = Intent(context, PrayerReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getNotificationId(prayerName),
            intent,
            PendingIntent.FLAG_NO_CREATE or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
        Log.d("PrayerReminder", "Cancelled $prayerName")
    }

    private fun calculateTriggerTime(prayerName: String, timeStr: String): Long? {
        val sdf = SimpleDateFormat("h:mm a", Locale.US)
        try {
            val date = sdf.parse(timeStr) ?: return null
            val calendar = Calendar.getInstance()
            val now = Calendar.getInstance()
            
            calendar.time = date
            calendar.set(Calendar.YEAR, now.get(Calendar.YEAR))
            calendar.set(Calendar.MONTH, now.get(Calendar.MONTH))
            calendar.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))
            
            val offsetMinutes = if (prayerName.equals("Jumu'ah", ignoreCase = true)) -60 else -15
            calendar.add(Calendar.MINUTE, offsetMinutes)
            
            return calendar.timeInMillis
        } catch (e: Exception) {
            Log.e("PrayerReminder", "Error parsing time: $timeStr", e)
            return null
        }
    }

    private fun getNotificationId(prayerName: String): Int {
        return when (prayerName.lowercase()) {
            "fajr" -> 101
            "dhuhr" -> 102
            "asr" -> 103
            "maghrib" -> 104
            "isha" -> 105
            "jumu'ah" -> 106
            else -> 100
        }
    }
}
