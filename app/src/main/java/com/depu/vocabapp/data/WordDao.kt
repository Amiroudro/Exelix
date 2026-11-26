package com.depu.vocabapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabDao {
    @Query("SELECT * FROM vocabulary ORDER BY word COLLATE NOCASE")
    fun observeAll(): Flow<List<VocabWord>>

    @Query("SELECT * FROM vocabulary WHERE word LIKE '%' || :query || '%' OR banglaMeaning LIKE '%' || :query || '%' OR englishDefinition LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY word COLLATE NOCASE")
    fun search(query: String): Flow<List<VocabWord>>

    @Query("SELECT * FROM vocabulary WHERE nextReviewDate IS NULL OR nextReviewDate <= :now ORDER BY COALESCE(nextReviewDate, 0), word COLLATE NOCASE")
    fun observeDue(now: Long = System.currentTimeMillis()): Flow<List<VocabWord>>

    @Query("SELECT COUNT(*) FROM vocabulary")
    fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM vocabulary WHERE masteryStatus = 'Mastered'")
    fun observeMasteredCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(word: VocabWord): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(word: VocabWord): Long

    @Update
    suspend fun update(word: VocabWord)

    @Delete
    suspend fun delete(word: VocabWord)

    @Insert
    suspend fun insertBatch(batch: ImportBatch): Long

    @Query("SELECT * FROM import_batches ORDER BY importedAt DESC")
    fun observeBatches(): Flow<List<ImportBatch>>
}
