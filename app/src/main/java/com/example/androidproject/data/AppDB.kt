package com.example.androidproject.data

import android.content.Context
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

    suspend fun setCurrentIfNotExists() {
        val db: AppDB = getDatabase()
        val questDao = db.questDao()

        val currentQuestFlow = questDao.getCurrent()
        currentQuestFlow.collect { currentQuest ->
            if (currentQuest.firstOrNull() == null) {
                questDao.getAll().collect {
                    if (it.isNotEmpty()) {
                        val quest = it[0]
                        quest.current = true
                        questDao.update(quest)
                    }
                }
            }
        }
    }

    suspend fun fillWithTestData() {
        val db: AppDB = getDatabase()
        val questDao = db.questDao()
        val checkpointDao = db.checkpointDao()

        questDao.insert(
            QuestEntity(
                id = 1,
                description = "Helsinki Historical Locations",
                category = "History",
                current = true
            )
        )

        questDao.insert(
            QuestEntity(
                id = 2,
                description = "Helsinki Tourist Attractions",
                category = "Attraction",
                current = false
            )
        )

        questDao.insert(
            QuestEntity(
                id = 3,
                description = "Metropolia UAS Campuses",
                category = "Education",
                current = false
            )
        )

        questDao.insert(
            QuestEntity(
                id = 4,
                description = "Arman's surroundings"
            )
        )

        val checkpoints1 = listOf(
            CheckpointEntity(id = 1, questId = 1, lat = 60.1699, long = 24.9384, name = "Helsinki Cathedral"),
            CheckpointEntity(id = 2, questId = 1, lat = 60.1609, long = 24.9522, name = "Suomenlinna Fortress"),
            CheckpointEntity(id = 3, questId = 2, lat = 60.1708, long = 24.9426, name = "Market Square"),
            CheckpointEntity(id = 4, questId = 2, lat = 60.1870, long = 24.9210, name = "Sibelius Monument"),
            CheckpointEntity(id = 5, questId = 2, lat = 60.1719, long = 24.9414, name = "Esplanadi Park"),
            CheckpointEntity(id = 6, questId = 2, lat = 60.1756, long = 24.9389, name = "Ateneum Art Museum"),
            CheckpointEntity(id = 7, questId = 1, lat = 60.1844, long = 24.9250, name = "Temppeliaukio Church")
        )

        checkpoints1.forEach { checkpoint ->
            checkpointDao.insert(checkpoint)
        }

        questDao.insert(QuestEntity(id = 2, description = "Helsinki Tour - Route 2"))

        val checkpoints2 = listOf(
            CheckpointEntity(id = 8, questId = 2, lat = 60.1699, long = 24.9384, name = "Helsinki Cathedral"),
            CheckpointEntity(id = 9, questId = 2, lat = 60.1609, long = 24.9522, name = "Suomenlinna Fortress"),
            CheckpointEntity(id = 10, questId = 2, lat = 60.1708, long = 24.9426, name = "Market Square"),
            CheckpointEntity(id = 11, questId = 2, lat = 60.1870, long = 24.9210, name = "Sibelius Monument"),
            CheckpointEntity(id = 12, questId = 2, lat = 60.1719, long = 24.9414, name = "Esplanadi Park"),
            CheckpointEntity(id = 13, questId = 2, lat = 60.1756, long = 24.9389, name = "Ateneum Art Museum"),
            CheckpointEntity(id = 14, questId = 2, lat = 60.1844, long = 24.9250, name = "Temppeliaukio Church")
        )

        checkpoints2.forEach { checkpoint ->
            checkpointDao.insert(checkpoint)
        }

        val metropoliaCheckpoints = listOf(
            CheckpointEntity(id = 15, questId = 3, lat = 60.2206, long = 24.8056, name = "Myllypuro Campus"),
            CheckpointEntity(id = 16, questId = 3, lat = 60.2026, long = 24.9342, name = "Karamalmi Campus"),
            CheckpointEntity(id = 17, questId = 3, lat = 60.2230, long = 24.7582, name = "Leppävaara Campus"),
            CheckpointEntity(id = 18, questId = 3, lat = 60.1691, long = 24.9402, name = "Hämeentie Campus")
        )
        metropoliaCheckpoints.forEach { checkpoint ->
            checkpointDao.insert(checkpoint)
        }

        val armanCheckpoints = listOf(
            CheckpointEntity(id = 19, questId = 4, lat = 60.235610, long = 25.006100, name = "Home"),
            CheckpointEntity(id = 20, questId = 4, lat = 60.234281, long = 25.011228, name = "S-market Pihlajamäki"),
        )
        armanCheckpoints.forEach { checkpoint ->
            checkpointDao.insert(checkpoint)
        }

        val armanTasks = listOf(
            TaskEntity(id = 1, checkpointId = 19, description = "Do project"),
            TaskEntity(id = 2, checkpointId = 20, description = "Buy grocery"),
        )
        armanTasks.forEach { task ->
            taskDao().insert(task)
        }
    }
}