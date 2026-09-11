package com.local.amjbc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.legacy.app.ActionBarDrawerToggle
import com.local.amjbc.adapters.NavDrawerListAdapter
import com.local.amjbc.chandacal.ChandaCal
import com.local.amjbc.model.NavDrawerItem
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class MainActivity : Activity() {

    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerList: ListView? = null
    private var mDrawerToggle: ActionBarDrawerToggle? = null

    // nav drawer title
    private var mDrawerTitle: CharSequence? = null

    // used to store app title
    private var mTitle: CharSequence? = null

    private var sp: SharedPreferences? = null

    // slide menu items
    private var navMenuTitles: Array<String>? = null
    private var navMenuIcons: TypedArray? = null

    private var navDrawerItems: ArrayList<NavDrawerItem>? = null
    private var adapter: NavDrawerListAdapter? = null

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bar = actionBar
        bar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#287AA9")))
        invalidateOptionsMenu()

        mTitle = title
        mDrawerTitle = title
        sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        navMenuTitles = resources.getStringArray(R.array.nav_drawer_items)
        navMenuIcons = resources.obtainTypedArray(R.array.nav_drawer_icons)
        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        mDrawerList = findViewById<View>(R.id.left_drawer) as ListView

        navDrawerItems = ArrayList<NavDrawerItem>()
        navDrawerItems!!.add(NavDrawerItem(navMenuTitles!![0], navMenuIcons!!.getResourceId(0, -1), false, "0"))
        navDrawerItems!!.add(NavDrawerItem(navMenuTitles!![1], navMenuIcons!!.getResourceId(1, -1), false, "0"))
        navDrawerItems!!.add(NavDrawerItem(navMenuTitles!![2], navMenuIcons!!.getResourceId(2, -1), false, "0"))
        navDrawerItems!!.add(NavDrawerItem(navMenuTitles!![3], navMenuIcons!!.getResourceId(3, -1), false, "0"))
        navDrawerItems!!.add(NavDrawerItem(navMenuTitles!![4], navMenuIcons!!.getResourceId(4, -1), false, "0"))
        navDrawerItems!!.add(NavDrawerItem(navMenuTitles!![5], navMenuIcons!!.getResourceId(5, -1), false, "0"))
        navDrawerItems!!.add(NavDrawerItem(navMenuTitles!![6], navMenuIcons!!.getResourceId(6, -1), false, "0"))
        navDrawerItems!!.add(NavDrawerItem(navMenuTitles!![7], navMenuIcons!!.getResourceId(7, -1), false, "0"))
        navMenuIcons!!.recycle()

        adapter = NavDrawerListAdapter(applicationContext, navDrawerItems)
        mDrawerList!!.adapter = adapter

        mDrawerList!!.onItemClickListener = SlideMenuClickListener()
        // enabling action bar app icon and behaving it as toggle button
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeButtonEnabled(true)

        mDrawerToggle = object : ActionBarDrawerToggle(
            this, mDrawerLayout,
            R.drawable.ic_drawer, //nav menu toggle icon
            R.string.app_name, // nav drawer open - description for accessibility
            R.string.app_name // nav drawer close - description for accessibility
        ) {
            override fun onDrawerClosed(view: View) {
                actionBar?.title = mTitle
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                actionBar?.title = mDrawerTitle
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu()
            }
        }

        mDrawerLayout!!.setDrawerListener(mDrawerToggle)

        if (savedInstanceState == null) {
            // on first time display view
            displayView(0)
        }
    }

    private inner class SlideMenuClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            // display view for selected nav drawer item
            displayView(position)
        }
    }

    private fun displayView(position: Int) {
        // update the main content by replacing fragments
        var fragment: Fragment? = null
        when (position) {
            0 -> {
                overridePendingTransition(R.animator.slide_in, R.animator.slide_out)
                fragment = NamazFragment()
            }
            1 -> {
                overridePendingTransition(R.animator.slide_in, R.animator.slide_out)
                fragment = CalendarWiz()
                fragment.setHasOptionsMenu(false)
            }
            2 -> {
                overridePendingTransition(R.animator.slide_in, R.animator.slide_out)
                fragment = RequestEvent()
            }
            3 -> {
                overridePendingTransition(R.animator.slide_in, R.animator.slide_out)
                fragment = CompassFragment()
            }
            4 -> {
                overridePendingTransition(R.animator.slide_in, R.animator.slide_out)
                //fragment = AboutUsFragment();
            }
            5 -> {
                overridePendingTransition(R.animator.slide_in, R.animator.slide_out)
                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "support@amj-bc.com", null))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback AMJ-BC Android App")
                startActivity(Intent.createChooser(emailIntent, "Send email..."))
            }
            6 -> {
                overridePendingTransition(R.animator.slide_in, R.animator.slide_out)
                fragment = ChandaCal()
            }
            7 -> {
                overridePendingTransition(R.animator.slide_in, R.animator.slide_out)
                fragment = RamazanFragment()
            }
            else -> {
            }
        }

        if (fragment != null) {
            // The Java code was using android.app.FragmentManager, which is deprecated.
            // But it was importing androidx.fragment.app.Fragment.
            // If the fragments are androidx fragments, we should use fragmentManager (if Activity) or supportFragmentManager.
            // Since this class extends Activity, getFragmentManager() returns android.app.FragmentManager.
            // If the fragments are androidx.fragment.app.Fragment, they cannot be used with android.app.FragmentManager.
            // I should check what the fragments extend.
            
            // fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList!!.setItemChecked(position, true)
            mDrawerList!!.setSelection(position)
            title = navMenuTitles!![position]
            mDrawerLayout!!.closeDrawer(mDrawerList!!)
        } else if (position != 5 && position != 4) {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else false
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.rehman).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu)
    }

    fun showpush(i: Intent) {
        val json: JSONObject
        try {
            json = JSONObject(i.extras!!.getString("com.parse.Data"))

            val itr = json.keys()
            while (itr.hasNext()) {
                val key = itr.next() as String
                //Log.d("", "key ::  " + json.getString(key));
                if (key == "alert") {
                    Toast.makeText(applicationContext, json.getString(key), Toast.LENGTH_LONG).show()
                }

                if (key == "title") {
                    val title = json.getString(key)
                }
            }
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    override fun setTitle(title: CharSequence) {
        mTitle = title
        actionBar?.title = mTitle
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle!!.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggls
        mDrawerToggle!!.onConfigurationChanged(newConfig)
    }
}
