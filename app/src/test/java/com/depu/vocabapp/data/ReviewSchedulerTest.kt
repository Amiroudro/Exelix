package com.depu.vocabapp.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ReviewSchedulerTest {
    @Test fun againResetsStreakAndSchedulesTenMinutes() {
        val now = 1_000_000L
        val result = ReviewScheduler.again(now)
        assertEquals(0, result.streak)
        assertEquals("Learning", result.status)
        assertEquals(now + 600_000L, result.nextReviewAt)
    }

    @Test fun rememberedUsesProgressiveIntervals() {
        val now = 1_000_000L
        val expectedDays = listOf(1, 3, 7, 14, 30)
        expectedDays.forEachIndexed { index, days ->
            val result = ReviewScheduler.remembered(index, now)
            assertEquals(index + 1, result.streak)
            assertEquals(now + days * 86_400_000L, result.nextReviewAt)
        }
    }

    @Test fun fifthSuccessMarksMastered() {
        assertEquals("Mastered", ReviewScheduler.remembered(4, 1L).status)
    }
    @Test fun intervalCapsAtThirtyDaysAfterFiveSuccesses() {
        val result = ReviewScheduler.remembered(5, 1_000L)
        assertEquals(6, result.streak)
        assertEquals("Mastered", result.status)
        assertEquals(1_000L + 30L * 86_400_000L, result.nextReviewAt)
    }

}
