package com.depu.vocabapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

data class ImportMergeResult(val added: Int, val duplicates: Int)

@Dao
interface VocabDao {
    @Insert
    suspend fun insertHistory(event: StudyHistoryEvent): Long

    @Query("SELECT * FROM study_history ORDER BY reviewedAt DESC LIMIT 200")
    fun observeHistory(): Flow<List<StudyHistoryEvent>>

    @Query("SELECT COUNT(*) FROM study_history WHERE reviewedAt >= :since")
    fun observeReviewCountSince(since: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM study_history WHERE remembered = 1 AND reviewedAt >= :since")
    fun observeRememberedCountSince(since: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM study_history")
    fun observeAllReviewCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM study_history WHERE remembered = 1")
    fun observeAllRememberedCount(): Flow<Int>

    @Query("SELECT reviewedAt FROM study_history ORDER BY reviewedAt DESC")
    fun observeReviewTimestamps(): Flow<List<Long>>

    @Query("SELECT * FROM study_history WHERE reviewedAt >= :since ORDER BY reviewedAt DESC")
    fun observeHistorySince(since: Long): Flow<List<StudyHistoryEvent>>

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


    @Transaction
    suspend fun mergeImport(fileName: String, words: List<VocabWord>): ImportMergeResult {
        val batchId = insertBatch(ImportBatch(fileName = fileName, wordCount = 0))
        var added = 0
        var duplicates = 0
        words.forEach { word ->
            val id = insertIgnore(word.copy(importBatchId = batchId))
            if (id == -1L) duplicates++ else added++
        }
        updateBatchWordCount(batchId, added)
        return ImportMergeResult(added = added, duplicates = duplicates)
    }

    @Query("UPDATE import_batches SET wordCount = :count WHERE id = :batchId")
    suspend fun updateBatchWordCount(batchId: Long, count: Int)


    @Query("SELECT * FROM import_batches ORDER BY importedAt DESC")
    fun observeBatches(): Flow<List<ImportBatch>>
}
