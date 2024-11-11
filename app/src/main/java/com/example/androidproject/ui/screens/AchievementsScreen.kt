package com.example.androidproject.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.androidproject.ui.viewmodels.AchievementViewModel

@Composable
fun AchievementsScreen(viewModel: AchievementViewModel = hiltViewModel()) {
    val achievements = viewModel.achievements.collectAsState(initial = emptyList()).value

    LazyColumn {
        items(achievements) { achievement ->
            Text(text = "${achievement.name} - ${achievement.type} - ${achievement.status}")
        }
    }
}