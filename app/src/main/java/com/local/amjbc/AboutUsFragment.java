package com.local.amjbc;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

public class AboutUsFragment extends Fragment {
	
	ImageView iv;
	
	//first change
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_aboutus, container, false);
		((MainActivity)getActivity()).getActionBar().setTitle("About");
		setHasOptionsMenu(true);
		
	iv = (ImageView)rootView.findViewById(R.id.twitter);


		iv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/hashtag/amjbc"));
				startActivity(browserIntent);

			}
		});
		
        return rootView;
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

}
