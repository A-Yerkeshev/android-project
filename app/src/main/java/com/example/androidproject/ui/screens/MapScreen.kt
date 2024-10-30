package com.example.androidproject.ui.screens

// Displays the map with the user's current location.
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {
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
        ShowMap()
    } else {
        // Handle permission not granted scenario
        // For example, show a message to the user
        Toast.makeText(
            context,
            "Location permission is required to display the map.",
            Toast.LENGTH_LONG
        ).show()
    }
}

@Composable
fun ShowMap() {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                // Enable pinch to zoom
                setMultiTouchControls(true)

                val mapController = controller
                mapController.setZoom(15.0)

                // Get user's current location
                val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                val hasFineLocationPermission = ActivityCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                val location = if (hasFineLocationPermission) {
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                } else {
                    null
                }

                val startPoint = if (location != null) {
                    GeoPoint(location.latitude, location.longitude)
                } else {
                    // Default to Helsinki coordinates if location is unavailable
                    GeoPoint(60.1699, 24.9384)
                }

                mapController.setCenter(startPoint)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}