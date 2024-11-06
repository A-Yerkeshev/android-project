package com.example.androidproject.ui.viewmodels

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class _PlaygroundVM {
    @Composable
    fun Play() {
        val checkpointVM = CheckpointViewModel()
        val checkpoints = checkpointVM.getAll(1).collectAsState(initial = listOf())

        Column {
            Spacer(modifier = Modifier.padding(0.dp, 280.dp))
            checkpoints.value.forEach {
                Log.d("DBG", "${it.name}, lat: ${it.lat}, lon: ${it.long}")
                Text(text = "${it.name}, lat: ${it.lat}, lon: ${it.long}")
            }
        }
    }
}