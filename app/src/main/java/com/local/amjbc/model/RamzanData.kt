package com.local.amjbc.model

data class RamzanDayItem(
    val r_dated: String,
    val sehri: String,
    val aftari: String
)

data class RamzanDayUiModel(
    val dayNumber: Int,
    val originalDateStr: String,
    val formattedDate: String,
    val hijriDateStr: String,
    val sehriTime: String,
    val iftariTime: String,
    val isToday: Boolean
)
