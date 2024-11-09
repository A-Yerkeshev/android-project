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
    private val repository = TaskRepository(database.questDao(), database.checkpointDao(), database.taskDao())

    private val _currentTasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val currentTasks: StateFlow<List<TaskEntity>> = _currentTasks

    init {
        getCurrentTasks()
    }

    private fun getCurrentTasks() {
        viewModelScope.launch {
            repository.getCurrentTasks().collect { tasks ->
                _currentTasks.value = tasks
            }
        }
    }

    fun markCompleted(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markCompleted(task)
        }
    }
}