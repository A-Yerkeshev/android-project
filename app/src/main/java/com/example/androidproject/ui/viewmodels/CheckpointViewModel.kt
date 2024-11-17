package com.example.androidproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidproject.data.AppDB
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.data.models.TaskEntity
import com.example.androidproject.repository.CheckpointRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CheckpointViewModel : ViewModel() {
    private val db = AppDB.getDatabase()
    private val checkpointDao = db.checkpointDao()
    private val repository = CheckpointRepository(checkpointDao)

    fun getAll(questId: Int): Flow<List<CheckpointEntity>> {
        return checkpointDao.getAll(questId)
    }

    fun markCompleted(checkpoint: CheckpointEntity?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markCompleted(checkpoint)
        }
    }
}