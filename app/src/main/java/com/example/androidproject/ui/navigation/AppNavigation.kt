package com.example.androidproject.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidproject.ui.screens.MapScreen
import com.example.androidproject.ui.screens.QuestDetailScreen
import com.example.androidproject.ui.screens.QuestsListScreen
import com.example.androidproject.ui.screens.WelcomeScreen

@Composable
fun AppNavigation(navController: NavController, modifier: Modifier = Modifier) {
    NavHost(navController = navController as NavHostController, startDestination = Screens.Welcome.name) {
        composable(route = Screens.Welcome.name) {
            WelcomeScreen(navCtrl = navController, modifier)
        }
        composable(route = Screens.QuestsList.name) {
            QuestsListScreen(navCtrl = navController, modifier)
        }
        composable(route = Screens.Map.name) {
            MapScreen(navCtrl = navController, modifier)
        }
    }

//    BottomNavigationBar(navController = navController)
}

enum class Screens {
    Welcome,
    QuestsList,
    Map
}

