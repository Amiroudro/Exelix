package com.depu.vocabapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val word: String,
    val meaning: String,
    val definition: String = "",
    val partOfSpeech: String = "",
    val example: String = "",
    val synonyms: String = "",
    val antonyms: String = "",
    val collocations: String = "",
    val source: String = "",
    val tags: String = "",
    val status: String = "New",
    val streak: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val nextReviewAt: Long = 0L
)
