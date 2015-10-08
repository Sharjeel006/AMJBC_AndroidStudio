package com.local.amjbc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Events;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EventsAdapter extends ArrayAdapter<HashMap<String, String>> {

	private ArrayList<HashMap<String, String>> eventList;
	
	private Context context;
	private int resource;
	Animation animation;
	
	   private static final String TAG_SUCCESS = "success";
	   private static final String TAG_EVENTS = "events";
	   private static final String TAG_ID = "e_id";
	   private static final String TAG_TITLE = "e_title";
	   private static final String TAG_DESC = "e_description";
	   private static final String TAG_DATE = "e_date";
	   private static final String TAG_TIME = "e_time";
	   private static final String TAG_VENUE = "e_venue";
	   private static final String TAG_MAJLIS = "e_majlis";
	
	public EventsAdapter(Context context, int resId, ArrayList<HashMap<String, String>> eventList)
	{
		super(context, resId, eventList);
		this.context = context;
		this.resource = resId;
		this.eventList = eventList;
	
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View event = convertView;
		
		TextView id, title, desc, date, time, venue, majlis;
		ImageView addToCalendar;
		
		HashMap<String, String> hm = eventList.get(position);
		
		if( event == null )
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			event = inflater.inflate( resource , parent, false  );
			event.setTag(eventList.get(position));
		}
		
		id = (TextView) event.findViewById( R.id.eid);
		title = (TextView) event.findViewById( R.id.etitle);
		desc = (TextView) event.findViewById( R.id.edesc);
		date = (TextView)event.findViewById(R.id.edate);
		time = (TextView)event.findViewById(R.id.etiming);
		venue = (TextView)event.findViewById(R.id.evenue);
		majlis = (TextView)event.findViewById(R.id.emajlis);
		addToCalendar = (ImageView)event.findViewById(R.id.addtocalendar);
		
		desc.setSelected(true);
		
		 String inputTimeStamp = hm.get(TAG_DATE);

	        final String inputFormat = "yyyy-MM-dd";
	        final String outputFormat = "EEE MMM dd, yyyy";
	        String dddd = "";
	        
	        try {
			dddd = TimeStampConverter(inputFormat, inputTimeStamp, outputFormat);
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        
		id.setText(hm.get(TAG_ID).toString());
		title.setText(hm.get(TAG_TITLE).toString());
		desc.setText(hm.get(TAG_DESC).toString());
		date.setText(dddd);
		time.setText(hm.get(TAG_TIME).toString());
		venue.setText(hm.get(TAG_VENUE).toString());
		majlis.setText(hm.get(TAG_MAJLIS).toString());
		
		addToCalendar.setTag(R.id.key_1, dddd);
		addToCalendar.setTag(R.id.key_2, hm.get(TAG_TITLE).toString());
		addToCalendar.setTag(R.id.key_3, hm.get(TAG_MAJLIS).toString());
		addToCalendar.setTag(R.id.key_4, hm.get(TAG_DESC).toString());
		
		
		
		try{
		animation = AnimationUtils.loadAnimation(context, R.anim.pushleftin);
		animation.setDuration(500);
		   event.startAnimation(animation);
		   animation = null;
		}
		catch(Exception ex)
		{
			
		}
		
		addToCalendar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TimeZone timeZone = TimeZone.getDefault();
				String strThatDay = v.getTag(R.id.key_1).toString();
				  SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd, yyyy");
				  Date d = null;
				  try {
				   d = formatter.parse(strThatDay);//catch exception
				  } catch (Exception e) {
				   
				  }

				  Calendar cal = Calendar.getInstance();
				    cal.setTime(d);
				    int year = cal.get(Calendar.YEAR);
				    int month = cal.get(Calendar.MONTH);
				    int day = cal.get(Calendar.DAY_OF_MONTH);
				  
				 long calID = 1;
					long startMillis = 0; 
					long endMillis = 0;     
					Calendar beginTime = Calendar.getInstance();
					beginTime.set(2014, month, day);
					startMillis = beginTime.getTimeInMillis();
					Calendar endTime = Calendar.getInstance();
					endTime.set(2014, month , day);
					endMillis = endTime.getTimeInMillis();

					ContentResolver cr = getContext().getContentResolver();
					ContentValues values = new ContentValues();
					values.put(Events.DTSTART, startMillis);
					values.put(Events.DTEND, endMillis);
					values.put(Events.TITLE,  v.getTag(R.id.key_2).toString());
					values.put(Events.DESCRIPTION,v.getTag(R.id.key_4).toString());
					values.put(Events.CALENDAR_ID, calID);
					values.put(Events.EVENT_TIMEZONE, timeZone.getID());
					Uri uri = cr.insert(Events.CONTENT_URI, values);

					// get the event ID that is the last element in the Uri
					long eventID = Long.parseLong(uri.getLastPathSegment());
					
					ContentValues values1 = new ContentValues();
					values1.put(Attendees.ATTENDEE_NAME, v.getTag(R.id.key_3).toString());
					values1.put(Attendees.ATTENDEE_EMAIL, "trevor@example.com");
					values1.put(Attendees.ATTENDEE_RELATIONSHIP, Attendees.RELATIONSHIP_ATTENDEE);
					values1.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_OPTIONAL);
					values1.put(Attendees.ATTENDEE_STATUS, Attendees.ATTENDEE_STATUS_INVITED);
					values1.put(Attendees.EVENT_ID, eventID);
					Uri uri2 = cr.insert(Attendees.CONTENT_URI, values1);
					
					//Toast.makeText(getContext(), "Event Added to your Calendar ", Toast.LENGTH_LONG).show();

					CustomDialogClass cdd=new CustomDialogClass(getContext());
					cdd.show(); 
					cdd.setCancelable(true);
					cdd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
					
				 
			}
		});
		
		return event;
	}

	 private static String TimeStampConverter(final String inputFormat,
	            String inputTimeStamp, final String outputFormat)
	            throws ParseException {
	        return new SimpleDateFormat(outputFormat).format(new SimpleDateFormat(
	                inputFormat).parse(inputTimeStamp));
	    }

}

