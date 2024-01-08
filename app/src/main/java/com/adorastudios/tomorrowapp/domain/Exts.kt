package com.adorastudios.tomorrowapp.domain

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import java.util.SimpleTimeZone
import java.util.TimeZone

const val MILLIS_TO_DAYS = 1000 * 60 * 60 * 24
const val MILLIS_TO_MINUTES: Long = 1000 * 60

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

fun currentTime(): Long {
    val tz = TimeZone.getDefault()
    val dst = if (ZoneId.systemDefault().rules.isDaylightSavings(
            ZonedDateTime.now(ZoneId.systemDefault()).toInstant(),
        )
    ) {
        tz.dstSavings
    } else {
        0
    }
    return System.currentTimeMillis() + tz.rawOffset + dst
}

fun Int.getDate(): String {
    val df = Date(this.toLong() * MILLIS_TO_DAYS)
    return SimpleDateFormat.getDateInstance().format(df)
}

fun Int.getTime(): String {
    val df = Date(this * MILLIS_TO_MINUTES)
    val simpleDateFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
    simpleDateFormat.timeZone = SimpleTimeZone(0, "UTC")
    return simpleDateFormat.format(df)
}
