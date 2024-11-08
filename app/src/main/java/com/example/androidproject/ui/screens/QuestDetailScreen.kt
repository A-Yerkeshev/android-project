package com.example.androidproject.ui.screens

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.isGranted
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.androidproject.R
import com.example.androidproject.data.AppDB
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.repository.QuestRepository
import com.example.androidproject.ui.viewmodels.QuestViewModel
import com.example.androidproject.ui.viewmodels.QuestViewModelFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.utsman.osmandcompose.CameraState
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
    questId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val database = AppDB.getDatabase()
    val repository = QuestRepository(database.questDao(), database.checkpointDao())
    val questViewModel: QuestViewModel = viewModel(factory = QuestViewModelFactory(repository))

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
        val selectedQuest by questViewModel.selectedQuest.observeAsState()

        // State to hold the selected checkpoint for highlighting
        var highlightedCheckpoint by remember { mutableStateOf<CheckpointEntity?>(null) }

        // Initialize the map camera state
        val cameraState = rememberCameraState()

        LaunchedEffect(checkpoints) {
            Log.d("QuestDetailScreen", "Number of checkpoints: ${checkpoints.size}")
            checkpoints.forEach { checkpoint ->
                Log.d("QuestDetailScreen", "Checkpoint: ${checkpoint.name} at (${checkpoint.lat}, ${checkpoint.long})")
            }
        }

        // Apply the modifier to the Column
        Column(modifier = modifier.fillMaxSize()) {
            // Map with specified height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                ShowMap(
                    checkpoints = checkpoints,
                    cameraState = cameraState,
                    onCheckpointClick = { checkpoint ->
                        highlightedCheckpoint = checkpoint
                        cameraState.geoPoint = GeoPoint(checkpoint.lat, checkpoint.long) // Center map on checkpoint
                    }
                )
            }
            // Display the quest title
            Text(
                text = selectedQuest?.description.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )

            // Display the list of checkpoints
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(checkpoints) { checkpoint ->
                    Text(
                        text = checkpoint.name,
                        color = if (checkpoint == highlightedCheckpoint) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                highlightedCheckpoint = checkpoint
                                cameraState.geoPoint = GeoPoint(checkpoint.lat, checkpoint.long) // Center map
                            }
                    )
                }
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
    cameraState: CameraState,
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

    val startPoint = if (location != null && location.latitude > 0 && location.longitude > 0) {
        GeoPoint(location.latitude, location.longitude)
    } else if (!checkpoints.isEmpty()) {
        GeoPoint(checkpoints[0].lat, checkpoints[0].long)
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
