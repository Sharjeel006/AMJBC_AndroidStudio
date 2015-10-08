package com.local.amjbc.chandacal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.local.amjbc.R;

public class DetailActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	
	SharedPreferences sp;
	boolean CheckboxPreference, remoteSyncCheck;
	CheckBoxPreference moosiBox,remoteSyncronization, monthlyCheck;
	ListPreference organisation;
	EditTextPreference wassiyatNo;
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       addPreferencesFromResource(R.xml.activity_preference2);
       sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
       
       moosiBox = (CheckBoxPreference) findPreference("moosiPre");
       monthlyCheck = (CheckBoxPreference) findPreference("monthlyPre");
       organisation = (ListPreference) findPreference("organisationPre");
       wassiyatNo = (EditTextPreference) findPreference("wassiyatNoPre");
     //  remoteSyncronization =  (CheckBoxPreference) findPreference("remoteSync");
       
       //remoteSyncronization.setDefaultValue(false);
       
       wassiyatNo.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
   		
   		public boolean onPreferenceChange(Preference preference, Object newValue) {
   			
   			SharedPreferences.Editor editor = sp.edit();
   			editor.putString("wassiyatno", (String) newValue);
   			editor.commit();
   			
   			return false;
   		}
   	});
       
       moosiBox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
		
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			
			if(moosiBox.isChecked())
			{
				wassiyatNo.setEnabled(false);
				
			}
			else
			{
				wassiyatNo.setEnabled(false);
			}
			return true;
		}
	});
       
       organisation.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
		
		public boolean onPreferenceChange(Preference preference, Object newValue) {
		
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("org", (String) newValue);
			editor.commit();
			
			return false;
		}
	});

	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    // Set up a listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	    
	    wassiyatNo.setText(sp.getString("wassiyatno", ""));
	    organisation.setValue(sp.getString("org", "Ansaar"));
	    
	    if(sp.getBoolean("moosiPre", true)) wassiyatNo.setEnabled(true);
	    else
	    	wassiyatNo.setEnabled(false);
	    
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    // Unregister the listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {}
	
}
