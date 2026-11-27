package com.depu.vocabapp.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class StudyCalendarTest {
    private fun localDate(dayOffset: Int, hour: Int, minute: Int): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, dayOffset)
    }.timeInMillis

    @Test fun startOfDayIsMidnightLocalTime() {
        val stamp = localDate(0, 23, 59)
        val start = Calendar.getInstance().apply { timeInMillis = StudyCalendar.startOfDay(stamp) }
        assertEquals(0, start.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, start.get(Calendar.MINUTE))
    }

    @Test fun sevenDayWindowStartsSixCalendarDaysBeforeToday() {
        val today = localDate(0, 10, 0)
        val start = Calendar.getInstance().apply { timeInMillis = StudyCalendar.sevenDayStart(today) }
        val expected = Calendar.getInstance().apply { timeInMillis = StudyCalendar.startOfDay(today); add(Calendar.DAY_OF_YEAR, -6) }
        assertEquals(expected.get(Calendar.YEAR), start.get(Calendar.YEAR))
        assertEquals(expected.get(Calendar.DAY_OF_YEAR), start.get(Calendar.DAY_OF_YEAR))
    }

    @Test fun midnightChangesTheDayBoundary() {
        val before = localDate(0, 23, 59)
        val after = localDate(1, 0, 1)
        assertEquals(1, Calendar.getInstance().apply { timeInMillis = after }.get(Calendar.DAY_OF_YEAR) -
            Calendar.getInstance().apply { timeInMillis = before }.get(Calendar.DAY_OF_YEAR))
        assertEquals(StudyCalendar.startOfDay(after), localDate(1, 0, 0))
    }
}
