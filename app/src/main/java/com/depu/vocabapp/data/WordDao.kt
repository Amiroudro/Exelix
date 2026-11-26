package com.depu.vocabapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY word COLLATE NOCASE")
    fun observeAll(): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE nextReviewAt <= :now OR nextReviewAt = 0 ORDER BY createdAt")
    fun observeDue(now: Long): Flow<List<WordEntity>>

    @Insert
    suspend fun insert(word: WordEntity): Long

    @Update
    suspend fun update(word: WordEntity)

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT COUNT(*) FROM words")
    suspend fun count(): Int
}
