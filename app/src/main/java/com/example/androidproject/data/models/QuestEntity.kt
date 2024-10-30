package com.example.androidproject.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "quests"
)
data class QuestEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "description")
    val description: String = "",
    @ColumnInfo(name = "category")
    val category: String? = null,
    @ColumnInfo(name = "completed_at")
    val completedAt: String? = null,
    @ColumnInfo(name = "current")
    val current: Boolean = false
)