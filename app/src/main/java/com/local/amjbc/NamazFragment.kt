package com.local.amjbc

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.View.OnClickListener
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.CalendarView.OnDateChangeListener
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.local.amjbc.adapters.OnSwipeTouchListener
import com.local.amjbc.model.JSONParser
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.*

class NamazFragment : Fragment() {

    var label: TextView? = null
    var juma_time: TextView? = null
    var hijri_date: TextView? = null
    var tvDates: TextView? = null
    var tvNextPrayerName: TextView? = null
    var tvNextPrayerTime: TextView? = null
    var tvNextPrayerCountdown: TextView? = null
    var pbPrayerProgress: ProgressBar? = null
    private var ramzanjump: Button? = null
    var prayerTimings: ListView? = null
    var adapter: NamazAdapter? = null
    var cv: CalendarView? = null
    var prayers: MutableList<String>? = null
    var timings: MutableList<String>? = null
    var masjidTimes: MutableList<String>? = null
    var marqueeText: MutableList<String>? = null
    var quranClassTimes: MutableList<String>? = null
    var date333 = ""
    var animation: Animation? = null
    var today: View? = null
    var sunriseText: TextView? = null
    var sunsetText: TextView? = null
    var btnPrev: View? = null
    var btnNext: View? = null
    var datePicker: MenuItem? = null
    var request_event: MenuItem? = null
    var c = Calendar.getInstance()
    private var pDialog: ProgressDialog? = null

    private lateinit var reminderManager: PrayerReminderManager

    private val PERMISSION_REQUEST_CODE = 123

    private var lastUpdateDayOfYear: Int = -1

    private val handler = Handler(Looper.getMainLooper())
    private val updateTask = object : Runnable {
        override fun run() {
            val now = Calendar.getInstance()
            val currentDay = now.get(Calendar.DAY_OF_YEAR)

            if (lastUpdateDayOfYear != -1 && lastUpdateDayOfYear != currentDay) {
                lastUpdateDayOfYear = currentDay
                resetToToday()
            }

            updateNextPrayerUI()
            handler.postDelayed(this, 60000) // Update every minute
        }
    }

    var year1: Int = 0
    var month1: Int = 0
    var day1: Int = 0

    var isTodayClicked: Boolean = false
    var isCalendarClicked: Boolean = false
    var firstTime = true

    private val url_timings_2 = "https://amj-bc.com/amjbc/get_masjid_timings.php"
    private val url_timings_3 = "https://amj-bc.com/amjbc/fetch_timings.php"
    private val url_timings_4 = "https://amj-bc.com/amjbc/getMarqueeInfo.php"
    private val url_timings_5 = "https//amj-bc.com/amjbc/getQuranClasstimes.php"
    private val url_timings_6 = "https://amj-bc.com/amjbc/get_juma_time.php"

    private val TAG_SUCCESS = "success"

    var tableTimings2: JSONArray? = null
    var tableTimings3: JSONArray? = null
    var tableTimings4: JSONArray? = null
    var tableTimings5: JSONArray? = null
    var jParser = JSONParser()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_namaz, container, false)
        setHasOptionsMenu(true)
        
        reminderManager = PrayerReminderManager(activity!!)

        checkNotificationPermission()

        pDialog = ProgressDialog(activity)
        pDialog!!.setMessage("Loading, Please wait...")
        pDialog!!.setIndeterminate(false)
        pDialog!!.setCancelable(false)

        prayers = ArrayList()
        timings = ArrayList()

        sunriseText = rootView.findViewById<TextView>(R.id.sunriseTime)
        sunsetText = rootView.findViewById<TextView>(R.id.sunsetTime)

        today = rootView.findViewById(R.id.today)
        ramzanjump = rootView.findViewById<Button>(R.id.ramazangoto)
        prayerTimings = rootView.findViewById<ListView>(R.id.listview1)
        juma_time = rootView.findViewById<TextView>(R.id.jumatimeid)
        hijri_date = rootView.findViewById<TextView>(R.id.tv_hijri_date)

        val jumuahToggle = rootView.findViewById<SwitchCompat>(R.id.jumuah_reminder_toggle)
        jumuahToggle?.isChecked = reminderManager.isReminderEnabled("Jumu'ah")
        jumuahToggle?.setOnCheckedChangeListener { _, isChecked ->
            reminderManager.setReminderEnabled("Jumu'ah", isChecked)
            if (isChecked) {
                juma_time?.text?.toString()?.let { reminderManager.scheduleReminder("Jumu'ah", it) }
            } else {
                reminderManager.cancelReminder("Jumu'ah")
            }
        }

        tvDates = rootView.findViewById<TextView>(R.id.tv_dates)
        tvNextPrayerName = rootView.findViewById<TextView>(R.id.tv_next_prayer_name)
        tvNextPrayerTime = rootView.findViewById<TextView>(R.id.tv_next_prayer_time)
        label = rootView.findViewById<TextView>(R.id.tv_next_prayer_label)
        tvNextPrayerCountdown = rootView.findViewById<TextView>(R.id.tv_next_prayer_countdown)
        pbPrayerProgress = rootView.findViewById<ProgressBar>(R.id.pb_prayer_progress)

        btnPrev = rootView.findViewById(R.id.btn_prev_day)
        btnNext = rootView.findViewById(R.id.btn_next_day)

        btnPrev?.setOnClickListener { navigateDate(-1) }
        btnNext?.setOnClickListener { navigateDate(1) }

        lastUpdateDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        updateDateUI()
        updateRamadanButtonVisibility()

        val formatter = DateTimeFormatter.ofPattern("d MMMM y")
        val hijrahDate = HijrahDate.now()
        val formattedHijriDate = hijrahDate.format(formatter)
        hijri_date?.text = formattedHijriDate

        updateRamadanButtonVisibility()

        prayerTimings!!.setOnTouchListener(object : OnSwipeTouchListener() {
            override fun onSwipeTop() {}

            override fun onSwipeRight() { navigateDate(-1) }

            override fun onSwipeLeft() { navigateDate(1) }

            override fun onSwipeBottom() {}
        })

        if (isNetworkAvailable(activity)) {
            val gqci = getQuranClassInfo()
            gqci.execute()
            val gssi = GetSunriseSunsetInfo()
            gssi.execute()
        } else {
            AlertDialog.Builder(activity)
                .setTitle("No internet")
                .setMessage("No internet connection detected, Cannot update")
                .setPositiveButton(android.R.string.yes) { dialog, which -> System.exit(0) }
                .setNegativeButton(android.R.string.no) { dialog, which -> System.exit(0) }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }

        prayers!!.add("Fajr")
        prayers!!.add("Dhuhr")
        prayers!!.add("Asr")
        prayers!!.add("Maghrib")
        prayers!!.add("Isha")

        timings!!.add("...")
        timings!!.add("...")
        timings!!.add("...")
        timings!!.add("...")
        timings!!.add("...")

        adapter = NamazAdapter(activity!!, R.layout.listview_item, prayers!!, timings!!)
        prayerTimings!!.adapter = adapter
        prayerTimings!!.post { setListViewHeightBasedOnChildren(prayerTimings) }

        ramzanjump?.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                ?.hide(this@NamazFragment)
                ?.show(activity?.supportFragmentManager?.findFragmentByTag("3")!!)
                ?.commit()
        }

        today?.setOnClickListener {
            resetToToday()
        }
        return rootView
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    activity!!,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun rescheduleAllReminders() {
        if (prayers == null || timings == null) return
        for (i in prayers!!.indices) {
            if (i < timings!!.size) {
                reminderManager.scheduleReminder(prayers!![i], timings!![i])
            }
        }
        juma_time?.text?.toString()?.let { reminderManager.scheduleReminder("Jumu'ah", it) }
    }

    private fun resetToToday() {
        c = Calendar.getInstance()
        isCalendarClicked = false
        firstTime = true
        updateDateUI()

        if (isNetworkAvailable(activity)) {
            getMasjidTimings().execute()
            getQuranClassInfo().execute()
        }

        animation = AnimationUtils.loadAnimation(activity, R.anim.shake)
        animation!!.duration = 500
        prayerTimings!!.startAnimation(animation)
    }

    private fun navigateDate(days: Int) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        if (firstTime) {
            date333 = sdf.format(c.time)
            firstTime = false
        }
        
        try {
            val date = sdf.parse(date333)
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.DATE, days)
            date333 = sdf.format(cal.time)
            c.time = cal.time
            isCalendarClicked = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        updateDateUI()

        if (isNetworkAvailable(activity)) {
            getMasjidTimings2().execute()
        }

        val animRes = if (days > 0) R.anim.pushleftin else R.anim.pushrightin
        animation = AnimationUtils.loadAnimation(activity, animRes)
        animation!!.duration = 500
        prayerTimings!!.startAnimation(animation)
    }

    private fun updateDateUI() {
        val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.US)
        val formattedDate = sdf.format(c.time)
        tvDates?.text = formattedDate.uppercase()
        datePicker?.title = formattedDate

        try {
            val formatter = DateTimeFormatter.ofPattern("d MMMM y")
            val localDate = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
            val hijrahDate = HijrahDate.from(localDate)
            val formattedHijriDate = hijrahDate.format(formatter)
            hijri_date?.text = formattedHijriDate
            updateRamadanButtonVisibility()
        } catch (ignored: Exception) {
            Log.e("NamazFragment", "Error updating Hijri date", ignored)
        }
    }

    private fun updateRamadanButtonVisibility() {
        try {
            val localDate = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
            val hijrahDate = HijrahDate.from(localDate)
            
            // HijrahMonth is 1-indexed, Ramadan is month 9
            val isRamadan = hijrahDate.get(java.time.temporal.ChronoField.MONTH_OF_YEAR) == 9
            
            ramzanjump?.visibility = if (isRamadan) View.VISIBLE else View.GONE
        } catch (e: Exception) {
            Log.e("NamazFragment", "Error checking Ramadan month", e)
            ramzanjump?.visibility = View.GONE
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        if (menu.findItem(R.id.suggest_event) != null) {
            menu.findItem(R.id.suggest_event).isVisible = false

        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        datePicker = menu.findItem(R.id.datepicker)

        if (isNetworkAvailable(activity)) {
            val gmt = getMasjidTimings()
            gmt.execute()
        }

        val sdf = SimpleDateFormat("MMM. d, yyyy", Locale.getDefault())
        val datenew = sdf.format(c.time)

        datePicker!!.title = datenew
        datePicker!!.setOnMenuItemClickListener {
            val dialog = Dialog(activity!!)
            dialog.setContentView(R.layout.custom_dialog)
            dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            dialog.setTitle("Pick a date")
            dialog.setCancelable(true)

            cv = dialog.findViewById<CalendarView>(R.id.calendarView1)
            cv?.date = c.timeInMillis

            cv?.setOnDateChangeListener { view, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                c = cal
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                date333 = sdf.format(c.time)
            }

            val dialogButton = dialog.findViewById<Button>(R.id.ok)
            dialogButton.setOnClickListener {
                isCalendarClicked = true
                isTodayClicked = true
                firstTime = false

                updateDateUI()

                if (isNetworkAvailable(activity)) {
                    getMasjidTimings2().execute()
                } else {
                    Toast.makeText(activity, "No internet connection, Cannot update", Toast.LENGTH_LONG).show()
                }

                dialog.dismiss()

                val animation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
                )
                animation.duration = 400
                prayerTimings!!.startAnimation(animation)
            }

            dialog.show()
            false
        }
    }

    @Throws(ParseException::class)
    private fun TimeStampConverter(inputFormat: String, inputTimeStamp: String, outputFormat: String): String {
        return SimpleDateFormat(outputFormat, Locale.ENGLISH).format(SimpleDateFormat(inputFormat).parse(inputTimeStamp))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()

        // Start periodic updates
        handler.post(updateTask)

        val today = Calendar.getInstance()
        val currentDay = today.get(Calendar.DAY_OF_YEAR)

        // Detect if the day has changed since last update
        if (lastUpdateDayOfYear != -1 && lastUpdateDayOfYear != currentDay) {
            lastUpdateDayOfYear = currentDay
            resetToToday()
        } else {
            // Otherwise just refresh timings to be safe
            if (isNetworkAvailable(activity)) {
                getMasjidTimings().execute()
                getQuranClassInfo().execute()
                GetSunriseSunsetInfo().execute()
            }
        }

        // Update DatePicker title
        if (datePicker != null) {
            val sdf = SimpleDateFormat("MMM. d, yyyy", Locale.getDefault())
            datePicker!!.title = sdf.format(c.time)
        }
    }

    override fun onPause() {
        super.onPause()
        // Stop periodic updates
        handler.removeCallbacks(updateTask)
    }

    @SuppressLint("SetTextI18n")
    private fun updateNextPrayerUI() {
        if (timings == null || timings!!.isEmpty() || timings!![0] == "...") return

        val now = Calendar.getInstance()
        val sdf = SimpleDateFormat("h:mm a", Locale.US)
        
        var nextPrayerIndex = -1
        var minDiff = Long.MAX_VALUE

        val prayerNames = arrayOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")

        // 1. Try to find a prayer later today
        for (i in 0 until 5) {
            try {
                if (timings!!.size <= i) break
                
                val timeStr = timings!![i]
                val prayerTime = sdf.parse(timeStr) ?: continue
                
                val prayerCal = Calendar.getInstance()
                prayerCal.time = prayerTime
                prayerCal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))

                if (prayerCal.after(now)) {
                    val diff = prayerCal.timeInMillis - now.timeInMillis
                    if (diff < minDiff) {
                        minDiff = diff
                        nextPrayerIndex = i
                    }
                }
            } catch (e: Exception) {
                Log.e("NamazFragment", "Error parsing time at index $i", e)
            }
        }

        if (nextPrayerIndex != -1) {
            // Found a prayer today
            val name = prayerNames[nextPrayerIndex]
            tvNextPrayerName?.text = name
            tvNextPrayerTime?.text = timings!![nextPrayerIndex]
            label?.text = "UPCOMING PRAYER"
            
            val totalMinutes = minDiff / (1000 * 60)
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            
            if (hours > 0) {
                tvNextPrayerCountdown?.text = "in ${hours}h ${minutes}m"
            } else {
                tvNextPrayerCountdown?.text = "in ${minutes}m"
            }
            
            // Update Progress Bar
            updateProgressBar(nextPrayerIndex, now, sdf)
            
        } else {
            // All prayers passed today, next is Fajr tomorrow
            tvNextPrayerName?.text = "Fajr"
            label?.text = "UPCOMING PRAYER"
            if (timings!!.isNotEmpty()) {
                tvNextPrayerTime?.text = timings!![0]
            }
            
            // Calculate time until tomorrow's Fajr
            try {
                val fajrTime = sdf.parse(timings!![0])
                val fajrCal = Calendar.getInstance()
                fajrCal.time = fajrTime
                fajrCal.add(Calendar.DATE, 1) // Tomorrow
                fajrCal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH) + 1)
                
                val diff = fajrCal.timeInMillis - now.timeInMillis
                val totalMinutes = diff / (1000 * 60)
                val hours = totalMinutes / 60
                val minutes = totalMinutes % 60
                
                if (hours > 0) {
                    tvNextPrayerCountdown?.text = "in ${hours}h ${minutes}m"
                } else {
                    tvNextPrayerCountdown?.text = "in ${minutes}m"
                }
            } catch (e: Exception) {
                tvNextPrayerCountdown?.text = "Tomorrow"
            }

            pbPrayerProgress?.progress = 0
        }
    }

    private fun updateProgressBar(nextIndex: Int, now: Calendar, sdf: SimpleDateFormat) {
        try {
            val nextTimeStr = timings!![nextIndex]
            val nextTime = sdf.parse(nextTimeStr) ?: return
            val nextCal = Calendar.getInstance().apply {
                time = nextTime
                set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            }

            val prevCal = Calendar.getInstance()
            if (nextIndex == 0) {
                // Next is Fajr today, previous was Isha yesterday. 
                // For simplicity, we just start from midnight or keep it at 0.
                prevCal.set(Calendar.HOUR_OF_DAY, 0)
                prevCal.set(Calendar.MINUTE, 0)
            } else {
                val prevTimeStr = timings!![nextIndex - 1]
                val prevTime = sdf.parse(prevTimeStr) ?: return
                prevCal.time = prevTime
                prevCal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            }

            val duration = nextCal.timeInMillis - prevCal.timeInMillis
            val elapsed = now.timeInMillis - prevCal.timeInMillis
            
            if (duration > 0) {
                val progress = (elapsed.toFloat() / duration.toFloat() * 100).toInt()
                pbPrayerProgress?.progress = progress.coerceIn(0, 100)
            }
        } catch (e: Exception) {
            pbPrayerProgress?.progress = 0
        }
    }

    private fun setListViewHeightBasedOnChildren(listView: ListView?) {
        if (listView == null) return
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(
                View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        if (params != null) {
            params.height = totalHeight + (listView.dividerHeight * (listAdapter.count - 1)) + listView.paddingTop + listView.paddingBottom
            listView.layoutParams = params
            listView.requestLayout()
        }
    }

    private inner class getMasjidTimings : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            if (pDialog!!.isShowing) {
                pDialog!!.dismiss()
                pDialog!!.show()
            } else {
                pDialog!!.show()
            }

        }

        override fun doInBackground(vararg params: String): String? {

            val params2 = ArrayList<Map.Entry<String, String>>()

            val json = jParser.nmakeHttpRequest(url_timings_2, "GET", params2)


            try {

                val success = json!!.getInt(TAG_SUCCESS)

                if (success == 1) {

                    tableTimings2 = json!!.getJSONArray("May")

                    masjidTimes = ArrayList()

                    for (i in 0 until tableTimings2!!.length()) {

                        val c = tableTimings2!!.getJSONObject(i)

                        val fajar = c.getString("Fajar")
                        val zuhr = c.getString("Zuhr")
                        val asar = c.getString("Asar")
                        val maghrib = c.getString("Maghrib")
                        val isha = c.getString("Isha")

                        masjidTimes!!.add(fajar)
                        masjidTimes!!.add(zuhr)
                        masjidTimes!!.add(asar)
                        masjidTimes!!.add(maghrib)
                        masjidTimes!!.add(isha)

                    }

                } else {

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: String?) {

            pDialog!!.dismiss()
            try {
                Collections.copy(timings, masjidTimes)
            } catch (e: Exception) {
                Toast.makeText(activity, "Server error", Toast.LENGTH_LONG).show()
            }

            adapter!!.notifyDataSetChanged()
            setListViewHeightBasedOnChildren(prayerTimings)
            updateNextPrayerUI()
            rescheduleAllReminders()
            if (!isTodayClicked) { //checktime();
            }
            isTodayClicked = false

        }

    }

    private inner class getMasjidTimings2 : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            if (pDialog!!.isShowing) {
                pDialog!!.dismiss()
                pDialog!!.show()
            } else {
                pDialog!!.show()
            }

        }

        override fun doInBackground(vararg params: String): String? {

            val params3 = ArrayList<Map.Entry<String, String>>()

            //params3.add(new BasicNameValuePair("date", date333));
            params3.add(AbstractMap.SimpleEntry("date", date333))
            val json = jParser.nmakeHttpRequest(url_timings_3, "GET", params3)


            try {

                val success = json!!.getInt(TAG_SUCCESS)

                if (success == 1) {

                    tableTimings3 = json!!.getJSONArray("May")

                    masjidTimes = ArrayList()

                    for (i in 0 until tableTimings3!!.length()) {

                        val c = tableTimings3!!.getJSONObject(i)

                        val fajar = c.getString("Fajar")
                        val zuhr = c.getString("Zuhr")
                        val asar = c.getString("Asar")
                        val maghrib = c.getString("Maghrib")
                        val isha = c.getString("Isha")

                        masjidTimes!!.add(fajar)
                        masjidTimes!!.add(zuhr)
                        masjidTimes!!.add(asar)
                        masjidTimes!!.add(maghrib)
                        masjidTimes!!.add(isha)

                    }

                } else {

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {

            pDialog!!.dismiss()
            try {
                Collections.copy(timings, masjidTimes)
            } catch (e: Exception) {
                Toast.makeText(activity, "Server error", Toast.LENGTH_LONG).show()
            }
            adapter!!.notifyDataSetChanged()
            setListViewHeightBasedOnChildren(prayerTimings)
            updateNextPrayerUI()
            rescheduleAllReminders()
        }


    }

    private inner class getMarqueeInfo : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            if (pDialog!!.isShowing) {
                pDialog!!.dismiss()
                pDialog!!.show()
            } else {
                pDialog!!.show()
            }
        }

        override fun doInBackground(vararg params: String): String? {

            val params4 = ArrayList<Map.Entry<String, String>>()

            val json = jParser.nmakeHttpRequest(url_timings_4, "GET", params4)

            try {
                var success = 0
                success = json!!.getInt(TAG_SUCCESS)
                if (success == 1) {

                    marqueeText = ArrayList()
                    tableTimings4 = json!!.getJSONArray("Info")

                    for (i in 0 until tableTimings4!!.length()) {

                        val c = tableTimings4!!.getJSONObject(i)
                        marqueeText!!.add(c.getString("i_text"))
                    }

                } else {

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            pDialog!!.dismiss()
            try {
                // marquee removed
            } catch (e: Exception) {
            }
        }
    }

    private inner class getQuranClassInfo : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            if (pDialog!!.isShowing) {
                pDialog!!.dismiss()
                pDialog!!.show()
            } else {
                pDialog!!.show()
            }
        }

        override fun doInBackground(vararg params: String): String? {
            val params5 = ArrayList<Map.Entry<String, String>>()
            val json = jParser.nmakeHttpRequest(url_timings_6, "GET", params5)

            try {
                var success = 0
                success = json!!.getInt(TAG_SUCCESS)
                if (success == 1) {
                    quranClassTimes = ArrayList()
                    tableTimings5 = json!!.getJSONArray("events")

                    for (i in 0 until tableTimings5!!.length()) {

                        val c = tableTimings5!!.getJSONObject(i)
                        quranClassTimes!!.add(c.getString("e_title"))
                        quranClassTimes!!.add(c.getString("e_time"))
                    }
                } else {

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        @RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
        override fun onPostExecute(result: String?) {
            pDialog!!.dismiss()
            try {
                juma_time!!.text = quranClassTimes!!.last()
                rescheduleAllReminders()
            } catch (e: Exception) {
            }

        }
    }

    private inner class GetSunriseSunsetInfo : AsyncTask<Void, Void, JSONObject>() {

        private val LATITUDE = "49.1472"
        private val LONGITUDE = "-122.8928"
        private val URL = "https://api.sunrise-sunset.org/json?lat=$LATITUDE&lng=$LONGITUDE&formatted=0"

        override fun doInBackground(vararg voids: Void): JSONObject? {
            val params = ArrayList<Map.Entry<String, String>>()

            try {
                return jParser.nmakeHttpRequest(URL, "GET", params)
            } catch (e: Exception) {
                Log.e("SunriseSunsetAPI", "HTTP request failed", e)
                return null
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(json: JSONObject?) {

            var sunriseValue = "Sunrise: Not Available"
            var sunsetValue = "Sunset: Not Available"

            if (json == null) {
                Log.e("SunriseSunsetAPI", "JSON is NULL")
                return
            }

            try {
                val status = json.optString("status", "ERROR")

                if (!status.equals("OK", ignoreCase = true)) {
                    Log.e("SunriseSunsetAPI", "API status not OK: $status")
                    return
                }

                val results = json.optJSONObject("results")
                if (results == null) {
                    Log.e("SunriseSunsetAPI", "results object missing")
                    return
                }

                val sunriseUTC = results.optString("sunrise", null)
                val sunsetUTC = results.optString("sunset", null)

                Log.d("SunriseSunsetAPI", "RAW sunrise: $sunriseUTC")
                Log.d("SunriseSunsetAPI", "RAW sunset: $sunsetUTC")

                val localSunrise = convertUTCToLocalTime(sunriseUTC)
                val localSunset = convertUTCToLocalTime(sunsetUTC)

                if (localSunrise != "--:--")
                    sunriseValue = localSunrise

                if (localSunset != "--:--")
                    sunsetValue = localSunset

            } catch (e: Exception) {
                Log.e("SunriseSunsetAPI", "Unexpected parsing error", e)
            }

            if (sunriseText != null) sunriseText!!.text = sunriseValue
            if (sunsetText != null) sunsetText!!.text = sunsetValue
        }

        /**
         * Converts UTC ISO-8601 string to user's local timezone automatically.
         */
        private fun convertUTCToLocalTime(utcTimeString: String?): String {

            if (utcTimeString == null || utcTimeString.trim { it <= ' ' }.isEmpty()) {
                return "--:--"
            }

            try {
                // 1. Parse time (UTC)
                val timeFormat = SimpleDateFormat("h:mm:ss a", Locale.US)
                timeFormat.timeZone = TimeZone.getTimeZone("UTC")

                val parsedTime = timeFormat.parse(utcTimeString)

                // 2. Get TODAY's date in UTC
                val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                val timeCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                timeCal.time = parsedTime

                // 3. Combine today's date + parsed time
                utcCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                utcCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                utcCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND))

                val fullUtcDate = utcCal.time

                // 4. Convert to local timezone (DST now works correctly)
                val localFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                localFormat.timeZone = TimeZone.getTimeZone("America/Vancouver")

                return localFormat.format(fullUtcDate)

            } catch (e: Exception) {
                Log.e("TimeConversion", "Conversion failed: $utcTimeString", e)
                return "--:--"
            }
        }
    }

    companion object {
        fun isNetworkAvailable(context: Context?): Boolean {
            var outcome = false

            if (context != null) {
                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                val networkInfos = cm.allNetworkInfo

                for (tempNetworkInfo in networkInfos) {
                    /**
                     * Can also check if the user is in roaming
                     */
                    if (tempNetworkInfo.isConnected) {
                        outcome = true
                        break
                    }
                }
            }
            return outcome
        }
    }
}
