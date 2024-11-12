package com.example.androidproject


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.androidproject.ui.navigation.AppNavigation
import com.example.androidproject.ui.navigation.BottomNavigationBar
import com.example.androidproject.ui.theme.AndroidProjectTheme
import com.example.androidproject.ui.viewmodels.QuestViewModel
import com.example.androidproject.ui.viewmodels.TaskViewModel

//import com.example.androidproject.viewmodels.QuestViewModel
//import com.example.androidproject.viewmodels.QuestViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // create single instance of each classes to be used across the app
            val navController = rememberNavController()
            val questViewModel: QuestViewModel = viewModel()
            val taskViewModel: TaskViewModel = viewModel()

            AndroidProjectTheme {
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            navController = navController,
                            questViewModel = questViewModel
                        )
                    }
                ) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        questViewModel = questViewModel,
                        taskViewModel = taskViewModel
                        )
                }
            }
        }
    }
}