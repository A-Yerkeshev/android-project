package com.example.androidproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Achievement(val title: String, val completed: Boolean)

@Composable
fun AchievementsScreen() {
    val achievements = listOf(
        Achievement("Complete First Quest", true),
        Achievement("Reach Level 5", false),
        Achievement("Discover Hidden Item", true),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        Text(
            text = "User Achievements",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(achievements) { achievement ->
                AchievementItem(achievement)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: Achievement) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = achievement.title,
            fontSize = 18.sp,
            color = if (achievement.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = if (achievement.completed) "Completed" else "Incomplete",
            color = if (achievement.completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}