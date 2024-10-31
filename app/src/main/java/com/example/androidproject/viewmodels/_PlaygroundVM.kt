package com.example.androidproject.viewmodels

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

class _PlaygroundVM {
    @Composable
    fun play() {
        val checkpointVM = CheckpointViewModel()
        val checkpoints = checkpointVM.getAll(1).collectAsState(initial = listOf())

        Column {
            checkpoints.value.forEach {
                Log.d("DBG", "${it.name}, lat: ${it.lat}, lon: ${it.long}")
                Text(text = "${it.name}, lat: ${it.lat}, lon: ${it.long}")
            }
        }
    }
}