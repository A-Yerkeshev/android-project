package com.example.androidproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidproject.data.models.AchievementEntity
import com.example.androidproject.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AchievementViewModel(private val repository: AchievementRepository) : ViewModel() {
    val achievements: Flow<List<AchievementEntity>> = repository.getAllAchievements()

    fun addAchievement(achievement: AchievementEntity) {
        viewModelScope.launch { repository.insertAchievement(achievement) }
    }

    fun updateAchievementStatus(id: Int, status: String) {
        viewModelScope.launch { repository.updateAchievementStatus(id, status) }
    }
}
