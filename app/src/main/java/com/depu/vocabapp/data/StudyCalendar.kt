package com.depu.vocabapp.data

import java.util.Calendar

/** Local calendar boundaries for date-based study statistics. */
object StudyCalendar {
    fun startOfDay(timestamp: Long): Long = Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    fun sevenDayStart(timestamp: Long): Long = Calendar.getInstance().apply {
        timeInMillis = startOfDay(timestamp)
        add(Calendar.DAY_OF_YEAR, -6)
    }.timeInMillis
}
