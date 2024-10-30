package com.example.androidproject


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import com.example.androidproject.data._PlaygroundDB
import com.example.androidproject.ui.screens.MapScreen
import com.example.androidproject.ui.theme.AndroidProjectTheme

class MainActivity : ComponentActivity() {
    val dbPlayground = _PlaygroundDB()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dbPlayground.play()
        setContent {
            AndroidProjectTheme {
                MapScreen()
            }
        }
    }
}