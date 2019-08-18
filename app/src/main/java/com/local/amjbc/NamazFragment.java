package com.local.amjbc;

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
import android.util.Log;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.local.amjbc.adapters.OnSwipeTouchListener;
import com.local.amjbc.model.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NamazFragment extends Fragment {
	
	TextView temp, upcoming, marquee, selectedMasjid, today, label, timeLeft, quranclass, quranclass2;
	Button ramzanjump, calendarwiz;
	ListView prayerTimings;
	NamazAdapter adapter;
	CalendarView cv;
	List<String> prayers, timings, masjidTimes, marqueeText, quranClassTimes;
	String date333 = "";
	Animation animation;
	MenuItem datePicker;
	Calendar c = Calendar.getInstance();
	private ProgressDialog pDialog;

    int year1, month1, day1;

	boolean isTodayClicked, isCalendarClicked =  false;
    boolean firstTime = true;

	private static String url_timings_2 =  "http://amj-bc.com/amjbc/get_masjid_timings.php";
	private static String url_timings_3 =  "http://amj-bc.com/amjbc/fetch_timings.php";
	private static String url_timings_4 =  "http://amj-bc.com/amjbc/getMarqueeInfo.php";
    private static String url_timings_5 =  "http://amj-bc.com/amjbc/getQuranClasstimes.php";
	
	private static final String TAG_SUCCESS = "success";

	JSONArray tableTimings2, tableTimings3, tableTimings4, tableTimings5 = null;
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
        quranclass = (TextView)rootView.findViewById(R.id.timeleft2);
        quranclass2 = (TextView)rootView.findViewById(R.id.timeleft3);
        marquee = (TextView)rootView.findViewById(R.id.MarqueeText);
        selectedMasjid = (TextView)rootView.findViewById(R.id.selectedmasjid);
        ramzanjump = (Button)rootView.findViewById(R.id.ramazangoto);
        calendarwiz = (Button)rootView.findViewById(R.id.calendarwizbutton);
        prayerTimings = (ListView)rootView.findViewById(R.id.listview1);
        timeLeft = (TextView)rootView.findViewById(R.id.timeleft);
        
        prayerTimings.setOnTouchListener(new OnSwipeTouchListener() {
            public void onSwipeTop() {

            }

            public void onSwipeRight() {

                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                int year = c.get(Calendar.YEAR);

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    if (isCalendarClicked) {
                        c.setTime(sdf.parse(date333));
                        c.add(Calendar.DATE, -1);
                        date333 = sdf.format(c.getTime());

                    } else {
                        if (firstTime) {
                            date333 = Integer.toString(year) + "-0" + Integer.toString(month + 1) + "-" + Integer.toString(dayOfMonth);
                        }
                        c.setTime(sdf.parse(date333));
                        c.add(Calendar.DATE, -1);
                        date333 = sdf.format(c.getTime());
                        firstTime = false;

                    }

                    SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM dd, yyyy");
                    Date rrr = sdf.parse(date333);
                    String eee = sdf2.format(rrr);
                    datePicker.setTitle(eee);

                } catch (Exception ex) {
                }
                if (isNetworkAvailable(getActivity())) {
                    if (selectedMasjid.getText().equals("Baitur Rahman")) {
                        getMasjidTimings2 gmt2 = new getMasjidTimings2();
                        gmt2.execute();
                    } else {
                    }
                }

                animation = AnimationUtils.loadAnimation(getActivity(), R.anim.pushrightin);
                animation.setDuration(500);
                prayerTimings.startAnimation(animation);
            }

            public void onSwipeLeft() {
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                int year = c.get(Calendar.YEAR);

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    if (isCalendarClicked) {
                        c.setTime(sdf.parse(date333));
                        c.add(Calendar.DATE, 1);
                        date333 = sdf.format(c.getTime());

                    } else {
                        if (firstTime) {
                            date333 = Integer.toString(year) + "-0" + Integer.toString(month + 1) + "-" + Integer.toString(dayOfMonth);
                        }
                        c.setTime(sdf.parse(date333));
                        c.add(Calendar.DATE, 1);
                        date333 = sdf.format(c.getTime());
                        firstTime = false;
                    }

                    SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM dd, yyyy");
                    Date rrr = sdf.parse(date333);
                    String eee = sdf2.format(rrr);
                    datePicker.setTitle(eee);

                } catch (Exception ex) {
                }

                if (isNetworkAvailable(getActivity())) {
                    if (selectedMasjid.getText().equals("Baitur Rahman")) {
                        getMasjidTimings2 gmt2 = new getMasjidTimings2();
                        gmt2.execute();
                    } else {
                    }
                }

                animation = AnimationUtils.loadAnimation(getActivity(), R.anim.pushleftin);
                animation.setDuration(500);
                prayerTimings.startAnimation(animation);

            }

            public void onSwipeBottom() {

            }
        });
        
        //marquee.setSelected(true);
        //quranclass.setSelected(true);
        //quranclass2.setSelected(true);
        
        if(isNetworkAvailable(getActivity()))
        {

    		//getMarqueeInfo gmi =  new getMarqueeInfo();
    		//gmi.execute();
           // getQuranClassInfo gqci = new getQuranClassInfo();
            //gqci.execute();
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
        selectedMasjid.setTypeface(tf3);
        today.setTypeface(tf3);
        
        adapter = new NamazAdapter(getActivity(),R.layout.listview_item, prayers, timings);
        prayerTimings.setAdapter(adapter);

        calendarwiz.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Fragment newFragment = new CalendarWiz();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                getActivity().overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
                transaction.replace(R.id.content_frame, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });


        ramzanjump.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Fragment newFragment = new RamazanFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                getActivity().overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
                transaction.replace(R.id.content_frame, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        
        today.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				isCalendarClicked = false;
				firstTime = true;
				
				   int month = c.get(Calendar.MONTH);
				   int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
				   int year = c.get(Calendar.YEAR);



                if(month + 1 == 1 )
                {
                    datePicker.setTitle("JANUARY " + dayOfMonth + ", " + year);
                }
                else if(month +1 == 2)
                {
                    datePicker.setTitle("FEBRUARY " + dayOfMonth + ", " + year);
                }
                else if(month +1 == 3)
                {
                    datePicker.setTitle("MARCH " + dayOfMonth + ", " + year);
                }
                else if(month +1 == 4)
                {
                    datePicker.setTitle("APRIL " + dayOfMonth + ", " + year);
                }
				if(month + 1 == 5 )
				{
					datePicker.setTitle("May " + dayOfMonth + ", " + year);
				}
				else if(month +1 == 6)
				{
					datePicker.setTitle("June " + dayOfMonth + ", " + year);
				}
				else if(month +1 == 7)
				{
					datePicker.setTitle("July " + dayOfMonth + ", " + year);
				}
				else if(month +1 == 8)
				{
					datePicker.setTitle("AUGUST " + dayOfMonth + ", " + year);
				}
				else if(month +1 == 9)
				{
					datePicker.setTitle("SEPTEMBER " + dayOfMonth + ", " + year);
				}
				else if(month +1 == 10)
				{
					datePicker.setTitle("October " + dayOfMonth + ", " + year);
				}
				else if(month +1 == 11)
				{
					datePicker.setTitle("November " + dayOfMonth + ", " + year);
				}
				else if(month +1 == 12)
				{
					datePicker.setTitle("DECEMBER " + dayOfMonth + ", " + year);
				}
				
				if(isNetworkAvailable(getActivity()))
				{
					   getMasjidTimings gmt = new getMasjidTimings();
					   gmt.execute();
				}				

				animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
		        animation.setDuration(500);		        
		        prayerTimings.startAnimation(animation);
				
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

    	//checktime();
    	
    	super.onCreateOptionsMenu(menu, inflater);
    	
    	datePicker = menu.findItem(R.id.datepicker);
		if(isNetworkAvailable(getActivity()))
		{
			   getMasjidTimings gmt = new getMasjidTimings();
			   gmt.execute();
		}
		
    	SimpleDateFormat sdf = new SimpleDateFormat("MMMM-dd-yyyy");
        String datenew = sdf.format(c.getTime());
        
        datePicker.setTitle(datenew);
        datePicker.setOnMenuItemClickListener(new OnMenuItemClickListener() {

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

                        isCalendarClicked = true;


                        year1 = year;
                        month1 = month + 1;
                        day1 = dayOfMonth;

                        date333 = Integer.toString(year) + "-0" + Integer.toString(month + 1) + "-" + Integer.toString(dayOfMonth);


                        if (month + 1 == 1) {
                            datePicker.setTitle("Jan " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 2) {
                            datePicker.setTitle("Feb " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 3) {
                            datePicker.setTitle("Mar " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 4) {
                            datePicker.setTitle("April " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 5) {
                            datePicker.setTitle("May " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 6) {
                            datePicker.setTitle("June " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 7) {
                            datePicker.setTitle("July " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 8) {
                            datePicker.setTitle("Aug " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 9) {
                            datePicker.setTitle("Sep " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 10) {
                            datePicker.setTitle("Oct " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 11) {
                            datePicker.setTitle("Nov " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 12) {
                            datePicker.setTitle("Dec " + dayOfMonth + ", " + year);
                        }

                    }
                });

                Button dialogButton = (Button) dialog.findViewById(R.id.ok);

                dialogButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        isTodayClicked = true;

                        if (isNetworkAvailable(getActivity())) {
                            getMasjidTimings2 gmt = new getMasjidTimings2();
                            gmt.execute();

                        } else {
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

                        prayerTimings.startAnimation(animation);
                    }
                });

                dialog.show();
                return false;
            }
        });
    }
   
    private static String TimeStampConverter(final String inputFormat, String inputTimeStamp, final String outputFormat)
           throws ParseException {
       return new SimpleDateFormat(outputFormat, Locale.ENGLISH).format(new SimpleDateFormat(inputFormat).parse(inputTimeStamp));
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
    	SimpleDateFormat sdf2 = new SimpleDateFormat(inputFormat, Locale.ENGLISH);
    	Date parseddate = sdf2.parse(beta+namaztime555);
    	SimpleDateFormat print = new SimpleDateFormat(outputFormat, Locale.ENGLISH);
		String namaztime22 = TimeStampConverter(inputFormat, print.format(parseddate), outputFormat);
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
			timeLeft.setText(Long.toString(diffMinutes) + " MINS LEFT");
		}
		else {
			timeLeft.setText(Long.toString(diffHours) + "" + " HRS " + Long.toString(diffMinutes) + " MINS LEFT");
		}
		
		timeLeft.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left));

		
		switch (magic) {
		case 0:
			upcoming.setText("FAJAR");
            Toast.makeText(getActivity(), "Monthly", Toast.LENGTH_LONG).show();
			break;
		case 1:
            if(c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                upcoming.setText("JUMMAH");
                return;
            }
            else{ upcoming.setText("ZUHR"); }
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
            Writer writer = new StringWriter();
            ex.printStackTrace(new PrintWriter(writer));
            String s = writer.toString();
            Log.e("checktime exception",s);
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
		
         JSONObject json = jParser.nmakeHttpRequest(url_timings_2, "GET", params2);


         try {

             int success = json.getInt(TAG_SUCCESS);

             if (success == 1) {

                 tableTimings2 = json.getJSONArray("May");
                 
                 masjidTimes = new ArrayList<String>();
                 
                 for (int i = 0; i < tableTimings2.length(); i++) {
                	 
                	 JSONObject c = tableTimings2.getJSONObject(i);
                     
                     String fajar = c.getString("Fajar");
                     String zuhr = c.getString("Zuhr");
                     String asar = c.getString("Asar");
                     String maghrib = c.getString("Maghrib");
                     String isha = c.getString("Isha");
                     
              	   masjidTimes.add(fajar);
              	   masjidTimes.add(zuhr);
              	   masjidTimes.add(asar);
              	   masjidTimes.add(maghrib);
              	   masjidTimes.add(isha);
              	 
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
		Collections.copy(timings, masjidTimes);
		adapter.notifyDataSetChanged();
		if(!isTodayClicked)
		{ //checktime();
		     }
		isTodayClicked = false;
		
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

        JSONObject json = jParser.nmakeHttpRequest(url_timings_3, "GET", params3);


         try {

             int success = json.getInt(TAG_SUCCESS);

             if (success == 1) {

                 tableTimings3 = json.getJSONArray("May");
                 
                 masjidTimes = new ArrayList<String>();
                 
                 for (int i = 0; i < tableTimings3.length(); i++) {
                	 
                	 JSONObject c = tableTimings3.getJSONObject(i);
                     
                     String fajar = c.getString("Fajar");
                     String zuhr = c.getString("Zuhr");
                     String asar = c.getString("Asar");
                     String maghrib = c.getString("Maghrib");
                     String isha = c.getString("Isha");
                     
              	   masjidTimes.add(fajar);
              	   masjidTimes.add(zuhr);
              	   masjidTimes.add(asar);
              	   masjidTimes.add(maghrib);
              	   masjidTimes.add(isha);
              	 
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
		Collections.copy(timings, masjidTimes);
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
	//checktime();
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
		
		JSONObject json = jParser.nmakeHttpRequest(url_timings_4, "GET", params4);

         try {
             int success = 0;
             success = json.getInt(TAG_SUCCESS);
             if (success == 1) {

				 marqueeText = new ArrayList<String>();
                 tableTimings4 = json.getJSONArray("Info");

                 for (int i = 0; i < tableTimings4.length(); i++) {
                	 
                	 JSONObject c = tableTimings4.getJSONObject(i);
                     marqueeText.add(c.getString("i_text"));
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

        try {

            if(c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                marquee.setText(marqueeText.get(1));
                return;
            }
            marquee.setText(marqueeText.get(0));
        }
        catch (Exception e)
        {

        }
    }
}

    private class getQuranClassInfo extends AsyncTask<String, String, String> {

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

            ArrayList<NameValuePair> params5 = new ArrayList<NameValuePair>();

            JSONObject json = jParser.nmakeHttpRequest(url_timings_5, "GET", params5);

            try {
                int success = 0;
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    quranClassTimes = new ArrayList<String>();
                    tableTimings5 = json.getJSONArray("Info2");

                    for (int i = 0; i < tableTimings5.length(); i++) {

                        JSONObject c = tableTimings5.getJSONObject(i);
                        quranClassTimes.add(c.getString("time"));

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

            try {

                quranclass2.setText(quranClassTimes.get(0));
                quranclass.setText(quranClassTimes.get(1));
            }
            catch (Exception e)
            {

            }

        }
    }
}


