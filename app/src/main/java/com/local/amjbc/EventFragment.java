package com.local.amjbc;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import com.local.amjbc.model.JSONParser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventFragment extends Fragment {

public EventFragment(){}
    
	private ProgressDialog pDialog;
	private static String url_events = "http://www.amj-bc.com/get_all_events.php";
	private TextView error;

	
	ListView lv;
	ArrayList<HashMap<String, String>> eventList;
	SharedPreferences sp;
	
	JSONParser jParser = new JSONParser();

	   private static final String TAG_SUCCESS = "success";
	   private static final String TAG_EVENTS = "events";
	   private static final String TAG_ID = "e_id";
	   private static final String TAG_TITLE = "e_title";
	   private static final String TAG_DESC = "e_description";
	   private static final String TAG_DATE = "e_date";
	   private static final String TAG_TIME = "e_time";
	   private static final String TAG_VENUE = "e_venue";
	   private static final String TAG_MAJLIS = "e_majlis";
	   
	   JSONArray products = null;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);
        
        lv = (ListView)rootView.findViewById(R.id.eventlist);
        error = (TextView)rootView.findViewById(R.id.error);
        
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        eventList = new ArrayList<HashMap<String,String>>();
        
        if(isNetworkAvailable(getActivity()))
        {
        	new LoadAllProducts().execute();
        }
        else 
        {
             error.setTextColor(Color.parseColor("#FFFFFF"));
             error.setTextSize(20);
             error.setPadding(10, 10, 10, 10);
             error.setGravity(Gravity.CENTER);
             error.setBackgroundColor(Color.RED);
             error.setText("No Internet");    
             
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
                     // do nothing
                 }
              })
             .setIcon(android.R.drawable.ic_dialog_alert)
              .show();
        }
        
		setHasOptionsMenu(true);
        return rootView;
        
	}



    public static boolean isNetworkAvailable(Context context) {
        boolean outcome = false;

        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo[] networkInfos = cm.getAllNetworkInfo();

            for (NetworkInfo tempNetworkInfo : networkInfos) {

                if (tempNetworkInfo.isConnected()) {
                    outcome = true;
                    break;
                }
            }
        }

        return outcome;
    }
    
   
    class LoadAllProducts extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading events. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
           
        }

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            JSONObject json = jParser.nmakeHttpRequest(url_events, "GET", params);

            try {

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    products = json.getJSONArray(TAG_EVENTS);

                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        String id = c.getString(TAG_ID);
                        String title = c.getString(TAG_TITLE);
                        String description = c.getString(TAG_DESC);
                        String date = c.getString(TAG_DATE);
                        String time = c.getString(TAG_TIME);
                        String venue = c.getString(TAG_VENUE);
                        String majlis = c.getString(TAG_MAJLIS);
                         
                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_ID, id );
                        map.put(TAG_TITLE, title );
                        map.put(TAG_DESC, description);
                        map.put(TAG_DATE, date);
                        map.put(TAG_TIME, time);
                        map.put(TAG_VENUE, venue);
                        map.put(TAG_MAJLIS, majlis);
                        
                        eventList.add(map);
                    }
                    
                    
                } else {
                	
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
        
        protected void onPostExecute(String file_url) {

            pDialog.dismiss();
            
            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                	Editor edit = sp.edit();
                	edit.putInt("eventsize", eventList.size());
                	edit.commit();
                	
                	EventsAdapter adapter = new EventsAdapter(getActivity(), R.layout.events_list_item, eventList);
                    
                    lv.setAdapter(adapter);

                }
            });
        
            }
        }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
    }

