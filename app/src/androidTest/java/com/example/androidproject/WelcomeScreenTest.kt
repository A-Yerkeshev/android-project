package com.example.androidproject

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.androidproject.ui.screens.WelcomeScreen
import com.example.androidproject.ui.viewmodels.CheckpointViewModel
import com.example.androidproject.ui.viewmodels.QuestViewModel
import com.example.androidproject.ui.viewmodels.TaskViewModel
import org.junit.Rule
import org.junit.Test

class WelcomeScreenTest {
    @get:Rule
    val rule = createComposeRule()
    private var ruleInitialized = false

    private fun setRule() {
        if (!ruleInitialized) {
            rule.setContent {
                val navController = rememberNavController()
                val questViewModel: QuestViewModel = viewModel()
                WelcomeScreen(
                    navCtrl = navController,
                    questViewModel = questViewModel
                )
            }
            ruleInitialized = true
        }
    }

    @Test
    fun showsGreeting() {
        setRule()
        rule.onNodeWithText("Welcome", substring = true).assertExists()
    }

    @Test
    fun showsCompeletedQuests() {
        setRule()
        rule.onNodeWithText("Completed Quest Example", substring = true).assertExists()
    }
}