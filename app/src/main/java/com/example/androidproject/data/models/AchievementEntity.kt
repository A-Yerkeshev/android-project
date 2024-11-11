package com.example.androidproject.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievement_table")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: String,
    var status: String
)
