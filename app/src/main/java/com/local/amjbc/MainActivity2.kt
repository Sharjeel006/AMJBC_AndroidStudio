package com.local.amjbc

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.local.amjbc.chandacal.ChandaCal

class MainActivity2 : AppCompatActivity() {

    private val fragment1: Fragment = NamazFragment()
    private val fragment2: Fragment = CalendarWiz()
    private val fragment3: Fragment = RamazanFragment()
    private val fragment4: Fragment = ChandaCal()
    private val fm: FragmentManager = supportFragmentManager
    private var active = fragment1

    private var bottomNavigationView: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        title = "Vancouver Imarat"
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navView.background = ColorDrawable(Color.WHITE)
        navView.itemIconTintList = android.content.res.ColorStateList.valueOf(Color.BLACK)
        navView.itemTextColor = android.content.res.ColorStateList.valueOf(Color.BLACK)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        supportActionBar?.apply {
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
            setTitle("Vancouver Imarat")
            // Set title text color to black
            val text = android.text.SpannableString(title)
            text.setSpan(android.text.style.ForegroundColorSpan(Color.BLACK), 0, text.length, android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            title = text
        }


        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.WHITE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }


        fm.beginTransaction().add(R.id.content_frame2, fragment3, "3").hide(fragment3).commit()
        fm.beginTransaction().add(R.id.content_frame2, fragment2, "2").hide(fragment2).commit()
        fm.beginTransaction().add(R.id.content_frame2, fragment4, "4").hide(fragment4).commit()
        fm.beginTransaction().add(R.id.content_frame2, fragment1, "1").commit()

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val bottomNavigationView = BottomNavigationView(this@MainActivity2)
        when (item.itemId) {
            R.id.navigation_home -> {
                fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(active)
                    .show(fragment1)
                    .commit()
                active = fragment1
                bottomNavigationView.selectedItemId = R.id.navigation_home
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_events -> {
                fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(active)
                    .show(fragment2)
                    .commit()
                active = fragment2
                bottomNavigationView.selectedItemId = R.id.navigation_events
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_ramzan -> {
                fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(active)
                    .show(fragment3)
                    .commit()
                active = fragment3
                bottomNavigationView.selectedItemId = R.id.navigation_ramzan
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_chandacal -> {
                bottomNavigationView.selectedItemId = R.id.navigation_chandacal
                fm.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(active)
                    .show(fragment4)
                    .commit()
                active = fragment4
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
}
