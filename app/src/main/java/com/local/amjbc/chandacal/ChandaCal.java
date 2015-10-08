package com.local.amjbc.chandacal;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.local.amjbc.MainActivity;
import com.local.amjbc.R;

import java.util.Calendar;


public class ChandaCal extends Fragment {
	
	TextWatcher tw_netincome, tw_main, tw_jsalana, tw_otherChandas;
	EditText et_netincome, et_mainamount, et_otheramount;
	TextView auxamount, jsalanaamount, aam, nettotalamount, netremamount, aux, netincome, tc, bal, marquee;
	Button save, otherChandas, moreInfo;
	SharedPreferences sp;
	CheckBox cb_yearly, cb_monthly;
	boolean roundup, autocalculate, moosi, monthly;
	double income, chanda_main, chanda_jsalana, chanda_aux, net_total, rem_total;
	String org;
	int other = 0;
	SQLiteDatabase database;
	ChandaDataSource cds;
	MySqlLiteHelper helper;
	public static boolean saveCheck = false;
	BroadcastReceiver reciever1;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_chanda_calc, container, false);
		((MainActivity)getActivity()).getActionBar().setTitle("Chanda Calculator");
		setHasOptionsMenu(true);

        aam = (TextView) rootView.findViewById(R.id.main);
        aux = (TextView) rootView.findViewById(R.id.aux);
        netincome = (TextView) rootView.findViewById(R.id.textView1);
        tc = (TextView) rootView.findViewById(R.id.textView7);
        marquee = (TextView)rootView.findViewById(R.id.MarqueeTextCalFragment);
        //bal = (TextView) rootView.findViewById(R.id.textView9);
        marquee.setSelected(true);
        
        Typeface tf3 = Typeface.createFromAsset(getActivity().getAssets(), "Ubahn.ttf");
        netincome.setTypeface(tf3);
        tc.setTypeface(tf3);
        //bal.setTypeface(tf3);
        
        sp = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        other = sp.getInt("others", 0);
 
        cds = new ChandaDataSource(getActivity().getBaseContext());
        
        roundup = sp.getBoolean("roundup", false);
        autocalculate = sp.getBoolean("autocalculate", true);
        moosi = sp.getBoolean("moosiPre", false);
        monthly = sp.getBoolean("monthlyPre", false);
        org = sp.getString("org", "Ansaar");
        
        
		   
		   if(monthly == true) Toast.makeText(getActivity(), "Monthly", Toast.LENGTH_LONG).show();
		   else Toast.makeText(getActivity(), "Yearly", Toast.LENGTH_LONG).show();
        
        
        
        setOrganisation();
       
       tw_otherChandas = new TextWatcher() {
    	   
    	public void afterTextChanged(Editable otherAmount) {
   			
    		if (isIntNumber(otherAmount)) {
   			save.setEnabled(true);
    		}
    		else
    		{}
    		
   		}
		public void onTextChanged(CharSequence s, int start, int before, int count) {}
		public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
       
       };
       
   	tw_netincome = new TextWatcher() {
        
        public void afterTextChanged(Editable income_amount) {
        	
        	if (isIntNumber(income_amount)) {
        		
        		save.setEnabled(true);
    			income = Double.parseDouble(income_amount.toString());
    			
    			if(income_amount.equals(0))
    				save.setEnabled(false); 
    			
    		} else income = 0;
    		
           monthly = sp.getBoolean("monthlyPre", false);
 		   
 		   if(monthly == true) calculatemonthly();
 		   else calculate();
        	
    		populate_fields();
        }

		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
    };
		
    et_netincome = (EditText)rootView.findViewById(R.id.income);
    et_netincome.addTextChangedListener(tw_netincome);
    et_otheramount = (EditText) rootView.findViewById(R.id.otheramount);
    et_otheramount.setText(Integer.toString(other));
    et_otheramount.addTextChangedListener(tw_otherChandas);
    
    otherChandas =(Button) rootView.findViewById(R.id.otherChandas);
    moreInfo = (Button) rootView.findViewById(R.id.moreInfo);
    otherChandas.addTextChangedListener(tw_otherChandas);
    save = (Button) rootView.findViewById(R.id.save);
    
    et_mainamount = (EditText) rootView.findViewById(R.id.mainamount);
    jsalanaamount = (TextView)rootView. findViewById(R.id.jsalanaamount);
    auxamount = (TextView)rootView. findViewById(R.id.auxamount);
    nettotalamount = (TextView)rootView. findViewById(R.id.nettotalamount);
    netremamount = (TextView) rootView.findViewById(R.id.netremamount);

    final CheckBox cb_roundup = (CheckBox) rootView.findViewById(R.id.amount_roundup);
    
    
    
    if (roundup) cb_roundup.setChecked(true);
    else cb_roundup.setChecked(false);
    
    moreInfo.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
		
			String info;
			info = "No information available";
			if(org.equals("Lajna")) info = getResources().getString(R.string.chanda_info_Lajna);
			else if(org.equals("Khudaam")) info = getResources().getString(R.string.chanda_info_Khuddam);
			else if(org.equals("Ansaar")) info = getResources().getString(R.string.chanda_info_Ansar);
			
			new AlertDialog.Builder(getActivity())
		    .setTitle("Info")
		    .setMessage(info)
		    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            
		        }
		     })
		    .setIcon(android.R.drawable.ic_dialog_info)
		    .show();
			
		}
	});
    
    
    save.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			
			SharedPreferences.Editor editor = sp.edit();
			
			editor.putString("main", Double.toString(chanda_main));
			editor.putString("jsalana", Double.toString(chanda_jsalana));
			editor.putString("orgz", Double.toString(chanda_aux));
			
			editor.commit();
			
			CheckSyncronization();
			
			}

		private void CheckSyncronization() {
		
			if(sp.getBoolean("remoteSync", false))
			{
				Toast.makeText(getActivity().getBaseContext(), "Synchronizing...", Toast.LENGTH_LONG).show();
				getActivity().startService(new Intent(getActivity(), SyncService.class));
				
			}	
			else
			{
				insertToDB();
			}
		}
	});
    
    otherChandas.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			
			Fragment newFragment = new OtherChandas();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			
			getActivity().overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
			transaction.replace(R.id.content_frame , newFragment);
			transaction.addToBackStack(null);
			transaction.commit();
			
		}
	});
    
    cb_roundup.setOnClickListener(new OnClickListener() {
    	public void onClick(View v) {
    		SharedPreferences.Editor editor = sp.edit();
            if (cb_roundup.isChecked()) {
            	Log.v("CA", "roundup true");
            	editor.putBoolean("roundup", true);
            	roundup = true;
            }
            else {
            	Log.v("CA", "roundup false");
            	editor.putBoolean("roundup", false);
            	roundup = false;
            }
            editor.commit();
            populate_fields();
    	}
    });
    
    IntentFilter filter = new IntentFilter();
		filter.addAction("broadcast");
        reciever1 = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				
				insertToDB();
			}
			
		};
		
		getActivity().registerReceiver(reciever1, filter);
		return rootView;
        
    }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		menu.clear();
		inflater.inflate(R.menu.options_menu, menu);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		if(item.getItemId() == R.id.detailpreferences)
		{
			Intent intent = new Intent(getActivity(), DetailActivity.class);
			startActivity(intent);	
			
		}
		
		return super.onOptionsItemSelected(item);
	}
    


	private void insertToDB()
	{
		String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		String sync = sp.getString("response", "false");
		
		try
		{
			cds.open();
			cds.insertChanda(sp.getString("main", "0"), sp.getString("jsalana", "0"), sp.getString("orgz", "0"),sp.getInt("tj", 0),
							sp.getInt("wj", 0),sp.getInt("ef", 0), sp.getInt("fit", 0), sp.getInt("sad", 0), mydate, sync);
			
			resetFields();
			Toast.makeText(getActivity().getBaseContext(), "Saved" , Toast.LENGTH_LONG).show();
			saveCheck = true;
			
		} 
		catch(Exception e) {
			Toast.makeText(getActivity().getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();	
		}
		
	}
	
	void resetFields()
	{
		et_netincome.setText("0");
		et_mainamount.setText("0");
		jsalanaamount.setText("0");
		auxamount.setText("0");
		et_otheramount.setText("0");
		nettotalamount.setText("0");
		netremamount.setText("0");
		
		save.setEnabled(false);
	}
	
	void populate_fields() {
    	if (net_total > (income  / 3)) {
    		//Toast.makeText(this, "ERROR: Chanda cannot be more than one third of the income.", Toast.LENGTH_LONG).show();
    	} else {
    		if (roundup) {
    			et_mainamount.setText(Double.toString(Math.ceil(chanda_main)));
    			jsalanaamount.setText(Double.toString(Math.ceil(chanda_jsalana)));
    			auxamount.setText(Double.toString(Math.ceil(chanda_aux)));
    			nettotalamount.setText(Double.toString(Math.ceil(net_total)));
    			netremamount.setText(Double.toString(Math.ceil(rem_total)));
    		} else {
    			et_mainamount.setText(Double.toString(round(chanda_main, 2)));
    			jsalanaamount.setText(Double.toString(round(chanda_jsalana, 2)));
    			auxamount.setText(Double.toString(round(chanda_aux, 2)));
    			nettotalamount.setText(Double.toString(round(net_total, 2)));
    			netremamount.setText(Double.toString(round(rem_total, 2)));
    		}
    	}
    }
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	
	void calculatemonthly() {
    	if(moosi) {
    		chanda_main = income / 10 / 12;
    		}
    	else{
    		chanda_main = income / 16 / 12; 
    	}
		chanda_jsalana = income / 120 / 12;
		chanda_aux = income / 100 ;
    	
		chanda_total();
    }
	
	void calculate() {
    	if(moosi) {
    		chanda_main = income / 10;
		}
    	else{
			chanda_main = income / 16;
		}
		chanda_jsalana = income / 120;
		chanda_aux = income / 100;
		
		chanda_total();
    }
	
	void chanda_total() {
    	net_total = chanda_main + chanda_jsalana + chanda_aux + other;
		rem_total = income - net_total;
    }
	
       public boolean isIntNumber(Editable num) {
   	    try {
   	    	Double.parseDouble(num.toString());
   	    } catch(NumberFormatException nfe) {
   	        return false;
   	    }
   	    return true;
   	}
       
       private void setOrganisation() {
   	       if(org.equals("Lajna"))
   	       	{   
   	    	   aux.setText(org); }
   	        else
   	        {
   	        	aux.setText(sp.getString("org", "Ansaar")); }
   		
   	}
       
       @Override
	public void onResume() {
   		super.onResume();
   		

   		if(et_netincome.getText().toString().equals("0")) save.setEnabled(false);
   		
   		moosi = sp.getBoolean("moosiPre", false);
   		org = sp.getString("org", "Ansaar");
   		other = sp.getInt("others", 0);
   		et_otheramount.setText(Integer.toString(other));

   		aux.setText(org);
   		chanda_main = income / 10;
   		net_total = chanda_main + chanda_jsalana + chanda_aux + other;
   		rem_total = income - net_total;
   		
   		setMoosi();
   		retainAmount();	
   	}
       
       private void retainAmount()	{
   		et_netincome.setText(sp.getString("netIncome", ""), TextView.BufferType.EDITABLE);
   		et_mainamount.setText(sp.getString("mainC", "0"),TextView.BufferType.EDITABLE);
   		jsalanaamount.setText(sp.getString("jSalaC", "0"),TextView.BufferType.EDITABLE);
   		auxamount.setText(sp.getString("auxC", "0"),TextView.BufferType.EDITABLE);

   	}
       
       private void setMoosi() {
   		if(moosi)
   		{
   			et_mainamount.setText(Double.toString(Math.ceil(chanda_main)));
   			netremamount.setText(Double.toString(Math.ceil(rem_total)));
   			nettotalamount.setText(Double.toString(Math.ceil(net_total)));
   				
   			aam.setText("Wassiyat");
   			
   		}
   		else
   		{
   			chanda_main = income / 16;
   			net_total = chanda_main + chanda_jsalana + chanda_aux;
   			rem_total = income - net_total;
   			et_mainamount.setText(Double.toString(Math.ceil(chanda_main)));
   			netremamount.setText(Double.toString(Math.ceil(rem_total)));
   			nettotalamount.setText(Double.toString(Math.ceil(net_total)));
   			
   			aam.setText("Aam");
   			
   		}
   	}
	

}