package com.example.androidproject.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidproject.data.models.QuestEntity
import com.example.androidproject.ui.navigation.Screens
import com.example.androidproject.ui.viewmodels.QuestViewModel

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    navCtrl: NavController,
    questViewModel: QuestViewModel
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userName = sharedPreferences.getString("user_name", "User")
    val completedQuests by questViewModel.completedQuests.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Welcome, $userName!",
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 45.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navCtrl.navigate(Screens.QuestsList.name) },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Text("Get Started", color = MaterialTheme.colorScheme.onSecondary)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        sharedPreferences.edit().clear().apply()
                        navCtrl.navigate(Screens.UserInput.name) {
                            popUpTo(Screens.Welcome.name) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Text("Reset Name", color = MaterialTheme.colorScheme.onError)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                val chunkedQuests = completedQuests.chunked(2)
                Log.d("CompletedQuests", "Chunked quests: $chunkedQuests")

                items(chunkedQuests) { rowQuests ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        rowQuests.forEach { quest ->
                            CompletedQuestCard(
                                quest = quest,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                                    .wrapContentHeight()
                            )
                        }
                        if (rowQuests.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompletedQuestCard(quest: QuestEntity, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Route: ${quest.description}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Status: ${if (quest.completedAt != null) "Completed" else "Not Completed"}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (quest.completedAt != null) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
            )
        }
    }
}