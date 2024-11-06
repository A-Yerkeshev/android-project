package com.example.androidproject.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.data.models.QuestEntity
import com.example.androidproject.repository.QuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class QuestViewModel(private val repository: QuestRepository) : ViewModel() {
    private val _questsWithCheckpoints = MutableStateFlow<List<QuestWithCheckpoints>>(emptyList())
    val questsWithCheckpoints: StateFlow<List<QuestWithCheckpoints>> = _questsWithCheckpoints

    init {
        getQuestsWithCheckpoints()
    }

    private fun getQuestsWithCheckpoints() {
        viewModelScope.launch {
            val questsFlow = repository.getAllQuests()
            val checkpointsFlow = repository.getAllCheckpoints()

            combine(questsFlow, checkpointsFlow) { quests, checkpoints ->
                quests.map { quest ->
                    QuestWithCheckpoints(
                        quest = quest,
                        checkpoints = checkpoints.filter { it.questId == quest.id }
                    )
                }
            }.collect { combinedData ->
                _questsWithCheckpoints.value = combinedData
            }
        }
    }

    // LiveData for all quests
    val allQuests: LiveData<List<QuestEntity>> = repository.getAllQuests().asLiveData()

    // LiveData for a specific quest
    private val _selectedQuestId = MutableLiveData<Int>()
    val selectedQuest: LiveData<QuestEntity?> = _selectedQuestId.switchMap { questId ->
        liveData {
            val quests = repository.getQuestById(questId).firstOrNull()
            emit(quests?.firstOrNull())
        }
    }

    // LiveData for checkpoints of the selected quest
    val checkpoints: LiveData<List<CheckpointEntity>> = _selectedQuestId.switchMap { questId ->
        repository.getCheckpointsByQuestId(questId).asLiveData()
    }

    // Method to set the selected quest ID
    fun selectQuest(questId: Int) {
        _selectedQuestId.value = questId
    }
}

data class QuestWithCheckpoints(
    val quest: QuestEntity,
    val checkpoints: List<CheckpointEntity>
)