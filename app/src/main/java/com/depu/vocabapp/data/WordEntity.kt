package com.depu.vocabapp.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Reconstructed from embedded APK schema evidence; this is not original source code. */
@Entity(
    tableName = "vocabulary",
    indices = [
        Index(value = ["normalizedWord"], unique = true),
        Index(value = ["dateAdded"]),
        Index(value = ["difficultyLevel"]),
        Index(value = ["examSourceRef"]),
        Index(value = ["importBatchId"]),
        Index(value = ["nextReviewDate"]),
        Index(value = ["masteryStatus"]),
        Index(value = ["incorrectStreak"])
    ]
)
data class VocabWord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val word: String,
    val partOfSpeech: String = "",
    val difficultyLevel: String = "Unknown",
    val banglaMeaning: String = "",
    val englishDefinition: String = "",
    val synonym: String? = null,
    val antonym: String? = null,
    val collocation: String? = null,
    val exampleSentence: String? = null,
    val examSourceRef: String? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val masteryStatus: String = "New",
    val correctStreak: Int = 0,
    val nextReviewDate: Long? = null,
    val incorrectStreak: Int = 0,
    val normalizedWord: String = word.trim().lowercase(),
    val tags: String = "",
    val importBatchId: Long? = null
)

@Entity(tableName = "import_batches")
data class ImportBatch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileName: String,
    val importedAt: Long = System.currentTimeMillis(),
    val wordCount: Int
)
