package com.local.amjbc.chandacal;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class SyncService extends Service {
	
	SharedPreferences sp;
	
	@Override
	public void onCreate() {
		super.onCreate();
		sp =PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		AccessServer server = new AccessServer();
		server.execute();
		
	}
	
	private class AccessServer extends AsyncTask<String, Integer, Integer>
	{
		@Override
		protected Integer doInBackground(String... params) {
			
			if (NetworkStatus.getInstance(SyncService.this).isOnline(SyncService.this)) {
			try 
			{	
			String searchUrl = "http://deltatechglobal.ca/ami-capp/?";

			HttpClient client = new  DefaultHttpClient();
			HttpGet get = new HttpGet(searchUrl+getDataToSync());
  
			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			String responseBody = null;
			responseBody = client.execute(get, responseHandler);

			Log.d("service", responseBody);

			if(responseBody == null)
			{
				SharedPreferences.Editor edit = sp.edit();
				edit.putString("response", "false");
				edit.commit();
			}
			else
				if(responseBody.contains("ok"))
				{
					SharedPreferences.Editor edit = sp.edit();
					edit.putString("response", "true");
					edit.commit();
	
				}
				else 
				{
					SharedPreferences.Editor edit = sp.edit();
					edit.putString("response", "false");
					edit.commit();

				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}

			Intent i = new Intent("broadcast");
			sendBroadcast(i);
			
			stopSelf();
			}
			else
			{
				stopSelf();
			}
			
			return null;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {	
		return null;
	}

	private String getDataToSync()
	{
		return "aam=" + sp.getString("main", "0")+
				"&j.salana=" + sp.getString("jsalana", "0") +
				"&org=" + sp.getString("orgz", "0")+
				"&tj=" + sp.getInt("tj",0)+
				"&wj=" + sp.getInt("wj", 0)+
				"&ef=" + sp.getInt("ef", 0)+
				"&fit=" + sp.getInt("fit", 0)+
				"&sad=" + sp.getInt("sad", 0);
		
	}

}
