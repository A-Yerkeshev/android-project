package com.example.androidproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.data.models.QuestEntity
import com.example.androidproject.repository.QuestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
}

data class QuestWithCheckpoints(
    val quest: QuestEntity,
    val checkpoints: List<CheckpointEntity>
)