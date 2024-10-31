package com.example.androidproject.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidproject.App
import com.example.androidproject.data.daos.CheckpointDao
import com.example.androidproject.data.daos.QuestDao
import com.example.androidproject.data.daos.TaskDao
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.data.models.QuestEntity
import com.example.androidproject.data.models.TaskEntity

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

    suspend fun fillWithTestData() {
        val db: AppDB = getDatabase()
        val questDao = db.questDao()
        val checkpointDao = db.checkpointDao()

        questDao.insert(QuestEntity(id = 1, description = "Helsinki Tour"))

        val checkpoints = listOf(
            CheckpointEntity(id = 1, questId = 1, lat = 60.1699, long = 24.9384, name = "Helsinki Cathedral"),
            CheckpointEntity(id = 2, questId = 1, lat = 60.1609, long = 24.9522, name = "Suomenlinna Fortress"),
            CheckpointEntity(id = 3, questId = 1, lat = 60.1708, long = 24.9426, name = "Market Square"),
            CheckpointEntity(id = 4, questId = 1, lat = 60.1870, long = 24.9210, name = "Sibelius Monument"),
            CheckpointEntity(id = 5, questId = 1, lat = 60.1719, long = 24.9414, name = "Esplanadi Park"),
            CheckpointEntity(id = 6, questId = 1, lat = 60.1756, long = 24.9389, name = "Ateneum Art Museum"),
            CheckpointEntity(id = 7, questId = 1, lat = 60.1844, long = 24.9250, name = "Temppeliaukio Church")
        )

        checkpoints.forEach { checkpoint ->
            checkpointDao.insert(checkpoint)
        }
    }
}