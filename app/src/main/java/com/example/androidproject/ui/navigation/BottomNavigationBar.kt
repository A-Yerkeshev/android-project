package com.example.androidproject.ui.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = currentRoute(navController)

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(if (currentRoute == Screens.Welcome.name) 30.dp else 24.dp)
                )
            },
            label = { if (currentRoute == Screens.Welcome.name) Text("Home") },
            selected = currentRoute == Screens.Welcome.name,
            onClick = {
                navController.navigate(Screens.Welcome.name) {
                    popUpTo(Screens.Welcome.name) { inclusive = true }
                }
            }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = "Quests",
                    modifier = Modifier.size(if (currentRoute == Screens.QuestsList.name) 30.dp else 24.dp)
                )
            },
            label = { if (currentRoute == Screens.QuestsList.name) Text("Quests") },
            selected = currentRoute == Screens.QuestsList.name,
            onClick = {
                navController.navigate(Screens.QuestsList.name) {
                    popUpTo(Screens.QuestsList.name) { inclusive = true }
                }
            }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Map",
                    modifier = Modifier.size(if (currentRoute == Screens.QuestDetail.name) 30.dp else 24.dp)
                )
            },
            label = { if (currentRoute == Screens.QuestDetail.name) Text("Map") },
            selected = currentRoute == Screens.QuestDetail.name,
            onClick = {
                navController.navigate(Screens.QuestDetail.name) {
                    popUpTo(Screens.QuestDetail.name) { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val backStackEntry = navController.currentBackStackEntryAsState().value
    return backStackEntry?.destination?.route
}