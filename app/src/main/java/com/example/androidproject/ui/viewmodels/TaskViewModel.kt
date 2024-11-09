package com.example.androidproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidproject.data.AppDB
import com.example.androidproject.data.models.TaskEntity
import com.example.androidproject.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val database = AppDB.getDatabase()
    private val repository = TaskRepository(database.checkpointDao())

    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks

    init {

    }

    fun markCompleted(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markCompleted(task)
        }
    }
}