package com.adorastudios.tomorrowapp.domain

import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import java.util.TimeZone

const val MILLIS_TO_DAYS = 1000 * 60 * 60 * 24

fun currentDay(): Int {
    val tz = TimeZone.getDefault()
    val dst = if (ZoneId.systemDefault().rules.isDaylightSavings(
            ZonedDateTime.now(ZoneId.systemDefault()).toInstant(),
        )
    ) {
        tz.dstSavings
    } else {
        0
    }
    return ((System.currentTimeMillis() + tz.rawOffset + dst) / MILLIS_TO_DAYS).toInt()
}

fun Int.getDate(): String {
    val df = Date(this.toLong() * MILLIS_TO_DAYS)
    return SimpleDateFormat.getDateInstance().format(df)
}
