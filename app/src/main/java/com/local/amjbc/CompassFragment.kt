package com.local.amjbc

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class CompassFragment : Fragment(), SensorEventListener {

    private var image: ImageView? = null
    private var currentDegree = 0f
    private var mSensorManager: SensorManager? = null
    private var hasOptions = false
    private var isHidden = false

    var tvHeading: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                             savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_compass, container, false)
        setHasOptionsMenu(true)

        // our compass image
        image = rootView.findViewById<View>(R.id.imageViewCompass) as ImageView

        // TextView that will tell the user what degree is he heading
        tvHeading = rootView.findViewById<View>(R.id.tvHeading) as TextView

        // initialize your android device sensor capabilities
        mSensorManager = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        return rootView
    }


    override fun onResume() {
        super.onResume()

        // for the system's orientation sensor registered listeners
        mSensorManager!!.registerListener(this, mSensorManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()

        // to stop the listener and save battery
        mSensorManager!!.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {

        // get the angle around the z-axis rotated
        val degree = Math.round(event.values[0]).toFloat()

        tvHeading!!.text = "Heading: " + java.lang.Float.toString(degree) + " degrees"

        // create a rotation animation (reverse turn degree degrees)
        val ra = RotateAnimation(
            currentDegree,
            -degree,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f)

        // how long the animation will take place
        ra.duration = 210

        // set the animation after the end of the reservation status
        ra.fillAfter = true

        // Start the animation
        image!!.startAnimation(ra)
        currentDegree = -degree

    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // not in use
    }


    fun isFragmentHidden(): Boolean {
        return isHidden
    }


    override fun setHasOptionsMenu(hasOptions: Boolean) {
        this.hasOptions = hasOptions
        super.setHasOptionsMenu(hasOptions)
    }

    fun onFragmentShown() {
        isHidden = false
        if (hasOptions) super.setHasOptionsMenu(true)
    }


    fun onFragmentHidden() {
        isHidden = true
        super.setHasOptionsMenu(false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
}
