package com.local.amjbc;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NamazAdapter extends ArrayAdapter<String> implements OnClickListener {

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
	public View getView(int position, View convert, ViewGroup parent) {

		View row = convert;
		
		TextView namaztext = null, timingtext = null;
		ImageView alarm;
		 
		Typeface tf3 = Typeface.createFromAsset(context.getAssets(), "Quicksand-Bold.otf");
		
		if( row == null )
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate( resource , parent, false );
			row.setTag(namaz.get(position));
		}
		
		namaztext = (TextView) row.findViewById( R.id.textView1);
		timingtext = (TextView) row.findViewById( R.id.textView2 );
		alarm = (ImageView)row.findViewById(R.id.alarm);
		
		alarm.setOnClickListener(this);
		
		namaztext.setTypeface(tf3);
		timingtext.setTypeface(tf3);
		
		namaztext.setText(namaz.get(position));
		timingtext.setText(timings.get(position));
		
		return row;
	}
	
	@Override
	public void onClick(View arg0) {
        
		Intent intent=new Intent(AlarmClock.ACTION_SET_ALARM).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    context.startActivity(intent);
    }

}
