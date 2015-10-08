package com.local.amjbc;

import android.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public class UpdateEventFragment extends Fragment {
	

	   @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	  
	        View rootView = inflater.inflate(R.layout.fragment_editeventlist, container, false);
	        
			setHasOptionsMenu(true);
	        return rootView;
	        
		}
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		    
		    menu.findItem(R.id.rehman).setVisible(false);
		    menu.findItem(R.id.rehman).setTitle("");
		    super.onCreateOptionsMenu(menu, inflater);
		}  


}
