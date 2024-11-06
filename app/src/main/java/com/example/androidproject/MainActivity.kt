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
import com.example.androidproject.ui.MyApp
import com.example.androidproject.ui.navigation.AppNavigation
import com.example.androidproject.ui.navigation.BottomNavigationBar
import com.example.androidproject.ui.theme.AndroidProjectTheme
import com.example.androidproject.viewmodels.QuestViewModel
import com.example.androidproject.viewmodels.QuestViewModelFactory


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
        val database = AppDB.getDatabase()
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