package com.example.androidproject.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.example.androidproject.App
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.utils.Constants.CHECKPOINT_PROXIMITY_METERS
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationProvider {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(App.appContext)

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    // configs for the request and build it
    private val locationRequest = LocationRequest.Builder(LocReqConstants.INTERVAL_MS) // interval between updates
        .setPriority(LocReqConstants.PRIORITY_HIGH_ACC) // priority for trade off between accuracy and power
        .setMinUpdateIntervalMillis(LocReqConstants.FASTEST_INTERVAL_MS) // minimum update interval
        .setMaxUpdateDelayMillis(LocReqConstants.MAX_DELAY_MS) // maximum update delay, even if accurate location is not available yet
        .setMinUpdateDistanceMeters(LocReqConstants.MIN_DISTANT_METERS) // minimum distance between updates
        .build()

    // update the state flow variable in the callback function
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            locationResult.lastLocation?.let { location ->
                _currentLocation.value = location
            }
        }
    }

    fun startLocationUpdates() {
        // will need to implement properly later in Permission util
        if (ActivityCompat.checkSelfPermission(
                App.appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                App.appContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}

// Checks whether current location is within proximity of a checkpoint
fun isNear(location: Location, checkpoint: CheckpointEntity, context: Context): Boolean {
    val distance = FloatArray(1)
    Location.distanceBetween(
        location.latitude,
        location.longitude,
        checkpoint.lat,
        checkpoint.long,
        distance
    )
    return distance[0] < CHECKPOINT_PROXIMITY_METERS
}