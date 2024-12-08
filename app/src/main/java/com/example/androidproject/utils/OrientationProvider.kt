package com.example.androidproject.utils

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import androidx.core.content.getSystemService
import com.example.androidproject.App
import com.google.android.gms.location.DeviceOrientation
import com.google.android.gms.location.DeviceOrientationListener
import com.google.android.gms.location.DeviceOrientationRequest
import com.google.android.gms.location.FusedOrientationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class OrientationProvider {
    val context = App.appContext

    private val fusedOrientationProviderClient: FusedOrientationProviderClient =
        LocationServices.getFusedOrientationProviderClient(context)

    private val _headingDegrees = MutableStateFlow<Float?>(null)
    val headingDegrees: StateFlow<Float?> = _headingDegrees

    private val orientationRequest = DeviceOrientationRequest
        .Builder(DeviceOrientationRequest.OUTPUT_PERIOD_DEFAULT)
        .build()

    private val listener: DeviceOrientationListener =
        DeviceOrientationListener { orientation: DeviceOrientation ->
            Log.d("XXX", "Azimuth: ${orientation.headingDegrees}")
            _headingDegrees.value = orientation.headingDegrees
    }

    init {
        startOrientationUpdate()
    }

    private fun startOrientationUpdate() {
        val executor = Executors.newSingleThreadExecutor()

        fusedOrientationProviderClient
            .requestOrientationUpdates(
                orientationRequest,
                executor,
                listener
            )
            .addOnSuccessListener {
                Log.d("XXX", "Fused Orientation Provider: registration success")
            }
            .addOnFailureListener { e: Exception? ->
                Log.d("XXX", "Fused Orientation Provider: registration failure: ${e?.message}")
            }
    }

    fun stopOrientationUpdate() {
        fusedOrientationProviderClient.removeOrientationUpdates(listener)
    }
}