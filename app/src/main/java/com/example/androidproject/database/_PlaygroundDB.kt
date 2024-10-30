package com.example.androidproject.database

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class _PlaygroundDB {
    private val database: AppDB = AppDB.getDatabase()
    private val checkpointDao: CheckpointDao = database.checkpointDao()
    private val taskDao: TaskDao = database.taskDao()
    private val questDao: QuestDao = database.questDao()

    fun play() {
        GlobalScope.launch {
            val questId: Int = (questDao.insert(QuestEntity())).toInt()
            val checkpointId: Int = (checkpointDao.insert(CheckpointEntity(questId = questId, lat = 60.168490, long = 24.959860))).toInt()
            val taskId: Int = (taskDao.insert(TaskEntity(checkpointId = checkpointId))).toInt()
            questDao.unsetAllCurrent()

            Log.d("DBG", "Database playground didn't crash")
        }
    }
}