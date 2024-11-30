package com.example.androidproject.repository

import com.example.androidproject.data.daos.CheckpointDao
import com.example.androidproject.data.daos.QuestDao
import com.example.androidproject.data.daos.TaskDao
import com.example.androidproject.data.models.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val questDao: QuestDao,
    private val checkpointDao: CheckpointDao,
    private val taskDao: TaskDao
) {
    // Fetches all tasks for the current quest
    fun getCurrentTasks(): Flow<List<TaskEntity>> = taskDao.getCurrent()

    // Marks checkpoint, associated with the task, as completed
    suspend fun markCompleted(task: TaskEntity) {
        checkpointDao.getById(task.checkpointId).collect {
            val checkpoint = it.first()
            checkpoint.completed = true
            checkpointDao.update(checkpoint)
        }
    }
}