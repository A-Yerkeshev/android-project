package com.example.androidproject.ui.screens

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.example.androidproject.R
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.ui.components.CameraControls
import com.example.androidproject.ui.components.CameraPreview
import com.example.androidproject.ui.components.ConfettiAnimation
import com.example.androidproject.ui.viewmodels.CheckpointViewModel
import com.example.androidproject.ui.viewmodels.MapViewModel
import com.example.androidproject.ui.viewmodels.QuestViewModel
import com.example.androidproject.ui.viewmodels.TaskViewModel
import com.example.androidproject.utils.Constants.CHECKPOINT_PROXIMITY_METERS
import com.example.androidproject.utils.cameraPermission
import com.example.androidproject.utils.locationPermission
import com.example.androidproject.utils.savePhoto
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


enum class BottomSheetState {
    Collapsed,
    HalfExpanded,
    Expanded
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalWearMaterialApi::class)
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
    var showConfetti by remember { mutableStateOf(false) }

    var showConfetti by remember { mutableStateOf(false) }

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
        val completableCheckpoints = completableCheckpoints(myLocation, checkpoints)

        // Calculate number of completed checkpoints
        val totalCheckpoints = checkpoints.size
        val completedCheckpoints = checkpoints.count {it.completed}

        // State to hold the selected/highlighted checkpoint
        var selectedCheckpoint by remember { mutableStateOf<CheckpointEntity?>(null) }

        // State of the live tracking map's camera
        var isLiveTracking by remember { mutableStateOf(false) }

        // Persistent bottom sheet state
        var bottomSheetState by remember { mutableStateOf(BottomSheetState.HalfExpanded) }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

            val density = LocalDensity.current
            val maxHeightPx = with(density) { maxHeight.toPx() }
            val collapsedHeightDp = 150.dp
            val collapsedHeightPx = with(density) { collapsedHeightDp.toPx() }

            val swipeableState = rememberSwipeableState(initialValue = bottomSheetState)
            val anchors = mapOf(
                maxHeightPx - collapsedHeightPx to BottomSheetState.Collapsed,
                maxHeightPx / 2 to BottomSheetState.HalfExpanded,
                0f to BottomSheetState.Expanded
            )

            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(swipeableState.currentValue) {
                bottomSheetState = swipeableState.currentValue
            }

            LaunchedEffect(showCameraView) {
                if (showCameraView) {
                    coroutineScope.launch {
                        swipeableState.animateTo(BottomSheetState.Collapsed)
                    }
                }
            }

            if (!showCameraView) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()

                ) {

                    // Calculate dynamic bottom padding for the map
                    val bottomSheetOffset = swipeableState.offset.value
                    val additionalOffsetPx = with(density) { 15.dp.toPx() } // Adjust this value as needed
                    val mapBottomPaddingPx = maxOf(maxHeightPx - bottomSheetOffset - additionalOffsetPx, 0f)
                    val mapBottomPaddingDp = with(density) { mapBottomPaddingPx.toDp() }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(bottom = mapBottomPaddingDp)
                    ) {
                        ShowMap(
                            checkpoints = checkpoints,
                            myLocation = myLocation,
                            isLiveTracking = isLiveTracking,
                            selectedCheckpoint = selectedCheckpoint,
                            onMapCameraMove = {
                                isLiveTracking = false
                            },
                            onCheckpointClick = { checkpoint ->
                                selectedCheckpoint = checkpoint
                            }
                        )

                        // Button for centering at current location
                        Button(
                            onClick = { isLiveTracking = !isLiveTracking },
                            modifier = Modifier
                                .size(76.dp)
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLiveTracking) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                contentColor = if (isLiveTracking) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                            ),
                            shape = CircleShape,
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_rounded_my_location),
                                contentDescription = "Center to my position",
                                tint = if (isLiveTracking) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
//                        .padding(0.dp, 0.dp, 0.dp, 100.dp)
                    ) {
                        CameraPreview(
                            controller = cameraController,
                            modifier = Modifier.fillMaxSize(),
                        )
                        CameraControls(
                            controller = cameraController,
                            onPhotoCapture = {
                                val checkpoint = photoForCheckpoint

                                savePhoto(
                                    controller = cameraController,
                                    onCompleted = {
                                        if (checkpoint != null) {
                                            checkpointViewModel.markCompleted(checkpoint)

                                            // If this was last uncompleted checkpoint - mark quest as completed
                                            val allOtherCompleted = checkpoints.filter { it != checkpoint }.all { it.completed }
                                            if (allOtherCompleted) {
                                                questViewModel.markCompleted(selectedQuest)
                                            }
                                            showConfetti = true

                                            // Close camera
                                            showCameraView = false
                                            photoForCheckpoint = null
                                        }
                                    }
                                )
                            },
                            onClose = {
                                showCameraView = false
                                photoForCheckpoint = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .absoluteOffset(y = -148.dp)
                                .border(2.dp, Color.White)
                        )
                    }
                }
                // Persistent Bottom Sheet
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .offset { IntOffset(0, swipeableState.offset.value.roundToInt()) }
                        .swipeable(
                            state = swipeableState,
                            anchors = anchors,
                            thresholds = { _, _ -> FractionalThreshold(0.3f) },
                            orientation = Orientation.Vertical
                        )
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                val targetState =
                                    if (swipeableState.currentValue == BottomSheetState.HalfExpanded) BottomSheetState.Expanded else BottomSheetState.HalfExpanded
                                coroutineScope.launch {
                                    swipeableState.animateTo(targetState)
                                }
                            }
                    ) {
                        BottomSheetDefaults.DragHandle(
                            modifier = Modifier
                                .clickable {
                                    val targetState = if (swipeableState.currentValue == BottomSheetState.HalfExpanded) BottomSheetState.Expanded else BottomSheetState.HalfExpanded
                                    coroutineScope.launch {
                                        swipeableState.animateTo(targetState)
                                    }
                                }
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

                        }
                        if (bottomSheetState != BottomSheetState.Collapsed) {

                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                items(checkpoints) { checkpoint ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                                        shape = MaterialTheme.shapes.medium
                                    ) {
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
                                                    .weight(1f)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))

                                            var btnColor =
                                                if (checkpoint in completableCheckpoints) MaterialTheme.colorScheme.primary else Color.Gray
                                            if (checkpoint.completed) {
                                                btnColor = Color.Green
                                            }

                                            Button(
                                                onClick = {
                                                    if (checkpoint in completableCheckpoints) {
                                                        if (cameraPermissionGranted) {
                                                            showCameraView = true
                                                            photoForCheckpoint = checkpoint
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                "Camera permission required.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Reach the checkpoint to activate the camera.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                },
                                                modifier = Modifier
                                                    .size(76.dp)
                                                    .padding(16.dp),
                                                colors = ButtonDefaults.buttonColors(btnColor),
                                                shape = CircleShape,
                                                contentPadding = PaddingValues(4.dp)
                                            ) {
                                                val icon =
                                                    if (checkpoint.completed) R.drawable.ic_checkpoint_completed else R.drawable.baseline_photo_camera_24
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
                }
            if (showConfetti) {
                ConfettiAnimation()
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(3000)
                    showConfetti = false
                }
            }
        }
    } else {
        Text(text = "Location permission is required to display the map.")
    }
}

// Returns list of checkpoints, which are near the user's location and not yet completed
@Composable
fun completableCheckpoints(location: Location?, checkpoints: List<CheckpointEntity>): List<CheckpointEntity> {
    if (location == null) return emptyList()
    val context = LocalContext.current

    return checkpoints.filter { checkpoint ->
        !checkpoint.completed && isNear(location, checkpoint, context)
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