package com.example.androidproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidproject.repository.QuestRepository

class QuestViewModelFactory(private val repository: QuestRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestViewModel::class.java))
            return QuestViewModel() as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}