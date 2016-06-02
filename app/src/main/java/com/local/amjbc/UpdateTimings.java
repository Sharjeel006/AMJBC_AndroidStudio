package com.local.amjbc;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class UpdateTimings extends Fragment {

	EditText Fajar, Zuhr, Asar, Maghrib, Isha;	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_update_timings_center, container, false);
        
		((MainActivity)getActivity()).getActionBar().setTitle("Update Timings");
		
		setHasOptionsMenu(true);
		
        Fajar = (EditText)rootView.findViewById(R.id.fajr);
        Zuhr = (EditText)rootView.findViewById(R.id.zuhr);
        Asar = (EditText)rootView.findViewById(R.id.asar);
        Maghrib = (EditText)rootView.findViewById(R.id.maghrib);
        Isha = (EditText)rootView.findViewById(R.id.isha);
		
        return rootView;
        
	}

	
}
