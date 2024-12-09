package com.example.androidproject.repository

// Manages quest data operations.
import com.example.androidproject.data.daos.CheckpointDao
import com.example.androidproject.data.daos.QuestDao
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.data.models.QuestEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class QuestRepository(
    private val questDao: QuestDao,
    private val checkpointDao: CheckpointDao
) {
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    // Fetch all quests
    fun getAllQuests(): Flow<List<QuestEntity>> = questDao.getAll()

    // Fetch a quest by ID
    fun getQuestById(questId: Int): Flow<List<QuestEntity>> = questDao.getById(questId)

    // Fetch checkpoints for a quest
    fun getCheckpointsByQuestId(questId: Int): Flow<List<CheckpointEntity>> = checkpointDao.getAll(questId)

    // Fetch all checkpoints
    fun getAllCheckpoints(): Flow<List<CheckpointEntity>> = checkpointDao.getAllCheckpoints()

    // Fetch current quest
    fun getCurrentQuest(): Flow<List<QuestEntity>> = questDao.getCurrent()

    // Fetch completed quests
    fun getCompletedQuests(): Flow<List<QuestEntity>> = questDao.getCompletedQuests()

    // Set quest as current
    suspend fun setQuestCurrent(quest: QuestEntity) {
        questDao.unsetAllCurrent()
        quest.current = true
        questDao.update(quest)
    }

    // Mark quest as completed
    suspend fun markCompleted(quest: QuestEntity) {
        quest.completedAt = LocalDateTime.now().format(formatter)
        questDao.update(quest)
    }

    // Reset quest
    suspend fun reset(quest: QuestEntity) {
        quest.completedAt = null
        questDao.update(quest)
        checkpointDao.getAll(quest.id).firstOrNull()?.let { checkpoints ->
            checkpoints.forEach { checkpoint ->
                checkpoint.completed = false
                checkpointDao.update(checkpoint)
            }
        }
    }
}