package com.example.androidproject

import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.androidproject.ui.screens.QuestsListScreen
import com.example.androidproject.ui.viewmodels.QuestViewModel
import org.junit.Rule
import org.junit.Test

class QuestsListScreenTest {
    @get:Rule
    val rule = createComposeRule()
    private var ruleInitialized = false

    private fun setRule() {
        if (!ruleInitialized) {
            rule.setContent {
                val navController = rememberNavController()
                val questViewModel: QuestViewModel = viewModel()
                QuestsListScreen(
                    navCtrl = navController,
                    questViewModel = questViewModel
                )
            }
            ruleInitialized = true
        }
    }

    @Test
    fun listOfQuestsShown() {
        setRule()
        // Every quest has a number
        rule.onAllNodesWithTag("QuestItem").assertAll(hasText("#", substring = true))
        // Every quest belongs to a category
        rule.onAllNodesWithTag("QuestItem").assertAll(hasText("Category", substring = true))
        // Every quest has completion status
        rule.onAllNodesWithTag("QuestItem").assertAll(hasText("Completed", substring = true))
        // Every quest button to go to map
        rule.onAllNodesWithTag("QuestItem").assertAll(hasTestTag("Button"))
    }
}