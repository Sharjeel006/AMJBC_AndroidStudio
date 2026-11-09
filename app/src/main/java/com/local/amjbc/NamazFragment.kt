package com.local.amjbc

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
    var tvPrevPrayerStatus: TextView? = null
    var pbPrayerProgress: ProgressBar? = null
    var nsvRoot: androidx.core.widget.NestedScrollView? = null
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

    var isTodayClicked: Boolean = false
    var isCalendarClicked: Boolean = false
    var firstTime = true

    private val url_timings_2 = "https://amj-bc.com/amjbc/get_masjid_timings.php"
    private val url_timings_3 = "https://amj-bc.com/amjbc/fetch_timings.php"
    private val url_timings_4 = "https://amj-bc.com/amjbc/getMarqueeInfo.php"
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

        reminderManager = PrayerReminderManager(requireActivity())

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

        nsvRoot = rootView.findViewById(R.id.nsv_root)
        tvDates = rootView.findViewById<TextView>(R.id.tv_dates)
        tvNextPrayerName = rootView.findViewById<TextView>(R.id.tv_next_prayer_name)
        tvNextPrayerTime = rootView.findViewById<TextView>(R.id.tv_next_prayer_time)
        label = rootView.findViewById<TextView>(R.id.tv_next_prayer_label)
        tvNextPrayerCountdown = rootView.findViewById<TextView>(R.id.tv_next_prayer_countdown)
        tvPrevPrayerStatus = rootView.findViewById<TextView>(R.id.tv_prev_prayer_status)
        pbPrayerProgress = rootView.findViewById<ProgressBar>(R.id.pb_prayer_progress)

        btnPrev = rootView.findViewById(R.id.btn_prev_day)
        btnNext = rootView.findViewById(R.id.btn_next_day)

        btnPrev?.setOnClickListener { navigateDate(-1) }
        btnNext?.setOnClickListener { navigateDate(1) }

        lastUpdateDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        updateDateUI()
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

        adapter = NamazAdapter(requireActivity(), R.layout.listview_item, prayers!!, timings!!)
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
                    requireActivity(),
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
        nsvRoot?.scrollTo(0, 0)

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
        val sdf = SimpleDateFormat("EEE, MMM d, yyyy", Locale.US)
        val formattedDate = sdf.format(c.time)
        datePicker?.title = formattedDate

        try {
            val formatter = DateTimeFormatter.ofPattern("d MMMM y", Locale.ENGLISH)
            val localDate = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
            val hijrahDate = HijrahDate.from(localDate)
            val formattedHijriDate = hijrahDate.format(formatter)
            tvDates?.text = "$formattedDate • $formattedHijriDate AH"
            hijri_date?.text = formattedHijriDate
            updateRamadanButtonVisibility()
        } catch (ignored: Exception) {
            Log.e("NamazFragment", "Error updating Hijri date", ignored)
            tvDates?.text = formattedDate
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
            val dialog = Dialog(requireActivity())
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val act = activity ?: return
        val v = view ?: return
        val imm = act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()

        handler.post(updateTask)

        val today = Calendar.getInstance()
        val currentDay = today.get(Calendar.DAY_OF_YEAR)

        if (lastUpdateDayOfYear != -1 && lastUpdateDayOfYear != currentDay) {
            lastUpdateDayOfYear = currentDay
            resetToToday()
        } else {
            if (isNetworkAvailable(activity)) {
                getMasjidTimings().execute()
                getQuranClassInfo().execute()
                GetSunriseSunsetInfo().execute()
            }
        }

        if (datePicker != null) {
            val sdf = SimpleDateFormat("MMM. d, yyyy", Locale.getDefault())
            datePicker!!.title = sdf.format(c.time)
        }
    }

    override fun onPause() {
        super.onPause()
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
        val statusLabels = arrayOf("Before Fajr", "After Fajr", "After Dhuhr", "After Asr", "After Maghrib")

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
            val name = prayerNames[nextPrayerIndex]
            tvNextPrayerName?.text = name
            tvNextPrayerTime?.text = timings!![nextPrayerIndex]
            label?.text = "UPCOMING PRAYER"
            tvPrevPrayerStatus?.text = statusLabels[nextPrayerIndex]
            adapter?.setActivePrayer(name)

            val totalMinutes = minDiff / (1000 * 60)
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60

            if (hours > 0) {
                tvNextPrayerCountdown?.text = "in ${hours}h ${minutes}m"
            } else {
                tvNextPrayerCountdown?.text = "in ${minutes}m"
            }

            updateProgressBar(nextPrayerIndex, now, sdf)

        } else {
            tvNextPrayerName?.text = "Fajr"
            label?.text = "UPCOMING PRAYER"
            tvPrevPrayerStatus?.text = "After Isha"
            adapter?.setActivePrayer("Fajr")

            if (timings!!.isNotEmpty()) {
                tvNextPrayerTime?.text = timings!![0]
            }

            try {
                val fajrTime = sdf.parse(timings!![0])
                val fajrCal = Calendar.getInstance()
                fajrCal.time = fajrTime
                fajrCal.add(Calendar.DATE, 1)
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

        val width = if (listView.width > 0) {
            listView.width
        } else {
            val metrics = resources.displayMetrics
            metrics.widthPixels - (36 * metrics.density).toInt()
        }

        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        if (params != null) {
            val dividerSpace = if (listAdapter.count > 1) listView.dividerHeight * (listAdapter.count - 1) else 0
            params.height = totalHeight + dividerSpace + listView.paddingTop + listView.paddingBottom
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
                    tableTimings2 = json.getJSONArray("May")
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
            params3.add(AbstractMap.SimpleEntry("date", date333))
            val json = jParser.nmakeHttpRequest(url_timings_3, "GET", params3)

            try {
                val success = json!!.getInt(TAG_SUCCESS)
                if (success == 1) {
                    tableTimings3 = json.getJSONArray("May")
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
                val success = json!!.getInt(TAG_SUCCESS)
                if (success == 1) {
                    marqueeText = ArrayList()
                    tableTimings4 = json.getJSONArray("Info")

                    for (i in 0 until tableTimings4!!.length()) {
                        val c = tableTimings4!!.getJSONObject(i)
                        marqueeText!!.add(c.getString("i_text"))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            pDialog!!.dismiss()
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
                val success = json!!.getInt(TAG_SUCCESS)
                if (success == 1) {
                    quranClassTimes = ArrayList()
                    tableTimings5 = json.getJSONArray("events")

                    for (i in 0 until tableTimings5!!.length()) {
                        val c = tableTimings5!!.getJSONObject(i)
                        quranClassTimes!!.add(c.getString("e_title"))
                        quranClassTimes!!.add(c.getString("e_time"))
                    }
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

            var sunriseValue = "--:--"
            var sunsetValue = "--:--"

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

                val localSunrise = convertUTCToLocalTime(sunriseUTC)
                val localSunset = convertUTCToLocalTime(sunsetUTC)

                if (localSunrise != "--:--") sunriseValue = localSunrise
                if (localSunset != "--:--") sunsetValue = localSunset

            } catch (e: Exception) {
                Log.e("SunriseSunsetAPI", "Unexpected parsing error", e)
            }

            if (sunriseText != null) sunriseText!!.text = sunriseValue
            if (sunsetText != null) sunsetText!!.text = sunsetValue
        }

        private fun convertUTCToLocalTime(utcTimeString: String?): String {

            if (utcTimeString == null || utcTimeString.trim { it <= ' ' }.isEmpty()) {
                return "--:--"
            }

            try {
                val timeFormat = SimpleDateFormat("h:mm:ss a", Locale.US)
                timeFormat.timeZone = TimeZone.getTimeZone("UTC")

                val parsedTime = timeFormat.parse(utcTimeString)

                val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                val timeCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                timeCal.time = parsedTime

                utcCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                utcCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                utcCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND))

                val fullUtcDate = utcCal.time

                val localFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
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
