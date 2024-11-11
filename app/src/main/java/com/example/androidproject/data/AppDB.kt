package com.example.androidproject.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidproject.App
import com.example.androidproject.data.daos.CheckpointDao
import com.example.androidproject.data.daos.QuestDao
import com.example.androidproject.data.daos.TaskDao
import com.example.androidproject.data.daos.AchievementDao
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.data.models.QuestEntity
import com.example.androidproject.data.models.TaskEntity
import com.example.androidproject.data.models.AchievementEntity

@Database(entities = [CheckpointEntity::class, TaskEntity::class, QuestEntity::class, AchievementEntity::class], version = 2, exportSchema = false)
abstract class AppDB : RoomDatabase() {

    abstract fun checkpointDao(): CheckpointDao
    abstract fun taskDao(): TaskDao
    abstract fun questDao(): QuestDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var Instance: AppDB? = null

        fun getDatabase(): AppDB {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    App.appContext,
                    AppDB::class.java,
                    "quest_app_database"
                )
                    .fallbackToDestructiveMigration()  // This will delete the database when the schema changes
                    .build().also { Instance = it }
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
        val taskDao = db.taskDao()
        val achievementDao = db.achievementDao()

        // Insert Quest Entities
        questDao.insert(QuestEntity(id = 1, description = "Helsinki Historical Locations", category = "History", current = true))
        questDao.insert(QuestEntity(id = 2, description = "Helsinki Tourist Attractions", category = "Attraction", current = false))
        questDao.insert(QuestEntity(id = 3, description = "Metropolia UAS Campuses", category = "Education", current = false))
        questDao.insert(QuestEntity(id = 4, description = "Arman's surroundings"))

        // Insert Checkpoint Entities
        val checkpoints1 = listOf(
            CheckpointEntity(id = 1, questId = 1, lat = 60.1699, long = 24.9384, name = "Helsinki Cathedral"),
            CheckpointEntity(id = 2, questId = 1, lat = 60.1609, long = 24.9522, name = "Suomenlinna Fortress")
        )
        checkpoints1.forEach { checkpoint ->
            checkpointDao.insert(checkpoint)
        }

        // Insert tasks (after inserting checkpoints to ensure foreign key integrity)
        val armanTasks = listOf(
            TaskEntity(id = 1, checkpointId = 1, description = "Do project"),
            TaskEntity(id = 2, checkpointId = 2, description = "Buy grocery")
        )
        armanTasks.forEach { task ->
            taskDao.insert(task) // Ensure checkpoints are already in the database
        }

        // Insert Achievements
        achievementDao.insert(AchievementEntity(name = "Route Completed", type = "Completion", status = "Unlocked"))
        achievementDao.insert(AchievementEntity(name = "Task Finished", type = "Task", status = "Locked"))
    }
}