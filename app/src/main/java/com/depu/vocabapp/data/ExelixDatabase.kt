package com.depu.vocabapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [VocabWord::class, ImportBatch::class], version = 1, exportSchema = true)
abstract class ExelixDatabase : RoomDatabase() {
    abstract fun vocabDao(): VocabDao
}
