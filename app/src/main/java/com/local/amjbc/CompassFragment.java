package com.local.amjbc;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;



public class CompassFragment extends Fragment implements SensorEventListener{

	private ImageView image;
	private float currentDegree = 0f;
	private SensorManager mSensorManager;
	private boolean hasOptions = false;
    private boolean isHidden = false;
    
    TextView tvHeading;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 View rootView = inflater.inflate(R.layout.fragment_compass, container, false);
		 setHasOptionsMenu(true);
		 
		// our compass image
			image = (ImageView) rootView.findViewById(R.id.imageViewCompass);

			// TextView that will tell the user what degree is he heading
			tvHeading = (TextView) rootView.findViewById(R.id.tvHeading);

			// initialize your android device sensor capabilities
			mSensorManager = (SensorManager)getActivity().getSystemService(getActivity().SENSOR_SERVICE);
			
	        return rootView;
	        
		}


	@Override
	public void onResume() {
		super.onResume();
		
		// for the system's orientation sensor registered listeners
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void onPause() {
		super.onPause();
		
		// to stop the listener and save battery
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		// get the angle around the z-axis rotated
		float degree = Math.round(event.values[0]);

		tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

		// create a rotation animation (reverse turn degree degrees)
		RotateAnimation ra = new RotateAnimation(
				currentDegree, 
				-degree,
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF,
				0.5f);

		// how long the animation will take place
		ra.setDuration(210);

		// set the animation after the end of the reservation status
		ra.setFillAfter(true);

		// Start the animation
		image.startAnimation(ra);
		currentDegree = -degree;

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// not in use
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

	    @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
	    }
}
