package com.local.amjbc

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class Splash : AppCompatActivity() {

    private val SPLASH_DURATION = 1200L
    private var isBackButtonPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_2)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isBackButtonPressed = true
                finish()
            }
        })

        val splashBgColor = ContextCompat.getColor(this, R.color.splash_bg)
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = splashBgColor
            navigationBarColor = splashBgColor
        }

        val isNight = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val flags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = if (isNight) {
                flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            } else {
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        val logo = findViewById<ImageView>(R.id.img1)
        val title = findViewById<TextView>(R.id.titleID)

        val animFadein = AnimationUtils.loadAnimation(applicationContext, R.anim.fadein)
        logo?.startAnimation(animFadein)
        title?.startAnimation(animFadein)

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isBackButtonPressed) {
                val intent = Intent(this@Splash, MainActivity2::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }, SPLASH_DURATION)
    }
}