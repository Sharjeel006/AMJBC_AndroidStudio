package com.local.amjbc;

import com.parse.Parse;
import com.parse.ParseInstallation;

public class Application extends android.app.Application {

@Override
public void onCreate() {
    super.onCreate();

	  Parse.initialize(this, "vsnE82WGqhZne9IuVXV6Ecae1YLyLLrSdnCB2nel", "iYhO5bD4o8XUxyGtI6ruGuqtkuuegtRiGDmh2q5r");
	  ParseInstallation.getCurrentInstallation().saveInBackground();

}
}
