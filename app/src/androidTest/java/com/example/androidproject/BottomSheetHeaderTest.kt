package com.example.androidproject

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.androidproject.ui.components.BottomSheetHeader
import org.junit.Rule
import org.junit.Test

class BottomSheetHeaderTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun headerShowsQuestDescriptionAndCheckpointsCount() {
        rule.setContent {
            BottomSheetHeader(
                selectedQuestDescription = "Sample Quest",
                completedCheckpoints = 2,
                totalCheckpoints = 5,
                onExpandCollapse = {}
            )
        }

        // Check that the quest description is shown
        rule.onNodeWithText("Sample Quest").assertExists()

        // Check that the completed count is shown
        rule.onNodeWithText("2 / 5 visited").assertExists()
    }
}