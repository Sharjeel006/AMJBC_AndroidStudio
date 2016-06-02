package com.local.amjbc;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NamazAdapter extends ArrayAdapter<String> {

	private List<String> namaz;
	private List<String> timings;
	private Context context;
	private int resource;
	
	public NamazAdapter(Context context, int resId, List<String> namaz , List<String> timings) {
		super(context, resId, namaz);
		this.context = context;
		this.resource = resId;
		this.namaz = namaz;
		this.timings = timings;	
	}

	@Override
	public View getView(final int position, View convert, ViewGroup parent) {

        View row = convert;

        TextView namaztext = null, timingtext = null;
        ImageView alarm;

        Typeface tf3 = Typeface.createFromAsset(context.getAssets(), "Quicksand-Bold.otf");

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);
            row.setTag(namaz.get(position));
        }

        namaztext = (TextView) row.findViewById(R.id.textView1);
        timingtext = (TextView) row.findViewById(R.id.textView2);
        alarm = (ImageView) row.findViewById(R.id.alarm);

        namaztext.setTypeface(tf3);
        timingtext.setTypeface(tf3);

        namaztext.setText(namaz.get(position));
        timingtext.setText(timings.get(position));

        return row;
    }

    private Notification getNotification(String content) {
        Notification.Builder mBuilder = new Notification.Builder(getContext());

        mBuilder.setSmallIcon(R.drawable.ic_islam_2).setTicker("1").setWhen(0)
                .setAutoCancel(true)
                .setContentTitle("Title")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_islam_2))
                .setContentText(content);

        return mBuilder.build();
    }

    private Notification getNotification2(String content) {
        Notification.Builder builder = new Notification.Builder(getContext());
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setSmallIcon(R.drawable.ic_launcher);
        return builder.build();
    }


}
