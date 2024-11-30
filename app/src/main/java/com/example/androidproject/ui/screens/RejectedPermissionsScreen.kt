package com.example.androidproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RejectedPermissionsScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(24.dp)
    ) {
        Text(
            text = "Application needs an access to location and camera in order to work.",
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            fontSize = 36.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Text(
            text = "Go to phone's Settings -> Applications -> Permissions and grant location and camera permissions to Hel Quests app.",
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    }
}

@Preview
@Composable
fun RejectedPermissionsScreenPreview() {
    RejectedPermissionsScreen()
}