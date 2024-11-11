package com.example.androidproject.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.androidproject.data.models.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {

    @Insert
    suspend fun insert(achievement: AchievementEntity)

    @Query("SELECT * FROM achievement_table")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Update
    suspend fun update(achievement: AchievementEntity)

    // Add this method to update the achievement's status by its ID
    @Query("UPDATE achievement_table SET status = :status WHERE id = :id")
    suspend fun updateAchievementStatus(id: Int, status: String)
}