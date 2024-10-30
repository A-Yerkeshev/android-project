package com.example.androidproject.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = CheckpointEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("checkpoint_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "checkpoint_id")
    val checkpointId: Int,
    @ColumnInfo(name = "description")
    val description: String = "",
    @ColumnInfo(name = "type")
    val type: String? = null,
    @ColumnInfo(name = "answer")
    val answer: String? = null
)