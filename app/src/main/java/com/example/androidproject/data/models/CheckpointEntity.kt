package com.example.androidproject.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "checkpoints",

    // Checkpoints belong to quests.
    // One quest can have many checkpoints.
    // Checkpoint can belong to only one quest.
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
    var completed: Boolean = false,
    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "name_fi")
    val nameFi: String? = null,
    @ColumnInfo(name = "description")
    val description: String? = null,
    @ColumnInfo(name = "type")
    val type: String = "",
    @ColumnInfo(name = "website")
    val website: String? = null,
    @ColumnInfo(name = "wikipedia")
    val wikipedia: String? = null
)