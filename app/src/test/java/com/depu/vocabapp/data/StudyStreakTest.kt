package com.depu.vocabapp.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class StudyStreakTest {
    private fun day(offset: Int, hour: Int = 12): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 12); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, offset)
        set(Calendar.HOUR_OF_DAY, hour)
    }.timeInMillis

    @Test fun noActivityReturnsZero() {
        assertEquals(0, StudyStreak.calculate(emptyList(), day(0)))
    }

    @Test fun streakContinuesWhenTodayNotYetStudied() {
        assertEquals(2, StudyStreak.calculate(listOf(day(-2), day(-1)), day(0)))
    }

    @Test fun todayActivityStartsStreakToday() {
        assertEquals(2, StudyStreak.calculate(listOf(day(-1), day(0)), day(0)))
    }

    @Test fun gapBreaksStreak() {
        assertEquals(1, StudyStreak.calculate(listOf(day(-3), day(0)), day(0)))
    }

    @Test fun futureActivityDoesNotStartStreak() {
        assertEquals(0, StudyStreak.calculate(listOf(day(1)), day(0)))
    }
}
