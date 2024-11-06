package com.example.androidproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.androidproject.data.AppDB
import com.example.androidproject.data.models.CheckpointEntity
import kotlinx.coroutines.flow.Flow

class CheckpointViewModel : ViewModel() {
    private val db = AppDB.getDatabase()
    private val checkpointDao = db.checkpointDao()

    fun getAll(questId: Int): Flow<List<CheckpointEntity>> {
        return checkpointDao.getAll(questId)
    }
}