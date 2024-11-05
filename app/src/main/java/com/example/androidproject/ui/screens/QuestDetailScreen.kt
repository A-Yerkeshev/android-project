package com.example.androidproject.ui.screens

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.isGranted
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.androidproject.R
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.viewmodels.QuestViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QuestDetailScreen(

    navCtrl: NavController,
    questViewModel: QuestViewModel,
    questId: Int,
    modifier: Modifier = Modifier
) {
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

    // Set the selected quest ID in the ViewModel
    LaunchedEffect(questId) {
        questViewModel.selectQuest(questId)
    }

    if (locationPermissionState.status.isGranted) {
        // Observe checkpoints from the ViewModel
        val checkpoints by questViewModel.checkpoints.observeAsState(emptyList())

        LaunchedEffect(checkpoints) {
            Log.d("QuestDetailScreen", "Number of checkpoints: ${checkpoints.size}")
            checkpoints.forEach { checkpoint ->
                Log.d("QuestDetailScreen", "Checkpoint: ${checkpoint.name} at (${checkpoint.lat}, ${checkpoint.long})")
            }
        }
        // State to hold the selected checkpoint for description display
        var selectedCheckpoint by remember { mutableStateOf<CheckpointEntity?>(null) }

        // Apply the modifier to the Column
        Column(modifier = modifier.fillMaxSize()) {
            // Map with specified height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                ShowMap(checkpoints = checkpoints) { checkpoint ->
                    selectedCheckpoint = checkpoint
                }
            }

            // Display the checkpoint description below the map
            selectedCheckpoint?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
                // add a 'description' field
//                Text(
//                    text = it.description ?: "No description available.",
//                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier.padding(horizontal = 8.dp)
//                )
            }

            Spacer(modifier = Modifier.weight(1f)) //here to push the button to the bottom!!!

            Button(
                onClick = { navCtrl.popBackStack() },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Go Back",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

        }
    } else {
        // Handle permission not granted scenario
        Text(text = "Location permission is required to display the map.")
    }
}

@Composable
fun ShowMap(
    checkpoints: List<CheckpointEntity>,
    onCheckpointClick: (CheckpointEntity) -> Unit
) {
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
            // User location marker
            Marker(
                state = rememberMarkerState(
                    geoPoint = GeoPoint(startPoint.latitude, startPoint.longitude)
                ),
                icon = ContextCompat.getDrawable(context, R.drawable.ic_location_marker),
                title = "Your Location"
            )

            // Checkpoint markers
            checkpoints.forEach { checkpoint ->
                Marker(
                    state = rememberMarkerState(
                        geoPoint = GeoPoint(checkpoint.lat, checkpoint.long)
                    ),
                    icon = ContextCompat.getDrawable(context, R.drawable.baseline_radio_button_checked_24),
                    title = checkpoint.name,
                    onClick = {
                        onCheckpointClick(checkpoint)
                        true
                    }
                )
            }
        }
    }
}
