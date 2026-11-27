package com.depu.vocabapp.data

/** Select and order currently due words, oldest scheduled date first. New unscheduled words come first. */
object ReviewQueue {
    fun dueFirst(words: List<VocabWord>, now: Long): List<VocabWord> =
        words.asSequence()
            .filter { it.nextReviewDate == null || it.nextReviewDate <= now }
            .sortedWith(compareBy<VocabWord> { it.nextReviewDate ?: Long.MIN_VALUE }
                .thenBy { it.word.trim().lowercase() })
            .toList()
}
