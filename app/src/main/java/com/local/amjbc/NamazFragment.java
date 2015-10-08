package com.local.amjbc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.local.amjbc.adapters.OnSwipeTouchListener;
import com.local.amjbc.model.JSONParser;
import com.local.amjbc.model.XMLParser;



public class NamazFragment extends Fragment {
	
	TextView temp, upcoming, marquee, selectedmasjid, today, label, timeleft;
	ImageView  infoicon;
	Button ok;
	ListView prayertimings;
	NamazAdapter adapter;
	CalendarView cv;
	List<String> prayers, timings, surreysouth, masjidtimes;
	List<String> Feb1, Feb8, Feb15, Feb22, Mar1, Mar9, Mar15, Mar22, Mar29, Apr5, Apr12, Apr19, Apr26, jumma;
	LinearLayout daynight ;
	RelativeLayout condition;
	RelativeLayout condition2;
	String city1 = "Delta";
	String woied = "9824";
	String temperature, conditions, selectedDate;
	String date333 = "";
	Animation animation;
	MenuItem datepicker;
	Calendar c = Calendar.getInstance();
	private ProgressDialog pDialog;
	String marqueetext;
	boolean istodayclicked =  false;
	
	
	int year1, month1, day1;
	boolean iscalendarclicked =  false;
	
	private static String url_timings =  "http://www.amj-bc.com/get_all_timings.php";
	private static String url_timings_2 =  "http://www.amj-bc.com/get_masjid_timings.php";
	private static String url_timings_3 =  "http://www.amj-bc.com/fetch_timings.php";
	private static String url_timings_4 =  "http://www.amj-bc.com/getMarqueeInfo.php";
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_TIMINGS = "timings";
	private static final String TAG_NAMAZ = "t_namaz";
	private static final String TAG_TIME = "t_time";
	
	JSONArray tabletimings = null;
	JSONArray tabletimings2 = null;
	JSONArray tabletimings3 = null;
	JSONArray tabletimings4 = null;
	
	boolean firsttime = true;
	
    JSONParser jParser = new JSONParser();
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_namaz, container, false);
        setHasOptionsMenu(true);
        
        ActionBar actionbar = getActivity().getActionBar();
        actionbar.setTitle("Prayer Timings");
        
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading, Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        
        prayers = new ArrayList<String>();
        timings = new ArrayList<String>();
        
        temp = (TextView)rootView.findViewById(R.id.weather);
        upcoming = (TextView)rootView.findViewById(R.id.upcoming2);
        today = (TextView)rootView.findViewById(R.id.today);
        label = (TextView)rootView.findViewById(R.id.label1);
        marquee = (TextView)rootView.findViewById(R.id.MarqueeText);
        selectedmasjid = (TextView)rootView.findViewById(R.id.selectedmasjid);
        infoicon = (ImageView)rootView.findViewById(R.id.infoicon);
        prayertimings = (ListView)rootView.findViewById(R.id.listview1);
        timeleft = (TextView)rootView.findViewById(R.id.timeleft);


        
        
        prayertimings.setOnTouchListener(new OnSwipeTouchListener() {
		    public void onSwipeTop() {
		        
		    }
		    public void onSwipeRight() {
		    	
				   int month = c.get(Calendar.MONTH);
				   int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
				   int year = c.get(Calendar.YEAR);

			try{
				   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				   Calendar c = Calendar.getInstance();
				   if(iscalendarclicked)
				   {
					   c.setTime(sdf.parse(date333));
					   c.add(Calendar.DATE, -1);
					   date333 = sdf.format(c.getTime());
					   
				   }
				   else
				   {
					   if(firsttime)
					   {
					   date333 = Integer.toString(year) + "-0" +Integer.toString(month+1) + "-" +  Integer.toString(dayOfMonth);
					   }   
						   c.setTime(sdf.parse(date333));
						   c.add(Calendar.DATE, -1);
						   date333 = sdf.format(c.getTime());
						   firsttime = false;
						   
				   }
				   
				   SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM dd, yyyy");
				   Date rrr = sdf.parse(date333);  
				   String eee = sdf2.format(rrr);
				   datepicker.setTitle(eee);
				   
				   }
				   catch(Exception ex)
				   {}
				   if(isNetworkAvailable(getActivity()))
				   {
					   if(selectedmasjid.getText().equals("Baitur Rahman"))
					   {
						   getMasjidTimings2 gmt2 =  new getMasjidTimings2();
						   gmt2.execute();   
					   }
					   else {}
				   }
		    	
		    	animation = AnimationUtils.loadAnimation(getActivity(), R.anim.pushrightin);
		        animation.setDuration(500);		        
		        prayertimings.startAnimation(animation);
		    }
		    
		    public void onSwipeLeft() {
				   int month = c.get(Calendar.MONTH);
				   int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
				   int year = c.get(Calendar.YEAR);

			try{
				   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				   Calendar c = Calendar.getInstance();
				   if(iscalendarclicked)
				   {
					   c.setTime(sdf.parse(date333));
					   c.add(Calendar.DATE, 1);
					   date333 = sdf.format(c.getTime());
					   
				   }
				   else
				   {
					   if(firsttime)
					   {
					   date333 = Integer.toString(year) + "-0" +Integer.toString(month+1) + "-" +  Integer.toString(dayOfMonth);
					   }   
						   c.setTime(sdf.parse(date333));
						   c.add(Calendar.DATE, 1);
						   date333 = sdf.format(c.getTime());
						   firsttime = false;
				   }
				   
				   SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM dd, yyyy");
				   Date rrr = sdf.parse(date333);  
				   String eee = sdf2.format(rrr);
				   datepicker.setTitle(eee);
				   
				   }
				   catch(Exception ex)
				   {}
				   
				   if(isNetworkAvailable(getActivity()))
				   {
					   if(selectedmasjid.getText().equals("Baitur Rahman"))
					   {
						   getMasjidTimings2 gmt2 =  new getMasjidTimings2();
						   gmt2.execute();   
					   }
					   else {}
				   }
				
		        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.pushleftin);
		        animation.setDuration(500);
		        prayertimings.startAnimation(animation);
		        
		    }
		    public void onSwipeBottom() {
		       
		    }
		});
        
        marquee.setSelected(true);
        
        if(isNetworkAvailable(getActivity()))
        {
        	JSONWeatherTask task = new JSONWeatherTask();
    		task.execute();	
    		getMarqueeInfo gmi =  new getMarqueeInfo();
    		gmi.execute();
        }
        else
        {
        	new AlertDialog.Builder(getActivity())
            .setTitle("No internet")
            .setMessage("No internet connection detected, Cannot update")
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { 
                    System.exit(0);
                }
             })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { 
                	System.exit(0);
                }
             })
            .setIcon(android.R.drawable.ic_dialog_alert)
             .show();
        }
        
        prayers.add("Fajar");
        prayers.add("Zuhr");
        prayers.add("Asar");
        prayers.add("Maghrib");
        prayers.add("Isha");
       
        timings.add("...");
        timings.add("...");
        timings.add("...");
        timings.add("...");
        timings.add("...");
       
        Typeface tf2 = Typeface.createFromAsset(getActivity().getAssets(), "Existence-Light.otf");
        Typeface tf3 = Typeface.createFromAsset(getActivity().getAssets(), "Ubahn.ttf");
         
        label.setTypeface(tf2);
        temp.setTypeface(tf2);
        upcoming.setTypeface(tf3);
        selectedmasjid.setTypeface(tf2);
        today.setTypeface(tf3);
        
        adapter = new NamazAdapter(getActivity(),R.layout.listview_item, prayers, timings);
        prayertimings.setAdapter(adapter);

        infoicon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				Fragment newFragment = new AboutUsFragment();
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				
				getActivity().overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
				transaction.replace(R.id.content_frame , newFragment);
				transaction.addToBackStack(null);
				transaction.commit();
				
			}
		});
        
        today.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				iscalendarclicked = false;
				firsttime = true;
				
				   int month = c.get(Calendar.MONTH);
				   int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
				   int year = c.get(Calendar.YEAR);

				if(month + 1 == 5 )
				{
					datepicker.setTitle("May " + dayOfMonth + ", " + year);
				}
				else if(month +1 == 6)
				{
					datepicker.setTitle("June " + dayOfMonth + ", " + year);
				}
				else if(month +1 == 7)
				{
					datepicker.setTitle("July " + dayOfMonth + ", " + year);
				}
				else if(month +1 == 8)
				{
					datepicker.setTitle("AUGUST " + dayOfMonth + ", " + year);
				}
				else if(month +1 == 9)
				{
					datepicker.setTitle("SEPTEMBER " + dayOfMonth + ", " + year);
				}
				else if(month +1 == 10)
				{
					datepicker.setTitle("October " + dayOfMonth + ", " + year);
				}
				else if(month +1 == 11)
				{
					datepicker.setTitle("November " + dayOfMonth + ", " + year);
				}
				else if(month +1 == 12)
				{
					datepicker.setTitle("DECEMBER " + dayOfMonth + ", " + year);
				}
				
				if(isNetworkAvailable(getActivity()))
				{
					   getMasjidTimings gmt = new getMasjidTimings();
					   gmt.execute();
				}				

				animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
		        animation.setDuration(500);		        
		        prayertimings.startAnimation(animation);
				
			}
		});
        return rootView;
    }    	
    
    public static boolean isNetworkAvailable(Context context) {
        boolean outcome = false;

        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo[] networkInfos = cm.getAllNetworkInfo();

            for (NetworkInfo tempNetworkInfo : networkInfos) {
                /**
                 * Can also check if the user is in roaming
                 */
                if (tempNetworkInfo.isConnected()) {
                    outcome = true;
                    break;
                }
            }
        }
        return outcome;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    	checktime();
    	
    	super.onCreateOptionsMenu(menu, inflater);
    	
    	datepicker = menu.findItem(R.id.datepicker);
		if(isNetworkAvailable(getActivity()))
		{
			   getMasjidTimings gmt = new getMasjidTimings();
			   gmt.execute();
		}
		
    	SimpleDateFormat sdf = new SimpleDateFormat("MMMM-dd-yyyy");
        String datenew = sdf.format(c.getTime());
        
        datepicker.setTitle(datenew);
        datepicker.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				
				final Dialog dialog = new Dialog(getActivity());
				dialog.setContentView(R.layout.custom_dialog);
				dialog.setTitle("Pick a date");
				dialog.setCancelable(false);
	 
				cv = (CalendarView) dialog.findViewById(R.id.calendarView1);
				
				cv.setOnDateChangeListener(new OnDateChangeListener() {
					
					@Override
					public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
					
						iscalendarclicked = true;
						
						
						
						year1 = year;
						month1 = month+1;
						day1 = dayOfMonth;
						
						date333 = Integer.toString(year) + "-0" +Integer.toString(month+1) + "-" +  Integer.toString(dayOfMonth);
						
						
						if(month + 1 == 1 )
						{
							datepicker.setTitle("Jan " + dayOfMonth + ", " + year);
						}
						else if(month + 1 == 2) 
						{
							datepicker.setTitle("Feb " + dayOfMonth + ", " + year);
						}
						else if(month + 1 == 3) 
						{
							datepicker.setTitle("Mar " + dayOfMonth + ", " + year);
						}
						else if(month + 1 == 4) 
						{
							datepicker.setTitle("April " + dayOfMonth + ", " + year);
						}
						else if(month + 1 == 5 )
						{
							datepicker.setTitle("May " + dayOfMonth + ", " + year);
						}
						else if(month + 1 == 6) 
						{
							datepicker.setTitle("June " + dayOfMonth + ", " + year);
						}
						else if(month + 1 == 7) 
						{
							datepicker.setTitle("July " + dayOfMonth + ", " + year);
						}
						else if(month + 1 == 8) 
						{
							datepicker.setTitle("Aug " + dayOfMonth + ", " + year);
						}
						else if(month + 1 == 9) 
						{
							datepicker.setTitle("Sep " + dayOfMonth + ", " + year);
						}
						else if(month + 1 == 10) 
						{
							datepicker.setTitle("Oct " + dayOfMonth + ", " + year);
						}
						else if(month + 1 == 11) 
						{
							datepicker.setTitle("Nov " + dayOfMonth + ", " + year);
						}
						else if(month + 1 == 12) 
						{
							datepicker.setTitle("Dec " + dayOfMonth + ", " + year);
						}
						
			}
		});
				
				Button dialogButton = (Button) dialog.findViewById(R.id.ok);
				
				dialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						istodayclicked = true;

						if(isNetworkAvailable(getActivity()))
						{
							getMasjidTimings2 gmt = new getMasjidTimings2();
							gmt.execute();
							
						}
						else 
						{
							Toast.makeText(getActivity(), "No internet connection, Cannot update", Toast.LENGTH_LONG).show();
						}
						
						dialog.dismiss();
						
						 Animation animation = new AlphaAnimation(0.0f, 1.0f);
					        animation.setDuration(400);

					        animation = new TranslateAnimation(
					            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
					            Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
					        );
					        animation.setDuration(400);
						
						prayertimings.startAnimation(animation);
					}
				});
	 
				dialog.show();
				return false;
			}
        });
    }
    
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
	   

	   today.setEnabled(true);

	       city1 = "Delta";
	       woied = "9824";

	       if(isNetworkAvailable(getActivity()))
	       {
	       JSONWeatherTask task = new JSONWeatherTask();
	       task.execute();
	       }
	       else {}
	       
	   selectedmasjid.setText("Baitur Rahman");
	   selectedmasjid.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left));

	   Animation animation = new AlphaAnimation(0.0f, 1.0f);
	           animation.setDuration(400);
	           animation = new TranslateAnimation(
	               Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
	               Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
	           );

	           animation.setDuration(400);

	   prayertimings.startAnimation(animation);
	   datepicker.setEnabled(true);

	   try{
		   if(isNetworkAvailable(getActivity()))
		   {
			   getMasjidTimings gmt = new getMasjidTimings();
			   gmt.execute();
		   }
	   	 }
	   catch(Exception ex) {}
	   
	   
	return super.onOptionsItemSelected(item);
   	}
   
   
   private static String TimeStampConverter(final String inputFormat, String inputTimeStamp, final String outputFormat)
           throws ParseException {
       return new SimpleDateFormat(outputFormat).format(new SimpleDateFormat(inputFormat).parse(inputTimeStamp));
   }

    public void checktime()
    {
    	try
    	{
    	int magic = 0;
    	Date namaztime4 = null;
    	Calendar c = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
    	String curDate = sdf.format(c.getTime());
    	Date curDate2 = sdf.parse(curDate);
    	final String inputFormat = "dd/MM/yyyy hh:mm aa";
    	final String outputFormat = "dd/MM/yyyy hh:mm aa";
    	
    	int d = c.get(Calendar.DAY_OF_MONTH);
    	int m = c.get(Calendar.MONTH);
    	int y = c.get(Calendar.YEAR) ;
    	
    	String beta;
		beta = Integer.toString(d)+ "/" + Integer.toString(m+1)+ "/"+Integer.toString(y)+ " ";
    	String namaztime555 = timings.get(4);
		String namaztime22 = TimeStampConverter(inputFormat, beta+namaztime555, outputFormat);
		Date namaztime33 = sdf.parse(namaztime22);
		
    	if(curDate2.getTime() > namaztime33.getTime())
    	{
    		beta = Integer.toString(d+1)+ "/" + Integer.toString(m+1)+ "/"+Integer.toString(y)+ " ";
    	}
    	
    	for(int i = 0 ; i < timings.size(); i++)
    	{
    			String namaztime = timings.get(i);
    		
				String namaztime2 = TimeStampConverter(inputFormat, beta+namaztime, outputFormat);
				Date namaztime3 = sdf.parse(namaztime2);
		
				if(curDate2.getTime() < namaztime3.getTime())
				{
					namaztime4 = namaztime3;
					magic = i;
					break;
				}
    	}
    	
    	long diff = namaztime4.getTime() - curDate2.getTime();
    	long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);
    	
		
		
		if(diffHours == 0)
		{
			timeleft.setText(Long.toString(diffMinutes) + " MINS LEFT" );
		}
		else {
			timeleft.setText(Long.toString(diffHours)+"" + " HRS " + Long.toString(diffMinutes) + " MINS LEFT" );	
		}
		
		timeleft.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left));
		
		
		switch (magic) {
		case 0:
			upcoming.setText("FAJAR");
			break;
		case 1:
			upcoming.setText("ZUHR");
			break;
		case 2:
			upcoming.setText("ASAR");
			break;
		case 3:
			if(timings.get(3).toString().equals(timings.get(4).toString()))
			{
				upcoming.setText("MAGHRIB & ISHA");
				upcoming.setTextSize((float)22.0);
			}else
			{
				upcoming.setText("MAGHRIB");
			}
			break;
		case 4:
			upcoming.setText("ISHA");
			upcoming.setTextSize((float)28.0);

		default:
			break;
		}
		upcoming.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left));
		
    }    	
    	catch(Exception ex)
    	{
    		
    	}

    }
    
private class JSONWeatherTask extends AsyncTask<String, Void, Void> {

	
		@Override
		protected Void doInBackground(String... params) {
			
			String xml;
			
			XMLParser parser = new XMLParser();
			try {
				xml = parser.getXmlFromUrl("http://weather.yahooapis.com/forecastrss?w="+ woied + "&u=c");
				Document doc = parser.getDomElement(xml);
				NodeList node = doc.getElementsByTagName("yweather:condition");
				Element terrif = (Element) node.item(0);
				temperature = terrif.getAttribute("temp").toString();
				conditions = terrif.getAttribute("text").toString();
			} catch (Exception e) {
				
			}		
			return null;
		
	}
		
	@Override
	protected void onPostExecute(Void result) {
	
		super.onPostExecute(result);
		temp.setText( city1 +": "  +temperature+ "°C");
		temp.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left));

	}
  }

class getNamazCenterTimings extends AsyncTask<String, String, String> {

	@Override
	protected void onPreExecute() {

		super.onPreExecute();
		if(pDialog.isShowing())
		{
			pDialog.dismiss();
	        pDialog.show();
		}
		else
		{
	        pDialog.show();   
		}	
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		
		 List<NameValuePair> params = new ArrayList<NameValuePair>();
         JSONObject json = jParser.makeHttpRequest(url_timings, "GET", params);

       try {
    	   	int success = json.getInt(TAG_SUCCESS);

             if (success == 1) {

                 tabletimings = json.getJSONArray(TAG_TIMINGS);
                 surreysouth = new ArrayList<String>();
                 
                 for (int i = 0; i < tabletimings.length(); i++) {
                	 
                	 JSONObject c = tabletimings.getJSONObject(i);
                     String timing = c.getString(TAG_TIME);
                     
              	   	 surreysouth.add(timing);
                 }
                 
             } else {
             	
             }
         } catch (Exception e) {
             e.printStackTrace();
         }

		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		pDialog.dismiss();
	Collections.copy(timings, surreysouth);
	adapter.notifyDataSetChanged();
	
	checktime();
		
	}
  }

private class getMasjidTimings extends AsyncTask<String, String, String> {

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(pDialog.isShowing())
		{
			pDialog.dismiss();
	        pDialog.show();
		}
		else
		{
	        pDialog.show();   
		}	
		
	}
	
	
	
	@Override
	protected String doInBackground(String... params) {

		List<NameValuePair> params2 = new ArrayList<NameValuePair>();
		
         JSONObject json = jParser.makeHttpRequest(url_timings_2, "GET", params2);

         try {

             int success = json.getInt(TAG_SUCCESS);

             if (success == 1) {

                 tabletimings2 = json.getJSONArray("May");
                 
                 masjidtimes = new ArrayList<String>();
                 
                 for (int i = 0; i < tabletimings2.length(); i++) {
                	 
                	 JSONObject c = tabletimings2.getJSONObject(i);
                     
                     String fajar = c.getString("Fajar");
                     String zuhr = c.getString("Zuhr");
                     String asar = c.getString("Asar");
                     String maghrib = c.getString("Maghrib");
                     String isha = c.getString("Isha");
                     
              	   masjidtimes.add(fajar);
              	   masjidtimes.add(zuhr);
              	   masjidtimes.add(asar);
              	   masjidtimes.add(maghrib);
              	   masjidtimes.add(isha);
              	 
                 }
                 
             } else {
             	
             }
         } catch (Exception e) {
             e.printStackTrace();
         }

		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		
		pDialog.dismiss();
		Collections.copy(timings, masjidtimes);
		adapter.notifyDataSetChanged();
		if(!istodayclicked)
		{ checktime(); }
		istodayclicked = false;
		
	}
	
}

private class getMasjidTimings2 extends AsyncTask<String, String, String> {

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(pDialog.isShowing())
		{
			pDialog.dismiss();
	        pDialog.show();
		}
		else
		{
	        pDialog.show();   
		}	
		
	}
	
	@Override
	protected String doInBackground(String... params) {

		ArrayList<NameValuePair> params3 = new ArrayList<NameValuePair>();
		
		params3.add(new BasicNameValuePair("date", date333));
		
         JSONObject json = jParser.makeHttpRequest(url_timings_3, "GET", params3);

         try {

             int success = json.getInt(TAG_SUCCESS);

             if (success == 1) {

                 tabletimings3 = json.getJSONArray("May");
                 
                 masjidtimes = new ArrayList<String>();
                 
                 for (int i = 0; i < tabletimings3.length(); i++) {
                	 
                	 JSONObject c = tabletimings3.getJSONObject(i);
                     
                     String fajar = c.getString("Fajar");
                     String zuhr = c.getString("Zuhr");
                     String asar = c.getString("Asar");
                     String maghrib = c.getString("Maghrib");
                     String isha = c.getString("Isha");
                     
              	   masjidtimes.add(fajar);
              	   masjidtimes.add(zuhr);
              	   masjidtimes.add(asar);
              	   masjidtimes.add(maghrib);
              	   masjidtimes.add(isha);
              	 
                 }
                 
             } else {
             	
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {

		pDialog.dismiss();
		Collections.copy(timings, masjidtimes);
		adapter.notifyDataSetChanged();
	}
	
	
}

@Override
public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
}

@Override
public void onResume() {

	super.onResume();
	
	checktime();
}

private class getMarqueeInfo extends AsyncTask<String, String, String> {

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(pDialog.isShowing())
		{
			pDialog.dismiss();
	        pDialog.show();
		}
		else
		{
	        pDialog.show();   
		}	
	}
	
	@Override
	protected String doInBackground(String... params) {

		ArrayList<NameValuePair> params4 = new ArrayList<NameValuePair>();
		
		JSONObject json = jParser.makeHttpRequest(url_timings_4, "GET", params4);

         try {
             int success = json.getInt(TAG_SUCCESS);
             if (success == 1) {

                 tabletimings4 = json.getJSONArray("Info");
                 
                 
                 for (int i = 0; i < tabletimings4.length(); i++) {
                	 
                	 JSONObject c = tabletimings4.getJSONObject(i);
                     marqueetext = c.getString("i_text");
                 }
                 
             } 
             else {
             	
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {

		pDialog.dismiss();
		marquee.setText(marqueetext);
		
		}
	}
}
