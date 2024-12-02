package com.example.androidproject.ui.navigation

import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.androidproject.ui.screens.QuestDetailScreen
import com.example.androidproject.ui.screens.QuestsListScreen
import com.example.androidproject.ui.screens.UserInputScreen
import com.example.androidproject.ui.screens.WelcomeScreen
import com.example.androidproject.ui.viewmodels.CheckpointViewModel
import com.example.androidproject.ui.viewmodels.LocationViewModel
import com.example.androidproject.ui.viewmodels.QuestViewModel
import com.example.androidproject.ui.viewmodels.TaskViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavController,
    questViewModel: QuestViewModel,
    taskViewModel: TaskViewModel,
    cameraController: LifecycleCameraController,
    checkpointViewModel: CheckpointViewModel,
    startDestination: String // Dynamically set start destination
) {
    val locationViewModel: LocationViewModel = viewModel()

    NavHost(
        navController = navController as NavHostController,
        startDestination = startDestination
    ) {
        composable(route = Screens.UserInput.name) {
            UserInputScreen(
                navCtrl = navController,
            )
        }
        composable(route = Screens.Welcome.name) {
            WelcomeScreen(
                navCtrl = navController,
                questViewModel = questViewModel
            )
        }
        composable(route = Screens.QuestsList.name) {
            QuestsListScreen(
                navCtrl = navController,
                questViewModel = questViewModel
            )
        }
        composable(
            route = "${Screens.QuestDetail.name}/{questId}",
            arguments = listOf(navArgument("questId") { type = NavType.IntType })
        ) { backStackEntry ->
            val questId = backStackEntry.arguments?.getInt("questId") ?: 0
            QuestDetailScreen(
                locationViewModel = locationViewModel,
                questViewModel = questViewModel,
                taskViewModel = taskViewModel,
                questId = questId,
                cameraController = cameraController,
                checkpointViewModel = checkpointViewModel
            )
        }
    }
}

enum class Screens {
    UserInput,
    Welcome,
    QuestsList,
    QuestDetail
}