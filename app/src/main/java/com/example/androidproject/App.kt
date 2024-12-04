package com.example.androidproject
import android.app.Application
import android.content.Context
import com.example.androidproject.data.AppDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class App: Application() {
    companion object {
        lateinit var appContext: Context
    }

    // When application is launched - sets global appContext variable,
    // fills database with test data and sets current quest.
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        val database: AppDB = AppDB.getDatabase()
        GlobalScope.launch(Dispatchers.IO) {
            database.fillWithTestData()
            database.setCurrentIfNotExists()
        }
    }
}