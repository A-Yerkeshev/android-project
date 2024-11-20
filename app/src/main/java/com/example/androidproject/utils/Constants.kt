package com.example.androidproject.utils

import com.google.android.gms.location.Priority

// Stores app-wide constants (e.g., API keys, static values).
object Constants {
    const val CHECKPOINT_PROXIMITY_METERS = 50
}

object LocReqConstants {
    const val PRIORITY_HIGH_ACC = Priority.PRIORITY_HIGH_ACCURACY
    const val PRIORITY_BALANCE = Priority.PRIORITY_BALANCED_POWER_ACCURACY
    const val INTERVAL_MS: Long = 3000
    const val FASTEST_INTERVAL_MS: Long = 2000
    const val MIN_DISTANT_METERS: Float = 5f
}


