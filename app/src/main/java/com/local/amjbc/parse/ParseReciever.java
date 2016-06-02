package com.local.amjbc.parse;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.local.amjbc.MainActivity;
import com.local.amjbc.R;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class ParseReciever extends ParsePushBroadcastReceiver {
	
	private final String TAG = "Parse Notification";
    private Intent parseIntent;
    SharedPreferences pref;

    public ParseReciever() {
        super();
    }
	
	@Override
	public void onPushReceive(Context context, Intent intent) {

        if (intent == null)
            return;

        try {
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            Log.e(TAG, "Push received: " + json);
            parseIntent = intent;
            parsePushJson(context, json);
        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
	}

    private void parsePushJson(Context context, JSONObject json) {
        try {
            pref = context.getApplicationContext().getSharedPreferences("MyPref", 0);
            boolean isBackground = json.getBoolean("is_background");
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");

            if (!isBackground) {
                Intent resultIntent = new Intent(context, MainActivity.class);
                Log.e(TAG, "inside: " + title );
                showNotificationMessage(context, title, message, resultIntent);
            }
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("title", title);
            editor.commit();

        } catch (JSONException e) {
            Log.e(TAG, "Push message json exception: " + e.getMessage());
        }
    }


    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

        private void showNotificationMessage(Context context, String title, String message, Intent intent) {

            intent.putExtras(parseIntent.getExtras());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT );
            int icon = R.drawable.ic_islam_2;

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

            Notification notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                    .setContentText(message)
                    .build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(100, notification);
        }
}