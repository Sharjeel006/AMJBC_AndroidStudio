package com.local.amjbc;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UpdateFragment extends Fragment {

	private boolean hasOptions = false;
    private boolean isHidden = false;
	
	   @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	  
	        View rootView = inflater.inflate(R.layout.fragment_update, container, false);
	        
			setHasOptionsMenu(true);
	        return rootView;
	        
		}


	   public boolean isFragmentHidden(){
	        return isHidden;
	    }


	    @Override
	    public void setHasOptionsMenu(boolean hasOptions){
	        this.hasOptions = hasOptions;
	        super.setHasOptionsMenu(hasOptions);
	    }

	    public void onFragmentShown() {
	        isHidden = false;
	        if(hasOptions) super.setHasOptionsMenu(true);
	    }


	    public void onFragmentHidden() {
	        isHidden = true;
	        super.setHasOptionsMenu(false); 
	    } 

	
}
