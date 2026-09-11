package com.local.amjbc

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d("TOKEN", s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("OnMessage", "Received")
        super.onMessageReceived(remoteMessage)
        Log.d("Firebase", "From " + remoteMessage.from)
        Log.d("Firebase", "Body " + remoteMessage.notification?.body)

        val aa = remoteMessage.toString()
        val intent = Intent(this@MyFirebaseMessagingService, MainActivity2::class.java)
        intent.putExtra("msg", aa)
        sendNotification(remoteMessage)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_islam_4)
        val mBuilder = NotificationCompat.Builder(this)
            .setContentText(remoteMessage.notification?.body)
            .setContentTitle(remoteMessage.notification?.title)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setLargeIcon(largeIcon)
            .setSmallIcon(R.drawable.ic_islam_4)
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, MainActivity2::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_islam_4)

        val notificationBuilder = NotificationCompat.Builder(this)
            .setContentText(remoteMessage.notification?.body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setLargeIcon(largeIcon)
            .setSmallIcon(R.drawable.ic_islam_4)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}
