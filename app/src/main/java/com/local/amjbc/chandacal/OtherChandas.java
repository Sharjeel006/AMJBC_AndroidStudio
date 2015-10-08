package com.local.amjbc.chandacal;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.local.amjbc.MainActivity;
import com.local.amjbc.R;

public class OtherChandas extends Fragment {

	Button add, cancel;
	EditText chanda_tj, chanda_wj, chanda_ef, chanda_fitrana, chanda_sadqa;
	int tj,wj,ef,fit,sad;
	SharedPreferences sp;

	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        
        View rootView = inflater.inflate(R.layout.other_chandas, container, false);
		((MainActivity)getActivity()).getActionBar().setTitle("Other Chandas");
		setHasOptionsMenu(false);
        
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        
        chanda_tj = (EditText)rootView.findViewById(R.id.tj);
        chanda_wj = (EditText)rootView.findViewById(R.id.wj);
        chanda_ef = (EditText) rootView.findViewById(R.id.ef);
        chanda_fitrana = (EditText) rootView.findViewById(R.id.fit);
        chanda_sadqa = (EditText) rootView.findViewById(R.id.sad);
        
        add = (Button) rootView.findViewById(R.id.add);
        add.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				addChandas();
			}
		});
        
       cancel = (Button)rootView.findViewById(R.id.cancel); 
       cancel.setOnClickListener(new OnClickListener() {
		
		public void onClick(View arg0) {
			getActivity().onBackPressed();
			
		}
	});
       
       return rootView;
       
    }

    
    
    void addChandas()
    {
    	if(chanda_tj.getText().length() == 0 && chanda_wj.getText().length() == 0 && chanda_ef.getText().length() == 0 && chanda_fitrana.getText().length() == 0 && chanda_sadqa.getText().length() == 0)
    	{    		
    		Toast.makeText(getActivity().getBaseContext(), "Enter Values", Toast.LENGTH_LONG).show();
    	}
    	else
    	{
    	
    	SharedPreferences.Editor editit = sp.edit();
    	
		tj = Integer.parseInt(chanda_tj.getText().toString());
    	wj = Integer.parseInt(chanda_wj.getText().toString());
    	ef = Integer.parseInt(chanda_ef.getText().toString());
    	fit = Integer.parseInt(chanda_fitrana.getText().toString());
    	sad = Integer.parseInt(chanda_sadqa.getText().toString());
    	
    	editit.putInt("tj", tj);
    	editit.putInt("wj", wj);
    	editit.putInt("ef", ef);
    	editit.putInt("fit", fit);
    	editit.putInt("sad", sad);
    	
    	int otherTotal = sad + tj + wj + ef + fit;
    	
    	Toast.makeText(getActivity().getBaseContext(), Integer.toString(otherTotal), Toast.LENGTH_LONG).show();
    	
    	editit.putInt("others", otherTotal);
    	editit.commit();
    	
    	getActivity().onBackPressed();
    	
//    	Fragment newFragment = new OtherChandas();
//		FragmentTransaction transaction = getFragmentManager().beginTransaction();
//		
//		getActivity().overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
//		transaction.replace(R.id.content_frame , newFragment);
//		transaction.addToBackStack(null);
//		transaction.commit();

    	}
    	
    }
//    @Override
//    protected void onResume() {
//    	super.onResume();
//    	
//    	if(CalculateActivity.saveCheck ==  false)
//    	{
//    		retainAmount();
//    	}
//    	else
//    	{
//    		
//    	}
//    	
//    }

	private void retainAmount() {
		
		chanda_tj.setText(Integer.toString(sp.getInt("tj", 0)), TextView.BufferType.EDITABLE );
		chanda_wj.setText(Integer.toString(sp.getInt("wj", 0)), TextView.BufferType.EDITABLE );
		chanda_ef.setText(Integer.toString(sp.getInt("ef", 0)), TextView.BufferType.EDITABLE );
		chanda_fitrana.setText(Integer.toString(sp.getInt("fit", 0)), TextView.BufferType.EDITABLE );
		chanda_sadqa.setText(Integer.toString(sp.getInt("sad", 0)), TextView.BufferType.EDITABLE );
		
	}
}

