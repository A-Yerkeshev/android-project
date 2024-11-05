package com.example.androidproject.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.androidproject.Screens
import com.example.androidproject.viewmodels.QuestViewModel

//@Composable
//fun QuestsListScreen(navCtrl: NavController, modifier: Modifier = Modifier) {
//    Column(modifier = Modifier.padding(0.dp, 60.dp)) {
//        Text(text = "Quests List Screen", fontSize = 24.sp)
//        Button(onClick = {
//            navCtrl.navigate(Screens.Map.name)
//        }) {
//            Text(text = "Select Quest")
//        }
//    }
//}
@Composable
fun QuestsListScreen(
    navCtrl: NavController,
    questViewModel: QuestViewModel,
    modifier: Modifier = Modifier
) {
    val quests by questViewModel.allQuests.observeAsState(emptyList())

    LazyColumn(modifier = modifier.padding(16.dp)) {
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
}