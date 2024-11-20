package com.example.androidproject.ui.screens

//import androidx.compose.material.icons.filled.ExpandMore
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidproject.R
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.data.models.TaskEntity
import com.example.androidproject.ui.components.CameraControls
import com.example.androidproject.ui.components.CameraPreview
import com.example.androidproject.ui.viewmodels.CheckpointViewModel
import com.example.androidproject.ui.viewmodels.MapViewModel
import com.example.androidproject.ui.viewmodels.QuestViewModel
import com.example.androidproject.ui.viewmodels.TaskViewModel
import com.example.androidproject.utils.Constants.CHECKPOINT_PROXIMITY_METERS
import com.example.androidproject.utils.cameraPermission
import com.example.androidproject.utils.locationPermission
import com.utsman.osmandcompose.CameraState
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.OsmAndroidComposable
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestDetailScreen(
    modifier: Modifier = Modifier,
    mapViewModel: MapViewModel = viewModel(),
    questViewModel: QuestViewModel,
    taskViewModel: TaskViewModel,
    checkpointViewModel: CheckpointViewModel,
    questId: Int,
    cameraController: LifecycleCameraController
) {
    val context = LocalContext.current

    val myLocation by mapViewModel.myLocation.collectAsState()

    // Initialize OSMDroid configuration
    DisposableEffect(Unit) {
        Configuration.getInstance().load(
            context,
            context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        onDispose { }
    }

    // Request location permission
    val locationPermissionGranted = locationPermission()

    val cameraPermissionGranted = cameraPermission()

    var showCameraView by remember { mutableStateOf(false) }
    var photoForCheckpoint by remember { mutableStateOf<CheckpointEntity?>(null) }

    // Set the selected quest ID in the ViewModel
    LaunchedEffect(questId) {
        questViewModel.selectQuest(questId)
    }

    if (locationPermissionGranted) {
        // Observe checkpoints and selected quest from the ViewModel
        val checkpoints by questViewModel.checkpoints.observeAsState(emptyList())
        val selectedQuest by questViewModel.selectedQuest.observeAsState()
        val completableCheckpoints = completableCheckpoints(checkpoints)

        // Calculate number of completed checkpoints
        val totalCheckpoints = checkpoints.size
        val completedCheckpoints = checkpoints.count {it.completed}

        // State to hold the selected/highlighted checkpoint
        var selectedCheckpoint by remember { mutableStateOf<CheckpointEntity?>(null) }

        // Initialize camera state
        val cameraState = rememberCameraState()
        var isCameraInitialized by remember { mutableStateOf(false) }

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
//        cameraState.geoPoint = startPoint
//        cameraState.zoom = 15.0
//        if (!isCameraInitialzed && myLocation != null) {
//            cameraState.geoPoint = GeoPoint(myLocation!!.latitude, myLocation!!.longitude)
//            cameraState.zoom = 18.0
//
//            isCameraInitialzed = true
//        }
        LaunchedEffect(isCameraInitialized, myLocation) {
            if (!isCameraInitialized && myLocation != null) {
            cameraState.geoPoint = GeoPoint(myLocation!!.latitude, myLocation!!.longitude)
            cameraState.zoom = 18.0

            isCameraInitialized = true
            }
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
        val sheetHeight = if (isBottomSheetExpanded) 350.dp else 140.dp
        val bottomSheetPadding = 50.dp

        // Main UI layout
        Box(modifier = Modifier.fillMaxSize()) {
            if (!showCameraView) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()

                ) {
                    // Destimated height of the navigation bar
                    val navigationBarHeight = 40.dp

                    // Calculate the combined padding
                    val combinedPadding = sheetHeight + navigationBarHeight
                    // Map with specified height
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = combinedPadding)
                    ) {
                        // key() wrapper is used to force recomposition of map, when checkpoints' state changes
                        key(checkpoints, myLocation) {
                            ShowMap(
                                checkpoints = checkpoints,
                                myLocation = myLocation,
                                cameraState = cameraState,
                                selectedCheckpoint = selectedCheckpoint,
                                onCheckpointClick = { checkpoint ->
                                    selectedCheckpoint = checkpoint
                                }
                            )
                        }

                        // Add the Recenter Button overlaid on the map
                        Button(
                            onClick = {
                                // On button click, recenter the map
//                                val newCenter = if (location != null
//                                    && location.latitude != 0.0 && location.longitude != 0.0
//                                    && location.latitude in -90.0..90.0 && location.longitude in -180.0..180.0
//                                ) {
                                val newCenter = if (myLocation != null) {
//                                    GeoPoint(location.latitude, location.longitude)
                                    GeoPoint(myLocation!!.latitude, myLocation!!.longitude)
                                } else {
                                    // Default to Helsinki
                                    GeoPoint(60.1699, 24.9384)
                                }
                                //temporarily solves recenter in emulator
                                //val newCenter = GeoPoint(60.1699, 24.9384)
                                cameraState.geoPoint = newCenter
                                cameraState.zoom = 18.0
                            },
                            modifier = Modifier
                                .size(76.dp)
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                            shape = CircleShape,
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_rounded_my_location),
                                contentDescription = "Center to my position",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(36.dp)
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

                    //Spacer(modifier = Modifier.weight(1f)) // Push the button to the bottom
                }
                // Persistent Bottom Sheet
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(sheetHeight)
                        .align(Alignment.BottomCenter)
                        .offset(y = -bottomSheetPadding) // Move up by navbar height
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { isBottomSheetExpanded = !isBottomSheetExpanded },
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = if (isBottomSheetExpanded) Arrangement.Top else Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        BottomSheetDefaults.DragHandle(
                            modifier = Modifier

                                .clickable { isBottomSheetExpanded = !isBottomSheetExpanded }
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = selectedQuest?.description ?: "Quest Details",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(8.dp),
                                textAlign = TextAlign.Center
                            )

                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                            ) {
                                Text(
                                    text = "$completedCheckpoints / $totalCheckpoints visited",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            // Linear progress indicator below quest title
                            LinearProgressIndicator(
                                progress = { if (totalCheckpoints > 0) completedCheckpoints / totalCheckpoints.toFloat() else 0f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.primary,
                            )

                        }
                        if (isBottomSheetExpanded) {
                            Text(
                                "List of Checkpoints:",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                items(checkpoints) { checkpoint ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp, 0.dp)
                                    ) {
                                        // Highlight checkpoint in list if it matches selectedCheckpoint
                                        Text(
                                            text = checkpoint.name,
                                            color = if (checkpoint == selectedCheckpoint) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier
                                                .clickable {
                                                    selectedCheckpoint = checkpoint
                                                }
                                        )
                                        var btnColor = if (checkpoint in completableCheckpoints) MaterialTheme.colorScheme.primary else Color.Gray
                                        if (checkpoint.completed) { btnColor = Color.Green }

                                        Button(
                                            onClick = {
                                                if (checkpoint in completableCheckpoints) {
                                                    if (cameraPermissionGranted) {
                                                        showCameraView = true
                                                        photoForCheckpoint = checkpoint
                                                    } else {
                                                        Toast.makeText(context, "Camera permission required.", Toast.LENGTH_SHORT).show()
                                                    }
                                                } else {
                                                    Toast.makeText(context, "Reach the checkpoint to activate the camera.", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            modifier = Modifier
                                                .size(76.dp)
                                                .padding(16.dp),
                                            colors = ButtonDefaults.buttonColors(btnColor),
                                            shape = CircleShape,
                                            contentPadding = PaddingValues(4.dp)
                                        ) {
                                            val icon = if (checkpoint.completed) R.drawable.ic_checkpoint_completed else R.drawable.baseline_photo_camera_24
                                            Icon(
                                                painter = painterResource(id = icon),
                                                contentDescription = "Take a photo",
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .matchParentSize()
//                        .padding(0.dp, 0.dp, 0.dp, 100.dp)
                ) {
                    CameraPreview(
                        controller = cameraController,
                        modifier = Modifier.fillMaxSize(),
                    )
                    CameraControls(
                        onPhotoCapture = {
                            val checkpoint = photoForCheckpoint
                            if (checkpoint != null) {
                                checkpointViewModel.markCompleted(checkpoint)
                                showCameraView = false
                                photoForCheckpoint = null
                            }
                        },
                        onClose = {
                            showCameraView = false
                            photoForCheckpoint = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .absoluteOffset(y = -124.dp)
                            .border(2.dp, Color.White)
                    )
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
    myLocation: Location?,
    cameraState: CameraState,
    selectedCheckpoint: CheckpointEntity?,
    onCheckpointClick: (CheckpointEntity) -> Unit
) {
    val context = LocalContext.current

//    val cameraState = rememberCameraState()
//    var isCameraInitialized by remember { mutableStateOf(false) }
//
//    if (!isCameraInitialized && myLocation != null) {
//        cameraState.geoPoint = GeoPoint(myLocation.latitude, myLocation.longitude)
//        cameraState.zoom = 18.0
//
//        isCameraInitialized = true
//    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
        ) {
//            val location = getLocation(context)
//            if (location != null && location.latitude > 0 && location.longitude > 0) {

                if (myLocation != null) {
                    Marker(
                        state = rememberMarkerState(
                            geoPoint = GeoPoint(myLocation.latitude, myLocation.longitude)
                        ),
                        icon = ContextCompat.getDrawable(context, R.drawable.ic_my_location_marker),
                        title = "Your Location"
                    )
                }

                checkpoints.forEach { checkpoint ->

                    val iconResId = if (checkpoint.completed) {
                        R.drawable.ic_checkpoint_completed
                    } else {
                        R.drawable.ic_checkpoint_not_completed
                    }

                    Marker(
                        state = rememberMarkerState(
                            geoPoint = GeoPoint(checkpoint.lat, checkpoint.long)
                        ),
                        icon = ContextCompat.getDrawable(
                            context,
                            iconResId
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

// Returns list of checkpoints, which are near the user's location and not yet completed
@Composable
fun completableCheckpoints(checkpoints: List<CheckpointEntity>): List<CheckpointEntity> {
    val context = LocalContext.current

    return checkpoints.filter { checkpoint ->
        !checkpoint.completed && isNear(checkpoint, context)
    }
}

// Returns list of tasks, whose associated checkpoint is near the user's location
@Composable
fun completableTasks(tasks: List<TaskEntity>, checkpoints: List<CheckpointEntity>): List<TaskEntity> {
    return tasks.filter { task ->
        completableCheckpoints(checkpoints).any { checkpoint ->
            task.checkpointId == checkpoint.id
        }
    }
}

// Checks whether current location is within proximity of a checkpoint
fun isNear(checkpoint: CheckpointEntity, context: Context): Boolean {
    val location = getLocation(context)

    if (location != null) {
        val distance = FloatArray(1)
        Location.distanceBetween(
            location.latitude,
            location.longitude,
            checkpoint.lat,
            checkpoint.long,
            distance
        )
        return distance[0] < CHECKPOINT_PROXIMITY_METERS
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