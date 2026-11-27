package com.depu.vocabapp.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ReviewQueueTest {
    private val now = 10_000L

    private fun word(name: String, due: Long?) = VocabWord(word = name, nextReviewDate = due)

    @Test fun futureWordsAreExcluded() {
        val result = ReviewQueue.dueFirst(listOf(word("Future", now + 1), word("Due", now)), now)
        assertEquals(listOf("Due"), result.map { it.word })
    }

    @Test fun oldestDueDatesComeFirst() {
        val result = ReviewQueue.dueFirst(listOf(word("Recent", now - 20), word("Older", now - 200)), now)
        assertEquals(listOf("Older", "Recent"), result.map { it.word })
    }

    @Test fun unscheduledNewWordsAreFirstAndAlphabetical() {
        val result = ReviewQueue.dueFirst(listOf(word("Zebra", null), word("Apple", null), word("Timed", now - 1)), now)
        assertEquals(listOf("Apple", "Zebra", "Timed"), result.map { it.word })
    }

    @Test fun equalDueDatesUseAlphabeticalOrder() {
        val result = ReviewQueue.dueFirst(listOf(word("Zulu", now), word("alpha", now)), now)
        assertEquals(listOf("alpha", "Zulu"), result.map { it.word })
    }
}
