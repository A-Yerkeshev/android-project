package com.example.androidproject

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.androidproject.ui.navigation.AppNavigation
import com.example.androidproject.ui.navigation.BottomNavigationBar
import com.example.androidproject.ui.navigation.Screens
import com.example.androidproject.ui.screens.RejectedPermissionsScreen
import com.example.androidproject.ui.theme.AndroidProjectTheme
import com.example.androidproject.ui.viewmodels.CheckpointViewModel
import com.example.androidproject.ui.viewmodels.LocationViewModel
import com.example.androidproject.ui.viewmodels.QuestViewModel
import com.example.androidproject.ui.viewmodels.TaskViewModel
import com.example.androidproject.utils.requestPermissions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var permissionGranted by remember { mutableStateOf(false) }

            permissionGranted = requestPermissions()

            if (permissionGranted) {
                val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val isUserNameSet = sharedPreferences.getBoolean("is_user_name_set", false)

                // If application is launched for the first time, show UserInput screen, else show Welcome screen
                val startDestination = if (isUserNameSet) {
                    Screens.Welcome.name
                } else {
                    Screens.UserInput.name
                }

                val navController = rememberNavController()
                val questViewModel: QuestViewModel = viewModel()
                val locationViewModel: LocationViewModel = viewModel()
                val taskViewModel: TaskViewModel = viewModel()
                val checkpointViewModel: CheckpointViewModel = viewModel()

                val cameraController = remember {
                    LifecycleCameraController(this).apply {
                        setEnabledUseCases(CameraController.IMAGE_CAPTURE)
                    }
                }

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
                            locationViewModel = locationViewModel,
                            taskViewModel = taskViewModel,
                            cameraController = cameraController,
                            checkpointViewModel = checkpointViewModel,
                            startDestination = startDestination
                        )
                    }
                }
            } else {
                RejectedPermissionsScreen()
            }
        }
    }
}