package com.example.androidproject.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidproject.App

@Database(entities = [CheckpointEntity::class, TaskEntity::class, QuestEntity::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {
    abstract fun checkpointDao(): CheckpointDao
    abstract fun taskDao(): TaskDao
    abstract fun questDao(): QuestDao

    companion object {
        @Volatile
        private var Instance: AppDB? = null

        fun getDatabase(): AppDB {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    App.appContext,
                    AppDB::class.java,
                    "quest_app_database"
                ).build().also { Instance = it }
            }
        }
    }
}