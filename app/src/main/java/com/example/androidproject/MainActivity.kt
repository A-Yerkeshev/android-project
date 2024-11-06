package com.example.androidproject


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
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
import com.example.androidproject.ui.navigation.AppNavigation
import com.example.androidproject.ui.navigation.BottomNavigationBar
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
            val navController = rememberNavController()

            MyApp(questViewModel)

            AndroidProjectTheme {
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    AppNavigation(navController = navController, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}