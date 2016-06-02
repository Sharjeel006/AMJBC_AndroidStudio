package com.local.amjbc;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.local.amjbc.model.JSONParser;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LoginFragment extends Fragment {
	
	Button login;
	EditText username1, pass1;
    String email, password;
    JSONParser jParser = new JSONParser();
    private static String url =  "http://amj-bc.com/amjbc/check.php";
    int success;
	
	   @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	  
           View rootView = inflater.inflate(R.layout.fragment_login, container, false);
	          
           username1 = (EditText)rootView.findViewById(R.id.username);
           pass1 = (EditText)rootView.findViewById(R.id.password);

	        login = (Button)rootView.findViewById(R.id.login);
	        login.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    JSONLogin task = new JSONLogin();
                    task.execute();
                }
            });
	        
			setHasOptionsMenu(true);
	        return rootView;
	        
		}

	private class JSONLogin extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            email = username1.getText().toString();
            password = pass1.getText().toString();
        }

        @Override
		protected Void doInBackground(String... params) {

            HttpClient hc = new DefaultHttpClient();
            String message;

            HttpPost p = new HttpPost(url);
            JSONObject object = new JSONObject();
            try {

                object.put("username", email.toString());
                object.put("password", password.toString());

            } catch (Exception ex) {

            }
            try {
                message = object.toString();

                p.setEntity(new StringEntity(message, "UTF8"));
                p.setHeader("Content-type", "application/json");
                HttpResponse resp = hc.execute(p);
                if (resp != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(),
                            "UTF-8"));
                    String json = reader.readLine();
                    JSONTokener tokener = new JSONTokener(json);
                    JSONObject finalResult = new JSONObject(tokener);

                    if(finalResult.get("success").toString().contains("1")) {
                        success=1;
                    }
                    else {
                        success=0;
                    }
                }
                Log.d("Status line", "" + resp.getStatusLine().getStatusCode());
            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;

		}
		@Override
		protected void onPostExecute(Void result) {

            if (success == 1) {
                Toast.makeText(getActivity().getApplicationContext(), "User found", Toast.LENGTH_LONG).show();
                Fragment newFragment = new AdminFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                getActivity().overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
                transaction.replace(R.id.content_frame , newFragment);
                transaction.commit();

            } else {
                Toast.makeText(getActivity().getApplicationContext(), "User Not found", Toast.LENGTH_LONG).show();
            }
		}
	}
}