package com.depu.vocabapp.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile private var instance: ExelixDatabase? = null
    fun get(context: Context): ExelixDatabase = instance ?: synchronized(this) {
        instance ?: Room.databaseBuilder(context.applicationContext, ExelixDatabase::class.java, "exelix_rebuild.db")
            // Use a separate reconstruction database; never overwrite the original app data.
            .build().also { instance = it }
    }
}
