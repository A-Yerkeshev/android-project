package com.example.androidproject.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.androidproject.App
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.utils.Constants.CHECKPOINT_PROXIMITY_METERS
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationProvider {
    val context = App.appContext

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(App.appContext)

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    // track the status of live location tracking
    private val _isLiveTrackingAvailable = MutableStateFlow<Boolean?>(null)
    val isLiveTrackingAvailable: StateFlow<Boolean?> = _isLiveTrackingAvailable

    // configs for the request and build it
    private val locationRequest = LocationRequest.Builder(LocReqConstants.INTERVAL_MS) // interval between updates
        .setPriority(LocReqConstants.PRIORITY_HIGH_ACC) // priority for trade off between accuracy and power
        .setMinUpdateIntervalMillis(LocReqConstants.FASTEST_INTERVAL_MS) // minimum update interval
        .setMaxUpdateDelayMillis(LocReqConstants.MAX_DELAY_MS) // maximum update delay, even if accurate location is not available yet
        .setMinUpdateDistanceMeters(LocReqConstants.MIN_DISTANT_METERS) // minimum distance between updates
        .build()


    private val locationCallback = object : LocationCallback() {
        // update the state flow variable in the callback function
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            locationResult.lastLocation?.let { location ->
                _currentLocation.value = location
            }
        }

        // update live location tracking status
        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            super.onLocationAvailability(locationAvailability)

            _isLiveTrackingAvailable.value = locationAvailability.isLocationAvailable
        }
    }

    // start updating location data upon init
    init {
        // request location updates
        startLocationUpdates()

        // get last known location from device's cache while waiting for the live tracking to be ready
        getLastLocation()?.let { lastLocation ->
            _currentLocation.value = lastLocation
        }
    }

    // not private, can be used later by different functionalities
    fun getLastLocation(): Location? {
        var lastLocation: Location? = null

        // will need to implement properly later in Permission util
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        lastLocation = location
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("XXX", "Failed to fetch last known location: ${exception.message}")
                }
        }

        return lastLocation
    }

    private fun startLocationUpdates() {
        // will need to implement properly later in Permission util
        if (ActivityCompat.checkSelfPermission(
                App.appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                App.appContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
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