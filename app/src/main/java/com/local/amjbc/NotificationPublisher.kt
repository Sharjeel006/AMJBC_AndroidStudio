package com.local.amjbc

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log

/**
 * Created by sharjeelhussain on 15-11-08.
 */
class NotificationPublisher : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        StaticWakeLock.lockOn(context)

        val notification = intent.getParcelableExtra<Notification>(NOTIFICATION)
        val namazname = intent.getStringExtra("namazname")
        val id = intent.getIntExtra(NOTIFICATION_ID, 0)
        // ... do work...
        //show toast
        Log.i("TAG", "inside publisher")
        val builder = Notification.Builder(context)
        builder.setContentTitle("Scheduled Notification")
        builder.setContentText(namazname)
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        builder.setSmallIcon(R.drawable.ic_launcher)

        notificationManager.notify(id, builder.build())

    }

    companion object {
        var NOTIFICATION_ID = "notification-id"
        var NOTIFICATION = "notification"
    }
}
