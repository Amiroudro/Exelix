package com.depu.vocabapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WordEntity::class], version = 1, exportSchema = false)
abstract class ExelixDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}
