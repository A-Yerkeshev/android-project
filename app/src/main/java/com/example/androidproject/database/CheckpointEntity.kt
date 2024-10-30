package com.example.androidproject.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "checkpoints",
    foreignKeys = [
        ForeignKey(
            entity = QuestEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("quest_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CheckpointEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "quest_id")
    val questId: Int,
    @ColumnInfo(name = "lat")
    val lat: Double,
    @ColumnInfo(name = "long")
    val long: Double,
    @ColumnInfo(name = "completed")
    val completed: Boolean = false
)