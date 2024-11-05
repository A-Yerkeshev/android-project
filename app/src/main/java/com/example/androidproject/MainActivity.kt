package com.example.androidproject


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidproject.data.AppDB
import com.example.androidproject.data._PlaygroundDB
import com.example.androidproject.repository.QuestRepository
import com.example.androidproject.ui.screens.MapScreen
import com.example.androidproject.ui.screens.QuestDetailScreen
import com.example.androidproject.ui.screens.QuestsListScreen
import com.example.androidproject.ui.screens.WelcomeScreen
import com.example.androidproject.ui.theme.AndroidProjectTheme
import com.example.androidproject.viewmodels.QuestViewModel
import com.example.androidproject.viewmodels.QuestViewModelFactory
import com.example.androidproject.viewmodels._PlaygroundVM
import com.example.androidproject.ui.MyApp


enum class Screens {
    Welcome,
    QuestsList,
    QuestDetail
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the database and repository
        val database = AppDB.getDatabase(this)
        val repository = QuestRepository(database.questDao(), database.checkpointDao())

        // Create the ViewModel using a factory
        val questViewModel: QuestViewModel = ViewModelProvider(
            this,
            QuestViewModelFactory(repository)
        ).get(QuestViewModel::class.java)

        setContent {
            MyApp(questViewModel)
        }





//        setContent {
//            val navController = rememberNavController()
//            val questViewModel: QuestViewModel = viewModel()
//
//            AndroidProjectTheme {
//                Scaffold(
////                    topBar = {
////                        TopAppBar(
////                            title = { Text("My App") },
////                            navigationIcon = {
////                                IconButton(onClick = { /* Handle navigation icon press */ }) {
////                                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
////                                }
////                            }
////                        )
////                    },
//                    modifier = Modifier.fillMaxSize()
//                ) { innerPadding ->
//                    NavHost(navController = navController, startDestination = Screens.Welcome.name) {
//                        composable(route = Screens.Welcome.name) {
//                            WelcomeScreen(navCtrl = navController, modifier = Modifier.padding(innerPadding))
//                        }
//                        composable(route = Screens.QuestsList.name) {
//                            QuestsListScreen(navCtrl = navController, questViewModel = questViewModel, modifier = Modifier.padding(innerPadding))
//                        }
////                        composable(route = Screens.Map.name) {
////                            MapScreen(navCtrl = navController, modifier = Modifier.padding(innerPadding))
////                        }
//                        composable(
//                            route = "${Screens.QuestDetail.name}/{questId}",
//                            arguments = listOf(navArgument("questId") { type = NavType.IntType })
//                        ) { backStackEntry ->
//                            val questId = backStackEntry.arguments?.getInt("questId") ?: 0
//                            QuestDetailScreen(
//                                navCtrl = navController,
//                                questViewModel = questViewModel,
//                                questId = questId,
//                                modifier = Modifier.padding(innerPadding)
//                            )
//                        }
//                    }
//                }
//            }
//        }
    }
}