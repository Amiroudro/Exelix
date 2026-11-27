package com.depu.vocabapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Local review event log for reconstructed study-history features. */
@Entity(tableName = "study_history")
data class StudyHistoryEvent(
    @PrimaryKey(autoGenerate = true) val eventId: Long = 0,
    val vocabularyId: Long,
    val word: String,
    val remembered: Boolean,
    val reviewedAt: Long = System.currentTimeMillis()
)
