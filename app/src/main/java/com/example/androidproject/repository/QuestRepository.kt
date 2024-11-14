package com.example.androidproject.repository

// Manages quest data operations.
import com.example.androidproject.data.daos.CheckpointDao
import com.example.androidproject.data.daos.QuestDao
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.data.models.QuestEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class QuestRepository(
    private val questDao: QuestDao,
    private val checkpointDao: CheckpointDao,
) {
    // Fetch all quests
    fun getAllQuests(): Flow<List<QuestEntity>> = questDao.getAll()

    // Fetch a quest by ID
    fun getQuestById(questId: Int): Flow<List<QuestEntity>> = questDao.getById(questId)

    // Fetch checkpoints for a quest
    fun getCheckpointsByQuestId(questId: Int): Flow<List<CheckpointEntity>> = checkpointDao.getAll(questId)

    fun getAllCheckpoints(): Flow<List<CheckpointEntity>> = checkpointDao.getAllCheckpoints()

    fun getCurrentQuest(): Flow<List<QuestEntity>> = questDao.getCurrent()

    fun getCompletedQuests(): Flow<List<QuestEntity>> = questDao.getCompletedQuests()

    suspend fun setQuestCurrent(quest: QuestEntity) {
        questDao.unsetAllCurrent()
        quest.current = true
        questDao.update(quest)
    }
}