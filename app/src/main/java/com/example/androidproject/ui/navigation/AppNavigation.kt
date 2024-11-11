package com.example.androidproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.androidproject.ui.screens.AchievementsScreen
import com.example.androidproject.ui.screens.QuestDetailScreen
import com.example.androidproject.ui.screens.QuestsListScreen
import com.example.androidproject.ui.screens.WelcomeScreen

@Composable
fun AppNavigation(navController: NavController) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = Screens.Welcome.name
    ) {
        composable(route = Screens.Welcome.name) {
            WelcomeScreen(
                navCtrl = navController
            )
        }
        composable(route = Screens.QuestsList.name) {
            QuestsListScreen(
                navCtrl = navController
            )
        }

        composable(
            route = "${Screens.QuestDetail.name}/{questId}",
            arguments = listOf(navArgument("questId") { type = NavType.IntType })
        ) { backStackEntry ->
            val questId = backStackEntry.arguments?.getInt("questId") ?: 0
            QuestDetailScreen(
                navCtrl = navController,
                questId = questId
            )
        }

        composable(route = Screens.Achievements.name) {
            AchievementsScreen()
        }
    }
}

enum class Screens {
    Welcome,
    QuestsList,
    QuestDetail,
    Achievements
}