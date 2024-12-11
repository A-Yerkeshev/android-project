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
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Database(entities = [CheckpointEntity::class, TaskEntity::class, QuestEntity::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {

    abstract fun checkpointDao(): CheckpointDao
    abstract fun taskDao(): TaskDao
    abstract fun questDao(): QuestDao

    companion object {
        @Volatile
        private var Instance: AppDB? = null

        // Ensures that only one instance of the database is created
        fun getDatabase(): AppDB {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    App.appContext,
                    AppDB::class.java,
                    "quest_app_database"
                )
                .fallbackToDestructiveMigration()
                .build().also { Instance = it }
            }
        }
    }

    // If this is the first time user launches the app - set the first quest as current.
    // Otherwise, current quest is the one user selected last time.
    suspend fun setCurrentIfNotExists() {
        val db: AppDB = getDatabase()
        val questDao = db.questDao()

        val currentQuest = questDao.getCurrent().firstOrNull()
        if (currentQuest == null) {
            val allQuests = questDao.getAll().firstOrNull()
            if (!allQuests.isNullOrEmpty()) {
                    val quest = allQuests[0]
                    quest.current = true
                    questDao.update(quest)
            }
        }
    }

    // Fill the database with some test data
    suspend fun fillWithTestData() {
        val db: AppDB = getDatabase()  // Get the database instance
        val questDao = db.questDao()   // Get the QuestDao instance
        val checkpointDao = db.checkpointDao() // Get the CheckpointDao instance
        val taskDao = db.taskDao()     // Get the TaskDao instance

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        val currentDate = LocalDateTime.now().format(formatter)


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

        questDao.insert(
            QuestEntity(
                id = 5,
                description = "Completed Quest Example",
                category = "Test",
                current = false,
                completedAt = currentDate
            )
        )

        questDao.insert(
            QuestEntity(
                id = 6,
                description = "Coffee & Cake Run",
                category = "Test2",
                current = false,
                completedAt = currentDate
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
            CheckpointEntity(id = 15, questId = 3, lat = 60.2235, long = 25.0784, name = "Myllypuro Campus"),
            CheckpointEntity(id = 16, questId = 3, lat = 60.2241, long = 24.7585, name = "Karamalmi Campus"),
            CheckpointEntity(id = 17, questId = 3, lat = 60.2589, long = 24.8452, name = "Myyrmäki Campus"),
            CheckpointEntity(id = 18, questId = 3, lat = 60.2100, long = 24.9767, name = "Arabia Campus")
        )
        metropoliaCheckpoints.forEach { checkpoint ->
            checkpointDao.insert(checkpoint)
        }

        val armanCheckpoints = listOf(
            CheckpointEntity(id = 19, questId = 4, lat = 60.235610, long = 25.006100, name = "Home"),
            CheckpointEntity(id = 20, questId = 4, lat = 60.234281, long = 25.011228, name = "S-market Pihlajamäki", completed = true),
            CheckpointEntity(id = 21, questId = 4, lat = 60.237243, long = 24.999844, name = "Helsingin uusi yhteiskoulu", completed = true)
        )
        armanCheckpoints.forEach { checkpoint ->
            checkpointDao.insert(checkpoint)
        }

        val armanTasks = listOf(
            TaskEntity(id = 1, checkpointId = 19, description = "Do project"),
            TaskEntity(id = 2, checkpointId = 20, description = "Buy grocery"),
            TaskEntity(id = 3, checkpointId = 21, description = "Fight with children"),
        )
        armanTasks.forEach { task ->
            taskDao.insert(task)
        }





    }
}