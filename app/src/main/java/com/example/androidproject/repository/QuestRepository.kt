package com.example.androidproject.repository

// Manages quest data operations.
import com.example.androidproject.data.daos.CheckpointDao
import com.example.androidproject.data.daos.QuestDao
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.data.models.QuestEntity
import kotlinx.coroutines.flow.Flow

class QuestRepository(
    private val questDao: QuestDao,
    private val checkpointDao: CheckpointDao
) {

    // Fetch all quests
    fun getAllQuests(): Flow<List<QuestEntity>> = questDao.getAllQuests()

    // Fetch a quest by ID
    fun getQuestById(questId: Int): Flow<List<QuestEntity>> = questDao.getById(questId)

    // Fetch checkpoints for a quest
    fun getCheckpointsByQuestId(questId: Int): Flow<List<CheckpointEntity>> = checkpointDao.getAll(questId)
}