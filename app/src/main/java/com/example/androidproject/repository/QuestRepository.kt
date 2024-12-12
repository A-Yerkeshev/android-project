package com.example.androidproject.repository

// Manages quest data operations.
import com.example.androidproject.data.daos.CheckpointDao
import com.example.androidproject.data.daos.QuestDao
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.data.models.QuestEntity
import com.example.androidproject.network.Element
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

    // converts results from API to CheckpointEntity
    private fun mapElementToCheckpointEntity(element: Element, questId: Int): CheckpointEntity {
        val type = element.tags?.tourism ?: element.tags?.historic ?: ""
        return CheckpointEntity(
            id = element.id.toInt(),
            questId = questId,
            lat = element.lat,
            long = element.lon,
            completed = false,
            name = element.tags?.name ?: "",
            nameFi = element.tags?.nameFi,
            description = element.tags?.description,
            wikipedia = element.tags?.wikipedia,
            website = element.tags?.website,
            type = type
        )
    }

    private suspend fun createNewQuest(description: String): Int {
        // use this to update newly created quest as the current one
//        questDao.unsetAllCurrent()

        val newQuest = QuestEntity(
            description = description,
//            current = true // use this to update newly created quest as the current one
        )

        return questDao.insert(newQuest).toInt()
    }
}