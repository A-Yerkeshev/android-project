package com.example.androidproject


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.androidproject.data.AppDB
import com.example.androidproject.repository.QuestRepository
import com.example.androidproject.ui.navigation.AppNavigation
import com.example.androidproject.ui.navigation.BottomNavigationBar
import com.example.androidproject.ui.theme.AndroidProjectTheme
//import com.example.androidproject.viewmodels.QuestViewModel
//import com.example.androidproject.viewmodels.QuestViewModelFactory

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            AndroidProjectTheme {
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
//                    NavHost(navController = navController, startDestination = Screens.Welcome.name) {
//                        composable(route = Screens.Welcome.name) {
//                            WelcomeScreen(navCtrl = navController, modifier = Modifier.padding(innerPadding))
//                        }
//                        composable(route = Screens.QuestsList.name) {
//                            QuestsListScreen(navCtrl = navController, modifier = Modifier.padding(innerPadding))
//                        }
//                        composable(route = Screens.Map.name) {
//                            MapScreen(navCtrl = navController, modifier = Modifier.padding(innerPadding))
//                        }
//                    }
                    AppNavigation(navController = navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}