package com.example.androidproject.ui.screens

import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.androidproject.data.UserPreferences
import com.example.androidproject.ui.navigation.Screens

@Composable
fun UserInputScreen(
    modifier: Modifier = Modifier,
    navCtrl: NavController
) {
    var userName by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "What's your name?",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Enter your name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // Save the name in SharedPreferences
                sharedPreferences.edit().putString("user_name", userName).apply()
                sharedPreferences.edit().putBoolean("is_user_name_set", true).apply()

                // Navigate to WelcomeScreen
                navCtrl.navigate(Screens.Welcome.name) {
                    popUpTo(Screens.UserInput.name) { inclusive = true }
                }
            }) {
                Text("Save and Continue")
            }
        }
    }
}