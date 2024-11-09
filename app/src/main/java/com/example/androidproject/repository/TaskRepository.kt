package com.example.androidproject.repository

import android.util.Log
import androidx.compose.runtime.collectAsState
import com.example.androidproject.data.daos.CheckpointDao
import com.example.androidproject.data.daos.QuestDao
import com.example.androidproject.data.daos.TaskDao
import com.example.androidproject.data.models.TaskEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class TaskRepository(
    private val questDao: QuestDao,
    private val checkpointDao: CheckpointDao,
    private val taskDao: TaskDao
) {
    fun getCurrentTasks(): Flow<List<TaskEntity>> = taskDao.getCurrent()

    suspend fun markCompleted(task: TaskEntity) {
        checkpointDao.getById(task.checkpointId).collect {
            val checkpoint = it.first()
            checkpoint.completed = true
            checkpointDao.update(checkpoint)
        }
    }
}