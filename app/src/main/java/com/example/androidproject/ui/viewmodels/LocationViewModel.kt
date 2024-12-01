package com.example.androidproject.ui.viewmodels

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidproject.utils.LocationProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {
    private val locationProvider = LocationProvider()

    // expose location updates to be observe by the UI
    private val _myLocation = MutableStateFlow<Location?>(null)
    val myLocation: StateFlow<Location?> = _myLocation

    // start location update from Location Provider when created and collect value to myLocation
    init {
        locationProvider.startLocationUpdates()

        viewModelScope.launch {
            locationProvider.currentLocation.collect { location ->
                _myLocation.value = location
            }
        }
    }

    // stop location update when destroyed
    override fun onCleared() {
        super.onCleared()
        locationProvider.stopLocationUpdates()
    }
}