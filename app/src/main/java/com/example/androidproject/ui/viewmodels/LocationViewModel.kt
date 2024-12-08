package com.example.androidproject.ui.viewmodels

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidproject.utils.LocationProvider
import com.example.androidproject.utils.OrientationProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {
    // create location provider instance
    private val locationProvider = LocationProvider()
    // create orientation provider instance
    private val orientationProvider = OrientationProvider()

    // expose location updates to be observed by the UI
    private val _myLocation = MutableStateFlow<Location?>(null)
    val myLocation: StateFlow<Location?> = _myLocation

    private val _isLiveTrackingAvailable = MutableStateFlow<Boolean?>(null)
    val isLiveTrackingAvailable: StateFlow<Boolean?> = _isLiveTrackingAvailable

    // expose azimuth degree to be observed by the UI
    private val _headingDegrees = MutableStateFlow<Float?>(null)
    val headingDegrees: StateFlow<Float?> = _headingDegrees

    init {
        // collect location updates
        viewModelScope.launch {
            locationProvider.currentLocation.collect { location ->
                _myLocation.value = location
            }
        }

        // collect live tracking status update
        viewModelScope.launch {
            locationProvider.isLiveTrackingAvailable.collect { isAvailable ->
                _isLiveTrackingAvailable.value = isAvailable
            }
        }

        // collect azimuth update
        viewModelScope.launch {
            orientationProvider.headingDegrees.collect { heading ->
                _headingDegrees.value = heading
            }
        }
    }

    // stop location update when destroyed
    override fun onCleared() {
        super.onCleared()
        locationProvider.stopLocationUpdates()
        orientationProvider.stopOrientationUpdate()
    }
}