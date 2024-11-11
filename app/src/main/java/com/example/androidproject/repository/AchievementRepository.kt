package com.example.androidproject.repository

import com.example.androidproject.data.daos.AchievementDao
import com.example.androidproject.data.models.AchievementEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AchievementRepository @Inject constructor(private val achievementDao: AchievementDao) {

    fun getAllAchievements(): Flow<List<AchievementEntity>> {
        return achievementDao.getAllAchievements()
    }

    suspend fun insertAchievement(achievement: AchievementEntity) {
        achievementDao.insert(achievement)
    }

    suspend fun updateAchievementStatus(id: Int, status: String) {
        achievementDao.updateAchievementStatus(id, status)
    }
}
