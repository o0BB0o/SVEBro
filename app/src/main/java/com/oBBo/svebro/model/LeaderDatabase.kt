package com.oBBo.svebro.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Leader::class], version = 1, exportSchema = false)
abstract class LeaderDatabase:RoomDatabase() {
    abstract fun LeaderDao(): LeaderDao

    companion object {
        @Volatile
        private var INSTANCE: LeaderDatabase? = null

        fun getDatabase(context: Context): LeaderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LeaderDatabase::class.java,
                    "leader_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}