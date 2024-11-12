package com.example.androidproject.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.example.androidproject.data.AppDB
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.data.models.QuestEntity
import com.example.androidproject.repository.QuestRepository
import com.example.androidproject.ui.navigation.Screens
import com.example.androidproject.ui.viewmodels.QuestViewModel
import com.example.androidproject.ui.viewmodels.QuestViewModelFactory

@Composable
fun QuestsListScreen(navCtrl: NavController, modifier: Modifier = Modifier) {
//    val viewModel = QuestViewModel()
    // using viewModel() instead of manually creating new instance of ViewModel(). viewModel() gets the
    // ViewModel from the provider, and persists through recomposition of the composable. It means when
    // the composable recomposes, the ViewModel stays the same, with all its state variables/data
    val viewModel: QuestViewModel = viewModel()
    val questsWithCheckpoints by viewModel.questsWithCheckpoints.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(questsWithCheckpoints) { questWithCheckpoints ->
            var isExpanded by remember { mutableStateOf(questWithCheckpoints.quest.current) }

            QuestItem(
                quest = questWithCheckpoints.quest,
                checkpoints = questWithCheckpoints.checkpoints,
                isExpanded = isExpanded,
                onExpandChanged = { isExpanded = it },
                onNavigateToMap = {
                    viewModel.setQuestCurrent(questWithCheckpoints.quest)
                    navCtrl.navigate("${Screens.QuestDetail.name}/${questWithCheckpoints.quest.id}")
                }
            )
        }
    }
}

@Composable
fun QuestItem(
    quest: QuestEntity,
    checkpoints: List<CheckpointEntity>,
    isExpanded: Boolean,
    onExpandChanged: (Boolean) -> Unit,
    onNavigateToMap: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onExpandChanged(!isExpanded) },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "#${quest.id} ${quest.description}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Category: ${quest.category ?: "Uncategorized"}",
                    style = MaterialTheme.typography.titleMedium
                )


                Button(
                    onClick = onNavigateToMap,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "Go to Map",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

            }

            if (isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Checkpoints: ${checkpoints.joinToString(", ") { it.name }}.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}