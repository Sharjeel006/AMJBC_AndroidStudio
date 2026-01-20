package com.local.amjbc;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.local.amjbc.adapters.OnSwipeTouchListener;
import com.local.amjbc.model.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class NamazFragment extends Fragment {

    TextView selectedMasjid, label, timeLeft, quranclass, quranclass2, marquee, juma_time, hijri_date;
    private Button ramzanjump, calendarwiz;
    ListView prayerTimings;
    NamazAdapter adapter;
    CalendarView cv;
    List<String> prayers, timings, masjidTimes, marqueeText, quranClassTimes;
    String date333 = "";
    Animation animation;
    ImageButton today;
    TextView sunriseText, sunsetText;
    MenuItem datePicker, request_event;
    Calendar c = Calendar.getInstance();
    private ProgressDialog pDialog;

    int year1, month1, day1;

    boolean isTodayClicked, isCalendarClicked = false;
    boolean firstTime = true;

    private static String url_timings_2 = "https://amj-bc.com/amjbc/get_masjid_timings.php";
    private static String url_timings_3 = "http://amj-bc.com/amjbc/fetch_timings.php";
    private static String url_timings_4 = "http://amj-bc.com/amjbc/getMarqueeInfo.php";
    private static String url_timings_5 = "http://amj-bc.com/amjbc/getQuranClasstimes.php";
    private static String url_timings_6 = "http://amj-bc.com/amjbc/get_juma_time.php";

    private static final String TAG_SUCCESS = "success";

    JSONArray tableTimings2, tableTimings3, tableTimings4, tableTimings5 = null;
    JSONParser jParser = new JSONParser();

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_namaz, container, false);
        setHasOptionsMenu(true);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading, Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        prayers = new ArrayList<String>();
        timings = new ArrayList<String>();

        sunriseText = (TextView) rootView.findViewById(R.id.sunriseTime);
        sunsetText = (TextView) rootView.findViewById(R.id.sunsetTime);

        today = (ImageButton) rootView.findViewById(R.id.today);
        //label = (TextView) rootView.findViewById(R.id.label1);
        //selectedMasjid = (TextView) rootView.findViewById(R.id.selectedmasjid);
        ramzanjump = (Button) rootView.findViewById(R.id.ramazangoto);
        //calendarwiz = (Button) rootView.findViewById(R.id.calendarwizbutton);
        prayerTimings = (ListView) rootView.findViewById(R.id.listview1);
       // timeLeft = (TextView) rootView.findViewById(R.id.timeleft);
        //marquee = (TextView) rootView.findViewById(R.id.MarqueeText);
        juma_time = (TextView) rootView.findViewById(R.id.jumatimeid);
        hijri_date = (TextView) rootView.findViewById(R.id.hijri_date);


        String hij_date = HijrahDate.now().toString();
        int date_len = hij_date.length();
        String m = hij_date.substring(date_len - 5, date_len - 3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM y");
        HijrahDate hijrahDate = HijrahDate.now();
        String formattedHijriDate = hijrahDate.format(formatter);
        hijri_date.setText(formattedHijriDate);

        if (m.equals("09")) ramzanjump.setVisibility(View.VISIBLE);
        else ramzanjump.setVisibility(View.GONE);



        prayerTimings.setOnTouchListener(new OnSwipeTouchListener() {
            public void onSwipeTop() {

            }

            public void onSwipeRight() {

                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                int year = c.get(Calendar.YEAR);

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    if (isCalendarClicked) {
                        c.setTime(sdf.parse(date333));
                        c.add(Calendar.DATE, -1);
                        date333 = sdf.format(c.getTime());

                    } else {
                        if (firstTime) {
                            date333 = Integer.toString(year) + "-0" + Integer.toString(month + 1) + "-" + Integer.toString(dayOfMonth);
                        }
                        c.setTime(sdf.parse(date333));
                        c.add(Calendar.DATE, -1);
                        date333 = sdf.format(c.getTime());
                        firstTime = false;

                    }

                    SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM dd, yyyy");
                    Date rrr = sdf.parse(date333);
                    String eee = sdf2.format(rrr);
                    datePicker.setTitle(eee);

                } catch (Exception ex) {
                }
                if (isNetworkAvailable(getActivity())) {

                        getMasjidTimings2 gmt2 = new getMasjidTimings2();
                        gmt2.execute();
                    } else {

                }

                animation = AnimationUtils.loadAnimation(getActivity(), R.anim.pushrightin);
                animation.setDuration(500);
                prayerTimings.startAnimation(animation);
            }

            public void onSwipeLeft() {
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                int year = c.get(Calendar.YEAR);

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    if (isCalendarClicked) {
                        c.setTime(sdf.parse(date333));
                        c.add(Calendar.DATE, 1);
                        date333 = sdf.format(c.getTime());

                    } else {
                        if (firstTime) {
                            date333 = Integer.toString(year) + "-0" + Integer.toString(month + 1) + "-" + Integer.toString(dayOfMonth);
                        }
                        c.setTime(sdf.parse(date333));
                        c.add(Calendar.DATE, 1);
                        date333 = sdf.format(c.getTime());
                        firstTime = false;
                    }

                    SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM dd, yyyy");
                    Date rrr = sdf.parse(date333);
                    String eee = sdf2.format(rrr);
                    datePicker.setTitle(eee);

                } catch (Exception ex) {
                }

                if (isNetworkAvailable(getActivity())) {

                        getMasjidTimings2 gmt2 = new getMasjidTimings2();
                        gmt2.execute();
                    } else {
                    }


                animation = AnimationUtils.loadAnimation(getActivity(), R.anim.pushleftin);
                animation.setDuration(500);
                prayerTimings.startAnimation(animation);

            }

            public void onSwipeBottom() {

            }
        });


        if (isNetworkAvailable(getActivity())) {
            //getMarqueeInfo gmi =  new getMarqueeInfo();
            //gmi.execute();
            getQuranClassInfo gqci = new getQuranClassInfo();
            gqci.execute();
            GetSunriseSunsetInfo gssi =  new GetSunriseSunsetInfo();
            gssi.execute();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("No internet")
                    .setMessage("No internet connection detected, Cannot update")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        prayers.add("Fajr");
        prayers.add("Dhuhr");
        prayers.add("Asr");
        prayers.add("Maghrib");
        prayers.add("Isha");

        timings.add("...");
        timings.add("...");
        timings.add("...");
        timings.add("...");
        timings.add("...");

        //Typeface tf2 = Typeface.createFromAsset(getActivity().getAssets(), "Existence-Light.otf");
      //  Typeface tf3 = Typeface.createFromAsset(getActivity().getAssets(), "Ubahn.ttf");

       // label.setTypeface(tf2);
        //selectedMasjid.setTypeface(tf3);
        adapter = new NamazAdapter(getActivity(), R.layout.listview_item, prayers, timings);
        prayerTimings.setAdapter(adapter);



        ramzanjump.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .hide(NamazFragment.this)
                        .show(getActivity().getSupportFragmentManager().findFragmentByTag("3"))
                        .commit();

            }
        });

        today.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                isCalendarClicked = false;
                firstTime = true;

                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                int year = c.get(Calendar.YEAR);

                if (month + 1 == 1) {
                    datePicker.setTitle("JANUARY " + dayOfMonth + ", " + year);
                } else if (month + 1 == 2) {
                    datePicker.setTitle("Feb" + dayOfMonth + ", " + year);
                } else if (month + 1 == 3) {
                    datePicker.setTitle("Mar." + dayOfMonth + ", " + year);
                } else if (month + 1 == 4) {
                    datePicker.setTitle("Apr. " + dayOfMonth + ", " + year);
                }
                if (month + 1 == 5) {
                    datePicker.setTitle("May " + dayOfMonth + ", " + year);
                } else if (month + 1 == 6) {
                    datePicker.setTitle("Jun." + dayOfMonth + ", " + year);
                } else if (month + 1 == 7) {
                    datePicker.setTitle("Jul." + dayOfMonth + ", " + year);
                } else if (month + 1 == 8) {
                    datePicker.setTitle("Aug." + dayOfMonth + ", " + year);
                } else if (month + 1 == 9) {
                    datePicker.setTitle("Sep." + dayOfMonth + ", " + year);
                } else if (month + 1 == 10) {
                    datePicker.setTitle("Oct. " + dayOfMonth + ", " + year);
                } else if (month + 1 == 11) {
                    datePicker.setTitle("Nov. " + dayOfMonth + ", " + year);
                } else if (month + 1 == 12) {
                    datePicker.setTitle("Dec. " + dayOfMonth + ", " + year);
                }

                if (isNetworkAvailable(getActivity())) {
                    getMasjidTimings gmt = new getMasjidTimings();
                    gmt.execute();
                    getQuranClassInfo gqc2 = new getQuranClassInfo();
                    gqc2.execute();
                }

                animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                animation.setDuration(500);
                prayerTimings.startAnimation(animation);
                juma_time.startAnimation(animation);

            }
        });
        return rootView;
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean outcome = false;

        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo[] networkInfos = cm.getAllNetworkInfo();

            for (NetworkInfo tempNetworkInfo : networkInfos) {
                /**
                 * Can also check if the user is in roaming
                 */
                if (tempNetworkInfo.isConnected()) {
                    outcome = true;
                    break;
                }
            }
        }
        return outcome;
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {


        if (menu.findItem(R.id.suggest_event) != null) {
            menu.findItem(R.id.suggest_event).setVisible(false);

        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        //checktime();

        super.onCreateOptionsMenu(menu, inflater);

        datePicker = menu.findItem(R.id.datepicker);


        if (isNetworkAvailable(getActivity())) {
            getMasjidTimings gmt = new getMasjidTimings();
            gmt.execute();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM. d, yyyy", Locale.getDefault());
        String datenew = sdf.format(c.getTime());


        datePicker.setTitle(datenew);
        datePicker.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.custom_dialog);
                dialog.setTitle("Pick a date");
                dialog.setCancelable(false);

                cv = (CalendarView) dialog.findViewById(R.id.calendarView1);

                cv.setOnDateChangeListener(new OnDateChangeListener() {

                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                        isCalendarClicked = true;


                        year1 = year;
                        month1 = month + 1;
                        day1 = dayOfMonth;

                        date333 = Integer.toString(year) + "-0" + Integer.toString(month + 1) + "-" + Integer.toString(dayOfMonth);


                        if (month + 1 == 1) {
                            datePicker.setTitle("Jan. " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 2) {
                            datePicker.setTitle("Feb. " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 3) {
                            datePicker.setTitle("Mar. " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 4) {
                            datePicker.setTitle("Apr. " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 5) {
                            datePicker.setTitle("May. " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 6) {
                            datePicker.setTitle("June. " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 7) {
                            datePicker.setTitle("July. " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 8) {
                            datePicker.setTitle("Aug. " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 9) {
                            datePicker.setTitle("Sep. " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 10) {
                            datePicker.setTitle("Oct. " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 11) {
                            datePicker.setTitle("Nov. " + dayOfMonth + ", " + year);
                        } else if (month + 1 == 12) {
                            datePicker.setTitle("Dec. " + dayOfMonth + ", " + year);
                        }

                    }
                });

                Button dialogButton = (Button) dialog.findViewById(R.id.ok);

                dialogButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        isTodayClicked = true;

                        if (isNetworkAvailable(getActivity())) {
                            getMasjidTimings2 gmt = new getMasjidTimings2();
                            gmt.execute();

                        } else {
                            Toast.makeText(getActivity(), "No internet connection, Cannot update", Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();

                        Animation animation = new AlphaAnimation(0.0f, 1.0f);
                        animation.setDuration(400);

                        animation = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
                        );
                        animation.setDuration(400);

                        prayerTimings.startAnimation(animation);
                    }
                });

                dialog.show();
                return false;
            }
        });
    }

    private static String TimeStampConverter(final String inputFormat, String inputTimeStamp, final String outputFormat)
            throws ParseException {
        return new SimpleDateFormat(outputFormat, Locale.ENGLISH).format(new SimpleDateFormat(inputFormat).parse(inputTimeStamp));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onResume() {

        super.onResume();
        //checktime();
    }

    private class getMasjidTimings extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog.isShowing()) {
                pDialog.dismiss();
                pDialog.show();
            } else {
                pDialog.show();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            List<Map.Entry<String, String>> params2 = new ArrayList<>();

            JSONObject json = jParser.nmakeHttpRequest(url_timings_2, "GET", params2);


            try {

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    tableTimings2 = json.getJSONArray("May");

                    masjidTimes = new ArrayList<String>();

                    for (int i = 0; i < tableTimings2.length(); i++) {

                        JSONObject c = tableTimings2.getJSONObject(i);

                        String fajar = c.getString("Fajar");
                        String zuhr = c.getString("Zuhr");
                        String asar = c.getString("Asar");
                        String maghrib = c.getString("Maghrib");
                        String isha = c.getString("Isha");

                        masjidTimes.add(fajar);
                        masjidTimes.add(zuhr);
                        masjidTimes.add(asar);
                        masjidTimes.add(maghrib);
                        masjidTimes.add(isha);

                    }

                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            pDialog.dismiss();
            try {
                Collections.copy(timings, masjidTimes);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Server error", Toast.LENGTH_LONG).show();
            }

            adapter.notifyDataSetChanged();
            if (!isTodayClicked) { //checktime();
            }
            isTodayClicked = false;

        }

    }

    private class getMasjidTimings2 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog.isShowing()) {
                pDialog.dismiss();
                pDialog.show();
            } else {
                pDialog.show();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            List<Map.Entry<String, String>> params3 = new ArrayList<>();

            //params3.add(new BasicNameValuePair("date", date333));
            params3.add(new AbstractMap.SimpleEntry<>("date", date333));
            JSONObject json = jParser.nmakeHttpRequest(url_timings_3, "GET", params3);


            try {

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    tableTimings3 = json.getJSONArray("May");

                    masjidTimes = new ArrayList<String>();

                    for (int i = 0; i < tableTimings3.length(); i++) {

                        JSONObject c = tableTimings3.getJSONObject(i);

                        String fajar = c.getString("Fajar");
                        String zuhr = c.getString("Zuhr");
                        String asar = c.getString("Asar");
                        String maghrib = c.getString("Maghrib");
                        String isha = c.getString("Isha");

                        masjidTimes.add(fajar);
                        masjidTimes.add(zuhr);
                        masjidTimes.add(asar);
                        masjidTimes.add(maghrib);
                        masjidTimes.add(isha);

                    }

                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            pDialog.dismiss();
            try {
                Collections.copy(timings, masjidTimes);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Server error", Toast.LENGTH_LONG).show();
            }
            adapter.notifyDataSetChanged();


        }


    }

    private class getMarqueeInfo extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog.isShowing()) {
                pDialog.dismiss();
                pDialog.show();
            } else {
                pDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            List<Map.Entry<String, String>> params4 = new ArrayList<>();

            JSONObject json = jParser.nmakeHttpRequest(url_timings_4, "GET", params4);

            try {
                int success = 0;
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    marqueeText = new ArrayList<String>();
                    tableTimings4 = json.getJSONArray("Info");

                    for (int i = 0; i < tableTimings4.length(); i++) {

                        JSONObject c = tableTimings4.getJSONObject(i);
                        marqueeText.add(c.getString("i_text"));
                    }

                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            try {
                if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                    marquee.setText(marqueeText.get(1));
                    return;
                }
                marquee.setText(marqueeText.get(0));
            } catch (Exception e) {
            }
        }
    }

    private class getQuranClassInfo extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog.isShowing()) {
                pDialog.dismiss();
                pDialog.show();
            } else {
                pDialog.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            List<Map.Entry<String, String>> params5 = new ArrayList<>();
            JSONObject json = jParser.nmakeHttpRequest(url_timings_6, "GET", params5);

            try {
                int success = 0;
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    quranClassTimes = new ArrayList<String>();
                    tableTimings5 = json.getJSONArray("events");

                    for (int i = 0; i < tableTimings5.length(); i++) {

                        JSONObject c = tableTimings5.getJSONObject(i);
                        quranClassTimes.add(c.getString("e_title"));
                        quranClassTimes.add(c.getString("e_time"));
                    }
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            try {
                juma_time.setText(quranClassTimes.getLast());

            } catch (Exception e) {
            }

        }
    }

    private class GetSunriseSunsetInfo extends AsyncTask<Void, Void, JSONObject> {

        private static final String LATITUDE = "49.1472";
        private static final String LONGITUDE = "-122.8928";
        private static final String URL =
                "https://api.sunrise-sunset.org/json?lat=" + LATITUDE + "&lng=" + LONGITUDE + "&formatted=0";

        @Override
        protected JSONObject doInBackground(Void... voids) {
            // Empty params for GET
            List<Map.Entry<String, String>> params = new ArrayList<>();

            try {
                return jParser.nmakeHttpRequest(URL, "GET", params);
            } catch (Exception e) {
                Log.e("SunriseSunsetAPI", "HTTP request failed", e);
                return null; // Return null so onPostExecute handles it safely
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(JSONObject json) {


            String sunriseValue = "Sunrise: Not Available";
            String sunsetValue = "Sunset: Not Available";

            if (json == null) {
                Log.e("SunriseSunsetAPI", "JSON is NULL");
                return;
            }

            Log.e("SunriseSunsetAPI", "FULL JSON RESPONSE: " + json.toString());

            try {
                String status = json.optString("status", "ERROR");

                if (!status.equalsIgnoreCase("OK")) {
                    Log.e("SunriseSunsetAPI", "API status not OK: " + status);
                    return;
                }

                JSONObject results = json.optJSONObject("results");
                if (results == null) {
                    Log.e("SunriseSunsetAPI", "results object missing");
                    return;
                }

                // TRY MULTIPLE POSSIBLE FIELDS
                String sunriseUTC =
                        results.optString("sunrise",
                                results.optString("sunrise_today",
                                        results.optString("dawn",
                                                null)));

                String sunsetUTC =
                        results.optString("sunset",
                                results.optString("sunset_today",
                                        results.optString("dusk",
                                                null)));

                Log.e("SunriseSunsetAPI", "RAW sunrise: " + sunriseUTC);
                Log.e("SunriseSunsetAPI", "RAW sunset: " + sunsetUTC);

                String localSunrise = convertUTCToLocalTime(sunriseUTC);
                String localSunset = convertUTCToLocalTime(sunsetUTC);

                if (!localSunrise.equals("--:--"))
                    sunriseValue = localSunrise;
                if (!localSunset.equals("--:--"))
                    sunsetValue = localSunset;

            } catch (Exception e) {
                Log.e("SunriseSunsetAPI", "Unexpected parsing error", e);
            }

            // Update UI
            if (sunriseText != null) sunriseText.setText(sunriseValue);
            if (sunsetText != null) sunsetText.setText(sunsetValue);
        }


        /**
         * Converts a UTC ISO-8601 time string into local Vancouver time.
         * Always returns a safe value (never throws).
         */
        private String convertUTCToLocalTime(String utcDateString) {

            if (utcDateString == null || utcDateString.trim().isEmpty()) {
                Log.e("TimeConversion", "UTC string empty");
                return "--:--";
            }

            Date date = null;

            // 1) Try API's actual format: "h:mm:ss a" in UTC
            try {
                SimpleDateFormat apiFormat = new SimpleDateFormat("h:mm:ss a", Locale.US);
                apiFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                date = apiFormat.parse(utcDateString);
            } catch (Exception ignored) {}

            // 2) Try ISO 8601 (if API ever switches)
            if (date == null) {
                try {
                    SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
                    iso.setTimeZone(TimeZone.getTimeZone("UTC"));
                    date = iso.parse(utcDateString);
                } catch (Exception ignored) {}
            }

            // If still null → cannot parse any known format
            if (date == null) {
                Log.e("TimeConversion", "Failed to convert UTC time: " + utcDateString);
                return "--:--";
            }

            // 3) Format to Vancouver time
            try {
                SimpleDateFormat local = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                local.setTimeZone(TimeZone.getTimeZone("America/Vancouver"));
                return local.format(date);
            } catch (Exception e) {
                Log.e("TimeConversion", "Format error", e);
                return "--:--";
            }
        }


    }


}





