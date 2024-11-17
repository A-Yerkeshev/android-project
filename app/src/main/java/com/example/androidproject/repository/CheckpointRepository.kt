package com.example.androidproject.repository

import android.util.Log
import com.example.androidproject.data.daos.CheckpointDao
import com.example.androidproject.data.daos.TaskDao
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.data.models.TaskEntity
import kotlinx.coroutines.flow.Flow

class CheckpointRepository(
    private val checkpointDao: CheckpointDao
) {
    suspend fun markCompleted(checkpoint: CheckpointEntity?) {
        checkpoint?.let {
            it.completed = true
            checkpointDao.update(it)
            checkpointDao.getById(it.id).collect{
                Log.d("DBG", it.toString())
            }
        }
    }
}