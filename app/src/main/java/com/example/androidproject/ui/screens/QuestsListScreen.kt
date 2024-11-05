package com.example.androidproject.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidproject.Screens
import com.example.androidproject.viewmodels.QuestViewModel

@Composable
fun QuestsListScreen(
    navCtrl: NavController,
    questViewModel: QuestViewModel,
    modifier: Modifier = Modifier
) {
    val quests by questViewModel.allQuests.observeAsState(emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Quest List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(quests) { quest ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Navigate to MapScreen with the selected questId
                            navCtrl.navigate("${Screens.QuestDetail.name}/${quest.id}")
                        }
                        .padding(8.dp)
                ) {
                    Text(text = quest.description, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navCtrl.popBackStack() },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Go Back",
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}