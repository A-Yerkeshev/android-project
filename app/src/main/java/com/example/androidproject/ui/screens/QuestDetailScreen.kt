package com.example.androidproject.ui.screens

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.isGranted
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import com.example.androidproject.data.models.TaskEntity
import com.example.androidproject.ui.viewmodels.TaskViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuestDetailScreen(
    navCtrl: NavController,
    questId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val questViewModel = QuestViewModel()
    val taskViewModel = TaskViewModel()

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
        // Observe checkpoints and selected quest from the ViewModel
        val checkpoints by questViewModel.checkpoints.observeAsState(emptyList())
        val selectedQuest by questViewModel.selectedQuest.observeAsState()
        val tasks by taskViewModel.currentTasks.collectAsState()
        val completableTasks = completableTasks(tasks, checkpoints)

        LaunchedEffect(checkpoints) {
            Log.d("QuestDetailScreen", "Number of checkpoints: ${checkpoints.size}")
            checkpoints.forEach { checkpoint ->
                Log.d("QuestDetailScreen", "Checkpoint: ${checkpoint.name} at (${checkpoint.lat}, ${checkpoint.long})")
            }
        }

        // State to hold the selected/highlighted checkpoint
        var selectedCheckpoint by remember { mutableStateOf<CheckpointEntity?>(null) }

        // Initialize camera state
        val cameraState = rememberCameraState()

        // Get the user's location
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

        val startPoint = remember(checkpoints, location) {
            val point = if (location != null
                && location.latitude != 0.0 && location.longitude != 0.0
                && location.latitude in -90.0..90.0 && location.longitude in -180.0..180.0
            ) {
                GeoPoint(location.latitude, location.longitude)
            } else if (checkpoints.isNotEmpty()) {
                GeoPoint(checkpoints[0].lat, checkpoints[0].long)
            } else {
                // Default coordinates if location is unavailable
                GeoPoint(60.1699, 24.9384) // Helsinki
            }
            Log.d("QuestDetailScreen", "startPoint: ${point.latitude}, ${point.longitude}")
            point
        }

        // Set initial camera position and zoom
        LaunchedEffect(startPoint) {
            cameraState.geoPoint = startPoint
            cameraState.zoom = 15.0
        }

        // Center the map on the selected checkpoint when it changes
        LaunchedEffect(selectedCheckpoint) {
            selectedCheckpoint?.let {
                cameraState.geoPoint = GeoPoint(it.lat, it.long)
                cameraState.zoom = 15.0
            }
        }


        // Persistent bottom sheet state
        var isBottomSheetExpanded by remember { mutableStateOf(false) }
        val sheetHeight = if (isBottomSheetExpanded) 400.dp else 200.dp
        val bottomSheetPadding = 100.dp  //

        // Main UI layout
        Box(modifier = Modifier.fillMaxSize()) {
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
                    selectedCheckpoint = selectedCheckpoint,
                    onCheckpointClick = { checkpoint ->
                        selectedCheckpoint = checkpoint
                    }
                )

                // Add the Recenter Button overlaid on the map
                Button(
                    onClick = {
                        // On button click, recenter the map
//                        val newCenter = if (location != null
//                            && location.latitude != 0.0 && location.longitude != 0.0
//                            && location.latitude in -90.0..90.0 && location.longitude in -180.0..180.0
//                        ) {
//                            GeoPoint(location.latitude, location.longitude)
//                        } else {
//                            // Default to Helsinki
//                            GeoPoint(60.1699, 24.9384)
//                        }
                        //temporarily solves recenter in emulator
                        val newCenter = GeoPoint(60.1699, 24.9384)
                        cameraState.geoPoint = newCenter
                        cameraState.zoom = 15.0
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Recenter",
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Display the quest title
            selectedQuest?.let {
                Text(
                    text = it.description.orEmpty(),  // Use 'description' if 'name' is not available
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Display the list of checkpoints
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(checkpoints) { checkpoint ->
                    // Highlight checkpoint in list if it matches selectedCheckpoint
                    Text(
                        text = checkpoint.name,
                        color = if (checkpoint == selectedCheckpoint) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                selectedCheckpoint =
                                    checkpoint  // Highlight the checkpoint in the list
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Push the button to the bottom

//            Button(
//                onClick = { navCtrl.popBackStack() },
//                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
//                shape = RoundedCornerShape(20.dp),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp)
//                    .padding(top = 16.dp)
//                    .padding(bottom = 16.dp)
//            ) {
//                Text(
//                    text = "Go Back",
//                    color = MaterialTheme.colorScheme.onSecondary,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
        }
            // Persistent Bottom Sheet
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(sheetHeight)
                    .padding(bottom = bottomSheetPadding)
                    .clickable { isBottomSheetExpanded = !isBottomSheetExpanded }
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Persistent Bottom Sheet. Click to expand/collapse.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                    if (isBottomSheetExpanded) {
                        Text(
                            "Additional content when expanded...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    } else {
        Text(text = "Location permission is required to display the map.")
    }
}

@Composable
fun ShowMap(
    checkpoints: List<CheckpointEntity>,
    cameraState: CameraState,
    selectedCheckpoint: CheckpointEntity?,
    onCheckpointClick: (CheckpointEntity) -> Unit
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState
        ) {
            val location = getLocation(context)

            if (location != null && location.latitude > 0 && location.longitude > 0) {
                Marker(
                    state = rememberMarkerState(
                        geoPoint = GeoPoint(location.latitude, location.longitude)
                    ),
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_location_marker),
                    title = "Your Location"
                )
            }

            checkpoints.forEach { checkpoint ->
                Marker(
                    state = rememberMarkerState(
                        geoPoint = GeoPoint(checkpoint.lat, checkpoint.long)
                    ),
                    icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.baseline_radio_button_checked_24
                    ),
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

@Composable
fun completableTasks(tasks: List<TaskEntity>, checkpoints: List<CheckpointEntity>): List<TaskEntity> {
    val context = LocalContext.current

    val availableCheckpoints = checkpoints.filter { checkpoint ->
        checkpoint.completed == false && isNear(checkpoint, context)
    }

    return tasks.filter { task ->
        availableCheckpoints.any { checkpoint ->
            task.checkpointId == checkpoint.id
        }
    }
}

fun isNear(checkpoint: CheckpointEntity, context: Context): Boolean {
    val location = getLocation(context)

    // Arman's home for testing
//    val location = GeoPoint(60.235610, 25.006100)

    if (location != null) {
        val distance = FloatArray(1)
        Location.distanceBetween(
            location.latitude,
            location.longitude,
            checkpoint.lat,
            checkpoint.long,
            distance
        )
        return distance[0] < 30
    } else {
        return false
    }
}

fun getLocation(context: Context): Location? {
    // User location marker
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val hasFineLocationPermission = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return if (hasFineLocationPermission) {
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    } else {
        null
    }
}