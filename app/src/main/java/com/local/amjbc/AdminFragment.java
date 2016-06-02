package com.local.amjbc;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AdminFragment extends Fragment  {

	Button add, timings;
	EditText username, pass;
	
	   @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	  
	        View rootView = inflater.inflate(R.layout.fragment_admin_options, container, false);
	       
	        setHasOptionsMenu(false);

	        username = (EditText)rootView.findViewById(R.id.username);
	        pass = (EditText)rootView.findViewById(R.id.password);
	        timings = (Button)rootView.findViewById(R.id.timingsupdate);
	        add = (Button)rootView.findViewById(R.id.button1);
	        add.setOnClickListener(onClickListener);
	        timings.setOnClickListener(onClickListener2);
	        
			setHasOptionsMenu(true);
	        return rootView;
	        
		}


	   private OnClickListener onClickListener = new OnClickListener() {
		     @Override
		     public void onClick(View v) {
		         
		            			Fragment newFragment = new AddEventFragment();
				 				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				 				
				 				getActivity().overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
				 				transaction.replace(R.id.content_frame , newFragment);
				 				transaction.addToBackStack(null);
				 				
				 				transaction.commit();
		     	    
		         }
		     
	   };
	   
	   
	   private OnClickListener onClickListener2 = new OnClickListener() {
		   
		   public void onClick(View v) {
			   
			   Fragment newFragment2 = new UpdateTimings();
				FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
				
				getActivity().overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
				transaction2.replace(R.id.content_frame , newFragment2);
				transaction2.addToBackStack(null);
				
				transaction2.commit();
			   
		   };
		   
	   };
}


		   


