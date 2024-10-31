package com.example.androidproject
import android.app.Application
import android.content.Context
import android.util.Log
import com.example.androidproject.data.AppDB
import com.example.androidproject.data._PlaygroundDB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class App: Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        val database: AppDB = AppDB.getDatabase()
        GlobalScope.launch {
            database.fillWithTestData()
        }
    }
}