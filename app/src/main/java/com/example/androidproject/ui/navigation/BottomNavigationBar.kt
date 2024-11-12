package com.example.androidproject.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.androidproject.R
import com.example.androidproject.data.models.QuestEntity
import com.example.androidproject.ui.viewmodels.QuestViewModel

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    questViewModel: QuestViewModel
) {
    val currentRoute = currentRoute(navController)

//    val questViewModel = QuestViewModel()

    // using viewModel() instead of manually creating new instance of ViewModel(). viewModel() gets the
    // ViewModel from the provider, and persists through recomposition of the composable. It means when
    // the composable recomposes, the ViewModel stays the same, with all its state variables/data
//    val questViewModel: QuestViewModel = viewModel()

    // this listens to changes from viewModel all the time, which makes the nav bar recomposes
    // repeatedly. Ideally nav bar should just listen to change in currentQuest Id to update the
    // questId val
//    val questsWithCheckpoints by questViewModel.questsWithCheckpoints.collectAsState()
//    val currentQuest: QuestEntity? by questViewModel.currentQuest.collectAsState()
//    val questId: Int = if (currentQuest != null) {
//        currentQuest!!.id
//    } else if (questsWithCheckpoints.isNotEmpty()) {
//        questsWithCheckpoints.first().quest.id
//    } else {
//        0
//    }

    val currentQuest: QuestEntity? by questViewModel.currentQuest.collectAsState()

    // rememberUpdatedState: remembers the latest value but does not trigger the composable recomposition
    // when its value changes. This nav bar only needs to remember the current quest Id for the
    // correct navigation of the icons
    val questId: Int by rememberUpdatedState(currentQuest?.id ?: 0)

    Log.d("XXX", "NavBar currentQuest Id updated: ${currentQuest?.id ?: 0}")

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_rounded_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(if (currentRoute == Screens.Welcome.name) 36.dp else 28.dp)
                )
            },
            label = { if (currentRoute == Screens.Welcome.name)
                Text(
                    text = "Home",
                    fontSize = 16.sp
                ) },
            selected = currentRoute == Screens.Welcome.name,
            onClick = {
                navController.navigate(Screens.Welcome.name) {
                    popUpTo(Screens.Welcome.name) { inclusive = true }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
//                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_rounded_file_map_stack),
                    contentDescription = "Quests",
                    modifier = Modifier.size(if (currentRoute == Screens.QuestsList.name) 36.dp else 28.dp)
                )
            },
            label = { if (currentRoute == Screens.QuestsList.name)
                Text(
                    text = "Quests",
                    fontSize = 16.sp
                ) },
            selected = currentRoute == Screens.QuestsList.name,
            onClick = {
                navController.navigate(Screens.QuestsList.name) {
                    popUpTo(Screens.QuestsList.name) { inclusive = true }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
//                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_rounded_map_search),
                    contentDescription = "Map",
                    modifier = Modifier.size(if (currentRoute == "${Screens.QuestDetail.name}/{questId}") 36.dp else 28.dp)
                )
            },
            label = { if (currentRoute == "${Screens.QuestDetail.name}/{questId}")
                Text(
                    text = "Map",
                    fontSize = 16.sp
                ) },
            selected = currentRoute == "${Screens.QuestDetail.name}/{questId}",
            onClick = {
                navController.navigate("${Screens.QuestDetail.name}/${questId}") {
                    popUpTo(Screens.QuestDetail.name) { inclusive = true }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
//                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val backStackEntry = navController.currentBackStackEntryAsState().value
    Log.d("XXX", "${backStackEntry?.destination?.route}")
    return backStackEntry?.destination?.route
}