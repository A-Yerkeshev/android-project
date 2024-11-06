package com.example.androidproject.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.androidproject.Screens
import com.example.androidproject.viewmodels.QuestViewModel
import com.example.androidproject.ui.theme.AndroidProjectTheme
import com.example.androidproject.ui.screens.*

@Composable
fun MyApp(questViewModel: QuestViewModel) {
    val navController = rememberNavController()

    AndroidProjectTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screens.Welcome.name
            ) {
                composable(route = Screens.Welcome.name) {
                    WelcomeScreen(
                        navCtrl = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                composable(route = Screens.QuestsList.name) {
                    QuestsListScreen(
                        navCtrl = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                composable(
                    route = "${Screens.QuestDetail.name}/{questId}",
                    arguments = listOf(navArgument("questId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val questId = backStackEntry.arguments?.getInt("questId") ?: 0
                    QuestDetailScreen(
                        navCtrl = navController,
                        questViewModel = questViewModel,
                        questId = questId,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}