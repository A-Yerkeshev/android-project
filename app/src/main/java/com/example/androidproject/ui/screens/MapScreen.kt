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
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.androidproject.R
import org.osmdroid.views.overlay.Marker

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

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                // Enable pinch to zoom
                setMultiTouchControls(true)

                // Configure map controller
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

                mapController.setCenter(startPoint)

                // Add a marker at the user's location
                val userMarker = Marker(this)
                userMarker.position = startPoint
                userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                // Use a custom icon or default
                userMarker.icon = ContextCompat.getDrawable(ctx, R.drawable.ic_location_marker)
                userMarker.title = "Your Location"

                overlays.add(userMarker)
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    )
}