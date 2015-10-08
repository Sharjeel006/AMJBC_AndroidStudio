package com.local.amjbc;

import java.util.Iterator;
import java.util.Random;

import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

public class ParseReciever extends ParsePushBroadcastReceiver {
	
	private final String TAG = "Parse Notification";
	public String msg, title = "";
	public String msg2 = "";
	public static int numMessages = 0;
	Uri alarmSound;
	
	@Override
	public void onPushReceive(Context context, Intent intent) {
		
		Log.i(TAG, "PUSH RECIEVED !!");
		try {
//				String action = intent.getAction();
//				String channel = intent.getExtras().getString("com.parse.Channel");
				JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
				
				Iterator itr = json.keys();
				while (itr.hasNext()) {
						String key = (String)itr.next();
						Log.d(TAG, "key ::  " + json.getString(key));
						if(key.equals("alert")) {
							msg= json.getString(key);	
						}
						
						if(key.equals("title")) {
							title= json.getString(key);  
						}
				}
				msg2 = msg;
				generateNotification(context, title, json, msg);
		
		} catch (Exception e) {
			Log.d(TAG, "Parse Exception: " + e.getMessage());
		}
	}
	
	private void generateNotification(Context context, String title, JSONObject json, String contenttext) {
		Random random = new Random();
		int contentIntentRequestCode = random.nextInt();
		Intent i=new Intent("com.parse.push.intent.OPEN");
		i.putExtra("message", msg);
		PendingIntent pContentIntent = PendingIntent.getBroadcast(context, contentIntentRequestCode, i, 0x8000000);

		alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
	    NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_islam_2).setContentTitle(title).setContentText(msg).setNumber(++numMessages);

	    mBuilder.setContentIntent(pContentIntent);
	    mBuilder.setAutoCancel(true);
	    mBuilder.setSound(alarmSound);
	    mBuilder.setDefaults(-1);
	    mNotifM.notify(001, mBuilder.build());

	}

	@Override
	protected void onPushOpen(Context context, Intent intent) {
		 Log.e("Push", "Clicked");
	        Intent i = new Intent(context, MainActivity.class);
	        i.putExtras(intent.getExtras());
	        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        context.startActivity(i);
	}

}
