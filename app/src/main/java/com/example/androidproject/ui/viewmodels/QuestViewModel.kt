package com.example.androidproject.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.androidproject.data.AppDB
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.data.models.QuestEntity
import com.example.androidproject.repository.QuestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class QuestViewModel : ViewModel() {
    private val database = AppDB.getDatabase()
    private val repository = QuestRepository(database.questDao(), database.checkpointDao())

    private val _questsWithCheckpoints = MutableStateFlow<List<QuestWithCheckpoints>>(emptyList())
    val questsWithCheckpoints: StateFlow<List<QuestWithCheckpoints>> = _questsWithCheckpoints

    private val _currentQuest = MutableStateFlow<QuestEntity?>(null)
    val currentQuest: StateFlow<QuestEntity?> = _currentQuest

    private val _completedQuests = MutableStateFlow<List<QuestEntity>>(emptyList())
    val completedQuests: StateFlow<List<QuestEntity>> = _completedQuests

    private val _newQuestResult = MutableStateFlow<Boolean?>(null)
    val newQuestResult: StateFlow<Boolean?> = _newQuestResult

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // get all quests information (with their checkpoints) to populate the QuestList screen (that
        // screen collects the questsWithCheckpoints variable here as State in the composable)
        getQuestsWithCheckpoints()

        // gets currentQuest the first time (when the ViewModel initializes)
        getCurrentQuest()

        getCompletedQuests()
    }

    fun createNewQuestWithCheckpoints(lat: Double, lon: Double, questDescription: String, amount: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.fetchAndInsertCheckpoints(lat, lon, questDescription, amount)
            _newQuestResult.value = result
            if (result) {
                getQuestsWithCheckpoints()
            }

            _isLoading.value = false

            // give time for the UI to collect the result
            delay(1000)
            _newQuestResult.value = null
        }
    }

    // Fetches all quests with their checkpoints
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

    private fun getCurrentQuest() {
        viewModelScope.launch {
            repository.getCurrentQuest().let { quest ->
                _currentQuest.value = quest.firstOrNull()?.first()
            }
        }
    }

    private fun getCompletedQuests() {
        viewModelScope.launch {
            repository.getCompletedQuests().collect { quests ->
                _completedQuests.value = quests
            }
        }
    }

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

    // Sets selected quest as current
    fun setQuestCurrent(quest: QuestEntity) {
        _currentQuest.value = quest

        viewModelScope.launch(Dispatchers.IO) {
            repository.setQuestCurrent(quest)
        }
    }

    // Marks quest as completed
    fun markCompleted(quest: QuestEntity?) {
        if (quest != null) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.markCompleted(quest)
            }
        }
    }

    // Resets quest's progress
    fun reset(quest: QuestEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.reset(quest)
        }
    }
}

data class QuestWithCheckpoints(
    val quest: QuestEntity,
    val checkpoints: List<CheckpointEntity>
)