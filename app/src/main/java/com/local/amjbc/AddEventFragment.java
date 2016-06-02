package com.local.amjbc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.local.amjbc.model.JSONParser;

public class AddEventFragment extends Fragment  {
	
	   Button submit;
	   DatePicker dp ;
	   String ddd, mmm;
	   AutoCompleteTextView venue;
	   Spinner majlis;
	   int success;
	   EditText title, descri;
	   TimePicker tp;
	   String bigdate, bigtime;
	   
	   private ProgressDialog pDialog;
	   private static String url_create_event = "http://www.amj-bc.com/create_event.php";
	   private static final String TAG_SUCCESS = "success";
	   
	   JSONParser jsonParser = new JSONParser();
	   String[] venues = { "Baitur Rahman", "Surrey Namaz Center"};
	   
	   @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	  
	        View rootView = inflater.inflate(R.layout.fragment_addevent, container, false);
	         
	        submit = (Button)rootView.findViewById(R.id.submit);
	        dp = (DatePicker)rootView.findViewById(R.id.datePicker1);
	        venue = (AutoCompleteTextView)rootView.findViewById(R.id.venuetext);
	        title = (EditText)rootView.findViewById(R.id.titletext);
	        descri = (EditText)rootView.findViewById(R.id.descritext);
	        majlis = (Spinner)rootView.findViewById(R.id.majlistext);
	        tp = (TimePicker)rootView.findViewById(R.id.timePicker1);
	        
	        venue.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, venues));
	        
	        tp.clearFocus();
	        
	        submit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				
					 int day = dp.getDayOfMonth();
					 int month = dp.getMonth() + 1;
					 int year = dp.getYear();
					 
					bigdate = year + "-" + month + "-" + day;
					
					updateTime(tp.getCurrentHour(), tp.getCurrentMinute());
					
					if(title.getText().toString().matches("") ||  descri.getText().toString().matches("") || venue.getText().toString().matches("")){
						Toast.makeText(getActivity(), "Fill the required fields", Toast.LENGTH_LONG).show();
					}
					else
					{
						try {
							new CreateNewEvent().execute();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}
			});

			setHasOptionsMenu(true);
	        return rootView;
	        
		}


	   private void updateTime(int hours, int mins) {
	         
	        String timeSet = "";
	        if (hours > 12) {
	            hours -= 12;
	            timeSet = "PM";
	        } else if (hours == 0) {
	            hours += 12;
	            timeSet = "AM";
	        } else if (hours == 12)
	            timeSet = "PM";
	        else
	            timeSet = "AM";
	        
	        String minutes = "";
	        if (mins < 10)
	            minutes = "0" + mins;
	        else
	            minutes = String.valueOf(mins);
	 
	         bigtime = new StringBuilder().append(hours).append(':')
	                .append(minutes).append(" ").append(timeSet).toString();
	          
	    }
	   
	  
	   class CreateNewEvent extends AsyncTask<String, String, String> {
		   
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(getActivity());
	            pDialog.setMessage("Creating Event..");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(true);
	            pDialog.show();
	        }
	 
	        /**
	         * Creating product
	         * */
	        protected String doInBackground(String... args) {
	            
	        	Looper.prepare();
	            // Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("e_title", title.getText().toString()));
	            params.add(new BasicNameValuePair("e_description", descri.getText().toString()));
	            params.add(new BasicNameValuePair("e_date", bigdate));
	            params.add(new BasicNameValuePair("e_time", bigtime));
	            params.add(new BasicNameValuePair("e_venue", venue.getText().toString()));
	            params.add(new BasicNameValuePair("e_majlis", majlis.getSelectedItem().toString()));
	 
	            JSONObject json = jsonParser.nmakeHttpRequest(url_create_event, "POST", params);

	            try {
	                 success = json.getInt(TAG_SUCCESS);
	 
	                if (success == 1) {
	                   
	                } else {
	                
	                }
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }
	 
	            return null;
	        }
	 
	        /**
	         * After completing background task Dismiss the progress dialog
	         * **/
	        protected void onPostExecute(String file_url) {
	            // dismiss the dialog once done
	            pDialog.dismiss();
	            if (success == 1) {
                    // successfully created product
                Toast.makeText(getActivity(), "Event Created", Toast.LENGTH_LONG).show();    
                getActivity().onBackPressed();
                } else {
                	Toast.makeText(getActivity(), "Failed to create event", Toast.LENGTH_LONG).show();
                }
	            
	        }
	 
	    }
	   
}
