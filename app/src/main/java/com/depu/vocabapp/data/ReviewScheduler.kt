package com.depu.vocabapp.data

data class ReviewResult(
    val streak: Int,
    val status: String,
    val nextReviewAt: Long
)

object ReviewScheduler {
    private val intervalsDays = listOf(1, 3, 7, 14, 30)

    fun again(now: Long = System.currentTimeMillis()): ReviewResult =
        ReviewResult(streak = 0, status = "Learning", nextReviewAt = now + 10 * 60_000L)

    fun remembered(previousStreak: Int, now: Long = System.currentTimeMillis()): ReviewResult {
        val nextStreak = previousStreak + 1
        val days = intervalsDays[(nextStreak - 1).coerceIn(0, intervalsDays.lastIndex)]
        val status = when {
            nextStreak >= 5 -> "Mastered"
            nextStreak >= 2 -> "Learning"
            else -> "New"
        }
        return ReviewResult(nextStreak, status, now + days * 24 * 60 * 60_000L)
    }
}
