package com.example.androidproject

import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.androidproject.ui.screens.QuestDetailScreen
import com.example.androidproject.ui.viewmodels.CheckpointViewModel
import com.example.androidproject.ui.viewmodels.QuestViewModel
import com.example.androidproject.ui.viewmodels.TaskViewModel
import org.junit.Rule
import org.junit.Test

class QuestDetailsScreenTest {
    @get:Rule
    val rule = createComposeRule()
    private var ruleInitialized = false

    private fun setRule() {
        if (!ruleInitialized) {
            rule.setContent {
                val navController = rememberNavController()
                val questViewModel: QuestViewModel = viewModel()
                val taskViewModel: TaskViewModel = viewModel()
                val checkpointViewModel: CheckpointViewModel = viewModel()
                val cameraController = remember {
                    LifecycleCameraController(App.appContext).apply {
                        setEnabledUseCases(CameraController.IMAGE_CAPTURE)
                    }
                }
                QuestDetailScreen(
                    questViewModel = questViewModel,
                    checkpointViewModel = checkpointViewModel,
                    questId = 4,
                    taskViewModel = taskViewModel,
                    cameraController = cameraController
                )
            }
            ruleInitialized = true
        }
    }

    @Test
    fun mapIsShown() {
        setRule()
        // Map exists
        rule.onNodeWithTag("OpenStreetMap").assertExists()
        // Recenter button exists
        rule.onNodeWithContentDescription("Center to my position").assertExists()
    }

    @Test
    fun bottomSheetIsShown() {
        setRule()
        rule.onNodeWithTag("BoxWithConstraints").assertExists()
    }
}