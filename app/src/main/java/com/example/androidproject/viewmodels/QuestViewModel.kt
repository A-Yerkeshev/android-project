//package com.example.androidproject.viewmodels
//
//// Manages quests and related logic.
//import androidx.lifecycle.*
//import com.example.androidproject.data.models.CheckpointEntity
//import com.example.androidproject.data.models.QuestEntity
//import com.example.androidproject.repository.QuestRepository
//import kotlinx.coroutines.flow.collect
//import kotlinx.coroutines.flow.firstOrNull
//import kotlinx.coroutines.launch
//
//class QuestViewModel(private val repository: QuestRepository) : ViewModel() {
//
//    // LiveData for all quests
//    val allQuests: LiveData<List<QuestEntity>> = repository.getAllQuests().asLiveData()
//
//    // LiveData for a specific quest
//    private val _selectedQuestId = MutableLiveData<Int>()
//    val selectedQuest: LiveData<QuestEntity?> = _selectedQuestId.switchMap { questId ->
//        liveData {
//            val quests = repository.getQuestById(questId).firstOrNull()
//            emit(quests?.firstOrNull())
//        }
//    }
//
//    // LiveData for checkpoints of the selected quest
//    val checkpoints: LiveData<List<CheckpointEntity>> = _selectedQuestId.switchMap { questId ->
//        repository.getCheckpointsByQuestId(questId).asLiveData()
//    }
//
//    // Method to set the selected quest ID
//    fun selectQuest(questId: Int) {
//        _selectedQuestId.value = questId
//    }
//}
//
//class QuestViewModelFactory(private val repository: QuestRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(QuestViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return QuestViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}