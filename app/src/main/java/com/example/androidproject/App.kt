package com.example.androidproject
import android.app.Application
import android.content.Context
import android.util.Log

class App: Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}