package com.local.amjbc

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat

class PrayerReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("prayer_name") ?: return
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "prayer_reminders_v2" // Updated ID to ensure sound takes effect on newer Android versions
        
        val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.notification_sound)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val channel = NotificationChannel(channelId, "Prayer Reminders", NotificationManager.IMPORTANCE_HIGH).apply {
                setSound(soundUri, audioAttributes)
                description = "Notifications for prayer timings"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val isJumuah = prayerName.equals("Jumu'ah", ignoreCase = true)
        val title = if (isJumuah) "Friday Prayer Reminder" else "Prayer Reminder"
        val message = if (isJumuah) "Friday prayer is coming up in 1 hour." else "$prayerName is coming up in 15 minutes."

        val activityIntent = Intent(context, MainActivity2::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification_mosque)
            .setContentTitle(title)
            .setContentText(message)
            .setSound(soundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(getNotificationId(prayerName), builder.build())
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
