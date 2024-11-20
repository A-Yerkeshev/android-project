package com.example.androidproject.data

import android.content.Context
import androidx.core.content.edit

object UserPreferences {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_USER_NAME = "user_name"

    fun saveUserName(context: Context, name: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_USER_NAME, name) }
    }

    fun getUserName(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_NAME, null)
    }
}
