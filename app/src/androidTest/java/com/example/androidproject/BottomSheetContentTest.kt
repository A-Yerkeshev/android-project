package com.example.androidproject

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.ui.components.BottomSheetContent
import org.junit.Rule
import org.junit.Test

class BottomSheetContentTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun showsCheckpoints() {
        val checkpoints = listOf(
            CheckpointEntity(id = 1, questId = 4, name = "Checkpoint A", completed = false, lat = 0.0, long = 0.0),
            CheckpointEntity(id = 2, questId = 4, name = "Checkpoint B", completed = true, lat = 0.0, long = 0.0)
        )

        rule.setContent {
            BottomSheetContent(
                checkpoints = checkpoints,
                selectedCheckpoint = null,
                completableCheckpoints = emptyList(),
                onCheckpointSelected = {},
                onCameraClick = {}
            )
        }

        // Check that both checkpoints appear by their names
        rule.onNodeWithText("Checkpoint A").assertExists()
        rule.onNodeWithText("Checkpoint B").assertExists()
    }
}
