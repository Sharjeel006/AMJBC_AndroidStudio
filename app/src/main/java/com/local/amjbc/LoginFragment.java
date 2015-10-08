package com.local.amjbc;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFragment extends Fragment {
	
	Button login;
	EditText username1, pass1;
	
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
					
					String uuu = String.valueOf(username1.getText());
					String ppp = String.valueOf(pass1.getText());
					
					 if(uuu.equals("admin") && ppp.equals("password") )
		     	        {
					Fragment newFragment = new AdminFragment();
					FragmentTransaction transaction = getFragmentManager().beginTransaction();
					
					getActivity().overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
					transaction.replace(R.id.content_frame , newFragment);
					
					transaction.commit();
		     	        }
					 else
					 {	 
	            		 Toast.makeText(getActivity(), "Authorized users only" , Toast.LENGTH_LONG).show();
					 }
					 
					
				}
			});
	        
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
