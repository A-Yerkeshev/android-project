package com.example.androidproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.data.models.QuestEntity
import com.example.androidproject.ui.navigation.Screens
import com.example.androidproject.ui.theme.SparaGreen
import com.example.androidproject.ui.viewmodels.QuestViewModel

// List of quests which are not completed. Each quest contains name, category, checkpoints, and status.
// By tapping on quest user goes to QuestDetailScreen and can proceed with completing the quest.
@Composable
fun QuestsListScreen(
    modifier: Modifier = Modifier,
    navCtrl: NavController,
    questViewModel: QuestViewModel
) {
    val questsWithCheckpoints by questViewModel.questsWithCheckpoints.collectAsState()

    // Filter out completed quests
    val uncompletedQuestsWithCheckpoints = questsWithCheckpoints.filter {
        it.quest.completedAt == null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary) // Apply background color here
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .padding(bottom = 56.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(uncompletedQuestsWithCheckpoints) { questWithCheckpoints ->
                var isExpanded by remember { mutableStateOf(questWithCheckpoints.quest.current) }

                QuestItem(
                    quest = questWithCheckpoints.quest,
                    checkpoints = questWithCheckpoints.checkpoints,
                    isExpanded = isExpanded,
                    onExpandChanged = { isExpanded = it },
                    onNavigateToMap = {
                        questViewModel.setQuestCurrent(questWithCheckpoints.quest)
                        navCtrl.navigate("${Screens.QuestDetail.name}/${questWithCheckpoints.quest.id}")
                    }
                )
            }
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
        shape = RoundedCornerShape(8.dp), // Define shape here
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Set the background color here
        )
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

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Category: ${quest.category ?: "Uncategorized"}",
                    style = MaterialTheme.typography.titleMedium
                )

                val completedAtText = if (quest.completedAt != null) {
                    "Completed: ${quest.completedAt}"
                } else {
                    "Status: Not Completed"
                }

                Text(
                    text = completedAtText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (quest.completedAt != null) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onNavigateToMap,
                colors = ButtonDefaults.buttonColors(containerColor = SparaGreen),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Go to Map",
                    style = MaterialTheme.typography.labelMedium
                )
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