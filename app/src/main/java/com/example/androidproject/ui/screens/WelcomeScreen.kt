package com.example.androidproject.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidproject.Screens

@Composable
fun WelcomeScreen(navCtrl: NavController, modifier: Modifier = Modifier) {
    Column(modifier = Modifier.padding(0.dp, 60.dp)) {
        Text(text = "Welcome Screen", fontSize = 24.sp)
        Button(onClick = {
            navCtrl.navigate(Screens.QuestsList.name)
        }) {
            Text(text = "To Quests List")
        }
    }
}