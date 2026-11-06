package com.local.amjbc

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView

class Splash : Activity(), SensorEventListener {

    private val SPLASH_DURATION = 1000
    private var startMain: Handler? = null
    private var isBackButtonPressed = false
    private var tv: TextView? = null
    private var hangoutTvOne: TextView? = null
    private var hangoutTvTwo: TextView? = null
    private var hangoutTvThree: TextView? = null
    private var waveOneAnimator: ObjectAnimator? = null
    private var waveTwoAnimator: ObjectAnimator? = null
    private var waveThreeAnimator: ObjectAnimator? = null
    var animFadein: Animation? = null
    var mp: MediaPlayer? = null
    var lm: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.splash_2)

        tv = findViewById<View>(R.id.titleID) as TextView

        hangoutTvOne = findViewById<View>(R.id.hangoutTvOne) as TextView
        hangoutTvTwo = findViewById<View>(R.id.hangoutTvTwo) as TextView
        hangoutTvThree = findViewById<View>(R.id.hangoutTvThree) as TextView

        hangoutTvOne!!.visibility = View.GONE
        hangoutTvTwo!!.visibility = View.GONE
        hangoutTvThree!!.visibility = View.GONE

        hangoutTvOne!!.visibility = View.VISIBLE
        hangoutTvTwo!!.visibility = View.VISIBLE
        hangoutTvThree!!.visibility = View.VISIBLE

        waveAnimation()

        val tf2 = Typeface.createFromAsset(assets, "Bender-Solid.otf")
        tv!!.typeface = tf2

        animFadein = AnimationUtils.loadAnimation(applicationContext, R.anim.fadein)
        tv!!.startAnimation(animFadein)

        startMain = Handler()

        startMain!!.postDelayed({
            finish()

            if (!isBackButtonPressed) {
                val intent = Intent(this@Splash, MainActivity2::class.java)
                this@Splash.startActivity(intent)
                finish()
            }
        }, SPLASH_DURATION.toLong())
    }

    fun waveAnimation() {
        val tvOne_Y = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, -40.0f)
        val tvOne_X = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f)
        waveOneAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvOne, tvOne_X, tvOne_Y)
        waveOneAnimator!!.repeatCount = -1
        waveOneAnimator!!.repeatMode = ValueAnimator.REVERSE
        waveOneAnimator!!.duration = 300
        waveOneAnimator!!.start()

        val tvTwo_Y = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, -40.0f)
        val tvTwo_X = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f)
        waveTwoAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvTwo, tvTwo_X, tvTwo_Y)
        waveTwoAnimator!!.repeatCount = -1
        waveTwoAnimator!!.repeatMode = ValueAnimator.REVERSE
        waveTwoAnimator!!.duration = 300
        waveTwoAnimator!!.startDelay = 100
        waveTwoAnimator!!.start()

        val tvThree_Y = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, -40.0f)
        val tvThree_X = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0f)
        waveThreeAnimator = ObjectAnimator.ofPropertyValuesHolder(hangoutTvThree, tvThree_X, tvThree_Y)
        waveThreeAnimator!!.repeatCount = -1
        waveThreeAnimator!!.repeatMode = ValueAnimator.REVERSE
        waveThreeAnimator!!.duration = 300
        waveThreeAnimator!!.startDelay = 200
        waveThreeAnimator!!.start()
    }

    override fun onBackPressed() {
        isBackButtonPressed = true
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onSensorChanged(event: SensorEvent) {
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }
}