package com.example.androidproject.repository

import com.example.androidproject.data.daos.CheckpointDao
import com.example.androidproject.data.models.TaskEntity

class TaskRepository(
    private val checkpointDao: CheckpointDao
) {
    suspend fun markCompleted(task: TaskEntity) {
        checkpointDao.getById(task.checkpointId).collect {
            val checkpoint = it.first()
            checkpoint.completed = true
            checkpointDao.update(checkpoint)
        }
    }
}