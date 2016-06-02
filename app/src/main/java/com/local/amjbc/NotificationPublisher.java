package com.local.amjbc;

/**
 * Created by sharjeelhussain on 15-11-08.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        StaticWakeLock.lockOn(context);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        String namazname = intent.getStringExtra("namazname");
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        // ... do work...
        //show toast
        Log.i("TAG", "inside publisher");
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(namazname);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setSmallIcon(R.drawable.ic_launcher);

        notificationManager.notify(id, builder.build());

    }
}