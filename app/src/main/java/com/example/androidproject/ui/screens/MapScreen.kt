package com.example.androidproject.ui.screens

// Displays the map with the user's current location.
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.androidproject.R
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState

//this file is not used in the app as Quest map? instead QuestDetailScreen.kt is used.

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(navCtrl: NavController, modifier: Modifier = Modifier) {

    val context = LocalContext.current

    // Initialize OSMDroid configuration
    DisposableEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        onDispose { }
    }

    // Request location permission
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(locationPermissionState.status) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    if (locationPermissionState.status.isGranted) {
        // Apply the modifier to the Column
        Column(modifier = modifier.fillMaxSize()) {
            // Other UI elements above the map
            Text(text = "Welcome to the Map Screen")
            Spacer(modifier = Modifier.height(16.dp))

            // Map with specified height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                ShowMap()
            }

            // Other UI elements below the map
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Additional content below the map")

        }
    } else {
        // Handle permission not granted scenario
        Text(text = "Location permission is required to display the map.")
    }
}

@Composable
fun ShowMap() {
    val context = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val hasFineLocationPermission = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val location = if (hasFineLocationPermission) {
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    } else {
        null
    }

    val startPoint = if (location != null) {
        GeoPoint(location.latitude, location.longitude)
    } else {
        // Default coordinates if location is unavailable
        GeoPoint(60.1699, 24.9384) // Helsinki
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        val cameraState = rememberCameraState {
            geoPoint = GeoPoint(startPoint.latitude, startPoint.longitude)
            zoom = 15.0
        }

        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState
        ) {
            Marker(
                state = rememberMarkerState(
                    geoPoint = GeoPoint(startPoint.latitude, startPoint.longitude)
                ),
                icon = ContextCompat.getDrawable(context, R.drawable.ic_location_marker),
                title = "Your Location"
            )
        }
    }
}