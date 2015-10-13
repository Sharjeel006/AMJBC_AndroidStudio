package com.local.amjbc;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.local.amjbc.adapters.NavDrawerListAdapter;
import com.local.amjbc.chandacal.ChandaCal;
import com.local.amjbc.model.NavDrawerItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends Activity {

	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
 
    // nav drawer title
    private CharSequence mDrawerTitle;
 
    // used to store app title
    private CharSequence mTitle;
    
    SharedPreferences sp;
 
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    
    private int newevents;
 
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#287AA9")));
		invalidateOptionsMenu();

        //HandleIntent(getIntent());

		mTitle = mDrawerTitle = getTitle();
		sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
 
        navDrawerItems = new ArrayList<NavDrawerItem>();
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1),false,"0"));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1),false,"0"));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1),false,"0"));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1),false,"0"));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1),false,"0"));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1),false,"0"));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1),false,"0"));
        navMenuIcons.recycle();
 
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);
 
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
 
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
 
        if (savedInstanceState == null) {
            // on first time display view
            displayView(0);
        }
		
	}



    public void HandleIntent(Intent intent) {

//            Log.i("Main Activity:", "PUSH RECIEVED !!" + intent.getStringExtra("message") );
//            Toast.makeText(this, intent.getStringExtra("message"), Toast.LENGTH_SHORT).show();

        Bundle extras = intent.getExtras();

        String jsonData = extras.getString("message");
        Toast.makeText(this, jsonData, Toast.LENGTH_SHORT).show();
//
//        try {
////				String action = intent.getAction();
////				String channel = intent.getExtras().getString("com.parse.Channel");
//            JSONObject json = new JSONObject(jsonData);
//
//            Iterator itr = json.keys();
//            while (itr.hasNext()) {
//                String key = (String)itr.next();
//                Log.d("App", "key ::  " + json.getString(key));
//                if(key.equals("alert")) {
//                    String msg= json.getString(key);
//                    Log.d("App", msg);
//                }
//
//                if(key.equals("title")) {
//                    String title= json.getString(key);
//                    Log.d("App", title);
//                }
//            }
//
//        } catch (Exception e) {
//            Log.d("App", "Parse Exception: " + e.getMessage());
//        }



    }

	 private class SlideMenuClickListener implements ListView.OnItemClickListener {
		 @Override
		 public void onItemClick(AdapterView<?> parent, View view, int position,
         long id) {
			 // display view for selected nav drawer item
			 displayView(position);
		 	}
	 }
	
	 
	 private void displayView(int position) {
     // update the main content by replacing fragments
     Fragment fragment = null;
     switch (position) {
     case 0:
    	 overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
         fragment = new NamazFragment();
         break;
     case 1:
    	 overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
         fragment = new EventFragment();
         fragment.setHasOptionsMenu(false);
         break;
     case 2:
    	 overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
         fragment = new LoginFragment();
         break;
     case 3:
    	 overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
         fragment = new CompassFragment();
         break;
     case 4:
    	 overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
         fragment = new AboutUsFragment();
         break;
     case 5:
    	 overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
    	 Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","support@amj-bc.com", null));
    	 emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback AMJ-BC Android App");
    	 startActivity(Intent.createChooser(emailIntent, "Send email..."));
         break;
     case 6:
    	 overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
         fragment = new ChandaCal();
         break;
       
     default:
         break;
     }

     if (fragment != null) {
         FragmentManager fragmentManager = getFragmentManager();
         fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

         // update selected item and title, then close the drawer
         mDrawerList.setItemChecked(position, true);
         mDrawerList.setSelection(position);
         setTitle(navMenuTitles[position]);
         mDrawerLayout.closeDrawer(mDrawerList);
     } else {
         // error in creating fragment
         Log.e("MainActivity", "Error in creating fragment");
     }
 }
 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		 if (mDrawerToggle.onOptionsItemSelected(item)) {
	            return true;
	        }
	        return false;
	}
	
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	
		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.rehman).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
		
	}
	
	
	public void showpush(Intent i)
	{
		JSONObject json;
		try {
			json = new JSONObject(i.getExtras().getString("com.parse.Data"));

			Iterator itr = json.keys();
			while (itr.hasNext()) {
					String key = (String)itr.next();
					//Log.d("", "key ::  " + json.getString(key));
					if(key.equals("alert")) {
						Toast.makeText(getApplicationContext(), json.getString(key), Toast.LENGTH_LONG) ;	
					}
					
					if(key.equals("title")) {
						String title = json.getString(key);  
					}
			}	
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	   @Override
	    public void setTitle(CharSequence title) {
	        mTitle = title;
	        getActionBar().setTitle(mTitle);
	        
	    }

	   @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        // Sync the toggle state after onRestoreInstanceState has occurred.
	        mDrawerToggle.syncState();
	    }
	   
	   @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
	        // Pass any configuration change to the drawer toggls
	        mDrawerToggle.onConfigurationChanged(newConfig);
	    }
}
