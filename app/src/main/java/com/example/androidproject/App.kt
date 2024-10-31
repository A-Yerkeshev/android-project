package com.example.androidproject
import android.app.Application
import android.content.Context
import android.util.Log
import com.example.androidproject.data._PlaygroundDB

class App: Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        _PlaygroundDB().play()
    }
}