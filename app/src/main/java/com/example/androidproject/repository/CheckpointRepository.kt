package com.example.androidproject.repository

import com.example.androidproject.data.daos.CheckpointDao
import com.example.androidproject.data.models.CheckpointEntity

class CheckpointRepository(
    private val checkpointDao: CheckpointDao
) {
    // Marks checkpoint as completed
    suspend fun markCompleted(checkpoint: CheckpointEntity?) {
        checkpoint?.let {
            it.completed = true
            checkpointDao.update(it)
        }
    }
}