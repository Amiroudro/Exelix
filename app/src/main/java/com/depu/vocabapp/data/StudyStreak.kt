package com.depu.vocabapp.data

/** Calendar-day streak calculation. If today has no activity, a streak from yesterday remains active. */
object StudyStreak {
    fun calculate(reviewTimestamps: List<Long>, now: Long = System.currentTimeMillis()): Int {
        if (reviewTimestamps.isEmpty()) return 0
        fun dayKey(timestamp: Long): Int = java.util.Calendar.getInstance().apply {
            timeInMillis = timestamp
        }.let { it.get(java.util.Calendar.YEAR) * 1000 + it.get(java.util.Calendar.DAY_OF_YEAR) }
        val activeDays = reviewTimestamps.map(::dayKey).toSet()
        val today = dayKey(now)
        val yesterday = java.util.Calendar.getInstance().apply {
            timeInMillis = now
            add(java.util.Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis.let(::dayKey)
        var cursor = if (today in activeDays) now else if (yesterday in activeDays) {
            java.util.Calendar.getInstance().apply { timeInMillis = now; add(java.util.Calendar.DAY_OF_YEAR, -1) }.timeInMillis
        } else return 0
        var streak = 0
        while (dayKey(cursor) in activeDays) {
            streak++
            cursor = java.util.Calendar.getInstance().apply {
                timeInMillis = cursor
                add(java.util.Calendar.DAY_OF_YEAR, -1)
            }.timeInMillis
        }
        return streak
    }
}
