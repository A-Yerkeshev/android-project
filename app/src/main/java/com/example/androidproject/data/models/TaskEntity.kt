package com.example.androidproject.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",

    // Tasks belong to checkpoints.
    // Checkpoint can have only one task.
    // Task can belong to only one checkpoint.
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
    val type: String? = null, // Which response is expected from user e.g. text, photo, audio etc.
    @ColumnInfo(name = "answer")
    val answer: String? = null // Currently applicable only to text type
)