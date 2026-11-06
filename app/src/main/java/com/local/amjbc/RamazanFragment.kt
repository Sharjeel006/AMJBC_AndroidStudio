package com.local.amjbc

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.local.amjbc.model.RamzanDayUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class RamazanFragment : Fragment() {

    // Active Ramadan Views
    private lateinit var llActiveRamadanSection: LinearLayout
    private lateinit var tvHeroDate: TextView
    private lateinit var tvHeroDayCount: TextView
    private lateinit var tvHeroHijriDate: TextView
    private lateinit var tvHeroSehrTime: TextView
    private lateinit var tvHeroIftarTime: TextView
    private lateinit var pbFastingProgress: ProgressBar
    private lateinit var tvFastingStatus: TextView
    private lateinit var tvFastingCountdown: TextView
    private lateinit var btnFullCalendar: TextView
    private lateinit var rvSchedule: RecyclerView

    // Before/After Ramadan Views
    private lateinit var llBeforeAfterSection: LinearLayout
    private lateinit var tvCountdownHeader: TextView
    private lateinit var tvCountdownNumber: TextView
    private lateinit var tvExpectedDate: TextView
    private lateinit var swMoonSightingAlert: SwitchCompat

    private lateinit var pbLoading: ProgressBar

    private val ramzanAdapter = RamzanAdapter()
    private var allScheduleItems: List<RamzanDayUiModel> = emptyList()
    private var isShowingFullCalendar = false

    private val PREFS_NAME = "RamadanPrefs"
    private val PREF_ALERT_KEY = "MoonSightingAlert"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_ramazan2, container, false)
        setHasOptionsMenu(true)

        initViews(rootView)
        setupRecyclerView()
        setupMoonSightingAlert()
        fetchRamzanData()

        return rootView
    }

    private fun initViews(rootView: View) {
        llActiveRamadanSection = rootView.findViewById(R.id.ll_active_ramadan_section)
        tvHeroDate = rootView.findViewById(R.id.tv_hero_date)
        tvHeroDayCount = rootView.findViewById(R.id.tv_hero_day_count)
        tvHeroHijriDate = rootView.findViewById(R.id.tv_hero_hijri_date)
        tvHeroSehrTime = rootView.findViewById(R.id.tv_hero_sehr_time)
        tvHeroIftarTime = rootView.findViewById(R.id.tv_hero_iftar_time)
        pbFastingProgress = rootView.findViewById(R.id.pb_fasting_progress)
        tvFastingStatus = rootView.findViewById(R.id.tv_fasting_status)
        tvFastingCountdown = rootView.findViewById(R.id.tv_fasting_countdown)
        btnFullCalendar = rootView.findViewById(R.id.btn_full_calendar)
        rvSchedule = rootView.findViewById(R.id.rv_ramzan_schedule)

        llBeforeAfterSection = rootView.findViewById(R.id.ll_before_after_section)
        tvCountdownHeader = rootView.findViewById(R.id.tv_countdown_header)
        tvCountdownNumber = rootView.findViewById(R.id.tv_countdown_number)
        tvExpectedDate = rootView.findViewById(R.id.tv_expected_date)
        swMoonSightingAlert = rootView.findViewById(R.id.sw_moon_sighting_alert)

        pbLoading = rootView.findViewById(R.id.pb_loading)

        btnFullCalendar.setOnClickListener {
            isShowingFullCalendar = !isShowingFullCalendar
            btnFullCalendar.text = if (isShowingFullCalendar) "< Upcoming days" else "Full calendar >"
            val todayIdx = allScheduleItems.indexOfFirst { it.isToday }
            updateScheduleList(if (todayIdx != -1) todayIdx else 0)
        }
    }

    private fun setupMoonSightingAlert() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isAlertOn = prefs.getBoolean(PREF_ALERT_KEY, true)
        swMoonSightingAlert.isChecked = isAlertOn

        swMoonSightingAlert.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(PREF_ALERT_KEY, isChecked).apply()
            val msg = if (isChecked) "Moon sighting alerts enabled" else "Moon sighting alerts disabled"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        rvSchedule.layoutManager = LinearLayoutManager(context)
        rvSchedule.adapter = ramzanAdapter
    }

    private fun fetchRamzanData() {
        pbLoading.visibility = View.VISIBLE
        llActiveRamadanSection.visibility = View.GONE
        llBeforeAfterSection.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val jsonString = URL("https://amj-bc.com/amjbc/fetch_ramzan.php").readText()
                val jsonObject = JSONObject(jsonString)
                val success = jsonObject.optInt("success", 0)

                if (success == 1) {
                    val ramzanArray = jsonObject.optJSONArray("Ramzan")
                    val items = mutableListOf<RamzanDayUiModel>()

                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val outputFormat = SimpleDateFormat("EEE, MMM d", Locale.US)
                    val hijriFormatter = DateTimeFormatter.ofPattern("d MMMM y", Locale.ENGLISH)
                    val todayDateStr = LocalDate.now().toString()

                    var todayIndex = -1

                    if (ramzanArray != null) {
                        for (i in 0 until ramzanArray.length()) {
                            val obj = ramzanArray.getJSONObject(i)
                            val rDated = obj.optString("r_dated")
                            val sehri = obj.optString("sehri")
                            val aftari = obj.optString("aftari")

                            var formattedDate = rDated
                            var hijriStr = ""
                            var isToday = false

                            try {
                                val parsedDate = inputFormat.parse(rDated)
                                if (parsedDate != null) {
                                    formattedDate = outputFormat.format(parsedDate)
                                }

                                val localDate = LocalDate.parse(rDated)
                                val hijrahDate = HijrahDate.from(localDate)
                                hijriStr = "${hijrahDate.format(hijriFormatter)} AH"

                                if (rDated == todayDateStr) {
                                    isToday = true
                                    todayIndex = i
                                }
                            } catch (e: Exception) {
                                Log.e("RamazanFragment", "Error parsing date: $rDated", e)
                            }

                            items.add(
                                RamzanDayUiModel(
                                    dayNumber = i + 1,
                                    originalDateStr = rDated,
                                    formattedDate = formattedDate,
                                    hijriDateStr = hijriStr,
                                    sehriTime = sehri,
                                    iftariTime = aftari,
                                    isToday = isToday
                                )
                            )
                        }
                    }

                    val isCurrentlyInRamadan = (todayIndex != -1)

                    withContext(Dispatchers.Main) {
                        allScheduleItems = items
                        pbLoading.visibility = View.GONE

                        if (isCurrentlyInRamadan) {
                            // Current date falls within Ramadan schedule -> Show Active Schedule
                            llActiveRamadanSection.visibility = View.VISIBLE
                            llBeforeAfterSection.visibility = View.GONE

                            bindHeroCard(items[todayIndex], items.size)
                            updateScheduleList(todayIndex)
                        } else {
                            // Current date is outside Ramadan -> Show Countdown & Info
                            llActiveRamadanSection.visibility = View.GONE
                            llBeforeAfterSection.visibility = View.VISIBLE

                            if (items.isNotEmpty()) {
                                bindBeforeAfterCard(items.first())
                            }
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        pbLoading.visibility = View.GONE
                        Toast.makeText(context, "Failed to load Ramadan data", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("RamazanFragment", "Network error loading Ramadan schedule", e)
                withContext(Dispatchers.Main) {
                    pbLoading.visibility = View.GONE
                    Toast.makeText(context, "Network error loading Ramadan schedule", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindHeroCard(todayModel: RamzanDayUiModel, totalDays: Int) {
        tvHeroDate.text = "TODAY • ${todayModel.formattedDate.uppercase(Locale.US)}"
        tvHeroDayCount.text = "Day ${todayModel.dayNumber} of $totalDays"
        tvHeroHijriDate.text = todayModel.hijriDateStr
        tvHeroSehrTime.text = todayModel.sehriTime
        tvHeroIftarTime.text = todayModel.iftariTime

        calculateFastingProgress(todayModel.sehriTime, todayModel.iftariTime)
    }

    private fun bindBeforeAfterCard(firstDayModel: RamzanDayUiModel) {
        try {
            val today = LocalDate.now()
            val startDate = LocalDate.parse(firstDayModel.originalDateStr)
            val daysUntil = ChronoUnit.DAYS.between(today, startDate)

            val hijrahDate = HijrahDate.from(startDate)
            val hijriYear = hijrahDate.get(java.time.temporal.ChronoField.YEAR)

            tvCountdownHeader.text = "RAMADAN $hijriYear BEGINS IN"

            if (daysUntil > 0) {
                tvCountdownNumber.text = daysUntil.toString()
            } else if (daysUntil == 0L) {
                tvCountdownNumber.text = "0"
            } else {
                tvCountdownNumber.text = Math.abs(daysUntil).toString()
                tvCountdownHeader.text = "RAMADAN $hijriYear COMPLETED"
            }

            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val expectedFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.US)
            val parsedStart = inputFormat.parse(firstDayModel.originalDateStr)
            val formattedExpected = if (parsedStart != null) expectedFormat.format(parsedStart) else firstDayModel.formattedDate

            tvExpectedDate.text = "Expected $formattedExpected"
        } catch (e: Exception) {
            Log.e("RamazanFragment", "Error binding before/after card", e)
        }
    }

    private fun calculateFastingProgress(sehrTimeStr: String, iftarTimeStr: String) {
        try {
            val sdf = SimpleDateFormat("h:mm a", Locale.US)
            val sehrDate = sdf.parse(sehrTimeStr) ?: return
            val iftarDate = sdf.parse(iftarTimeStr) ?: return

            val now = Calendar.getInstance()
            val sehrCal = Calendar.getInstance().apply {
                time = sehrDate
                set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            }
            val iftarCal = Calendar.getInstance().apply {
                time = iftarDate
                set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            }

            val nowMillis = now.timeInMillis
            val sehrMillis = sehrCal.timeInMillis
            val iftarMillis = iftarCal.timeInMillis

            when {
                nowMillis < sehrMillis -> {
                    val diff = sehrMillis - nowMillis
                    val hours = diff / (1000 * 60 * 60)
                    val mins = (diff / (1000 * 60)) % 60
                    tvFastingStatus.text = "Sehr ends"
                    tvFastingCountdown.text = "Sehr ends in ${hours}h ${mins}m"
                    pbFastingProgress.progress = 15
                }
                nowMillis in sehrMillis..iftarMillis -> {
                    val diff = iftarMillis - nowMillis
                    val hours = diff / (1000 * 60 * 60)
                    val mins = (diff / (1000 * 60)) % 60
                    tvFastingStatus.text = "Fasting"
                    tvFastingCountdown.text = "Iftar in ${hours}h ${mins}m"

                    val totalFastingDuration = iftarMillis - sehrMillis
                    val elapsed = nowMillis - sehrMillis
                    val progress = if (totalFastingDuration > 0) {
                        (elapsed.toFloat() / totalFastingDuration.toFloat() * 100).toInt()
                    } else 50
                    pbFastingProgress.progress = progress.coerceIn(0, 100)
                }
                else -> {
                    tvFastingStatus.text = "Fasting completed"
                    tvFastingCountdown.text = "Iftar completed"
                    pbFastingProgress.progress = 100
                }
            }
        } catch (e: Exception) {
            Log.e("RamazanFragment", "Error calculating progress", e)
            tvFastingStatus.text = "Fasting"
            tvFastingCountdown.text = ""
            pbFastingProgress.progress = 50
        }
    }

    private fun updateScheduleList(todayIndex: Int) {
        if (allScheduleItems.isEmpty()) return

        val displayList = if (isShowingFullCalendar) {
            allScheduleItems
        } else {
            val startIndex = if (todayIndex != -1) todayIndex else 0
            val endIndex = (startIndex + 6).coerceAtMost(allScheduleItems.size)
            allScheduleItems.subList(startIndex, endIndex)
        }

        ramzanAdapter.submitList(displayList)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        if (menu.findItem(R.id.suggest_event) != null) {
            menu.findItem(R.id.suggest_event).isVisible = false
            menu.findItem(R.id.datepicker)?.isVisible = false
        }
        super.onPrepareOptionsMenu(menu)
    }
}