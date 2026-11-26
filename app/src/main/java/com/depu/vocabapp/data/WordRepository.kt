package com.depu.vocabapp.data

import kotlinx.coroutines.flow.Flow

class WordRepository(private val dao: WordDao) {
    val words: Flow<List<WordEntity>> = dao.observeAll()
    fun due(now: Long = System.currentTimeMillis()): Flow<List<WordEntity>> = dao.observeDue(now)
    suspend fun add(word: WordEntity) = dao.insert(word)
    suspend fun update(word: WordEntity) = dao.update(word)
    suspend fun delete(id: Long) = dao.delete(id)
}
