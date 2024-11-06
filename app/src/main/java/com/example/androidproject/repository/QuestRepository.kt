package com.example.androidproject.repository

import com.example.androidproject.data.daos.CheckpointDao
import com.example.androidproject.data.daos.QuestDao
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.data.models.QuestEntity
import kotlinx.coroutines.flow.Flow

class QuestRepository(private val questDao: QuestDao, private val checkpointDao: CheckpointDao) {
    fun getAllQuests(): Flow<List<QuestEntity>> = questDao.getAll()

    fun getAllCheckpoints(): Flow<List<CheckpointEntity>> = checkpointDao.getAllCheckpoints()
}
