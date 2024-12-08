package com.example.androidproject.ui.screens

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
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.example.androidproject.data.models.CheckpointEntity
import com.example.androidproject.ui.components.BottomSheetContent
import com.example.androidproject.ui.components.BottomSheetHeader
import com.example.androidproject.ui.components.CameraControls
import com.example.androidproject.ui.components.CameraPreview
import com.example.androidproject.ui.components.ConfettiAnimation
import com.example.androidproject.ui.components.RecenterButton
import com.example.androidproject.ui.components.ShowMap
import com.example.androidproject.ui.viewmodels.CheckpointViewModel
import com.example.androidproject.ui.viewmodels.LocationViewModel
import com.example.androidproject.ui.viewmodels.QuestViewModel
import com.example.androidproject.ui.viewmodels.TaskViewModel
import com.example.androidproject.utils.isNear
import com.example.androidproject.utils.requestPermissions
import com.example.androidproject.utils.savePhoto
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class BottomSheetState {
    Collapsed,
    HalfExpanded,
    Expanded
}

// Screen, which contains map with markers, bottom sheet with checkpoint details and camera view.
@OptIn(ExperimentalMaterial3Api::class, ExperimentalWearMaterialApi::class)
@Composable
fun QuestDetailScreen(
    modifier: Modifier = Modifier,
    locationViewModel: LocationViewModel = viewModel(),
    questViewModel: QuestViewModel,
    taskViewModel: TaskViewModel,
    checkpointViewModel: CheckpointViewModel,
    questId: Int,
    cameraController: LifecycleCameraController
) {
    val context = LocalContext.current

    // collect current location data
    val myLocation by locationViewModel.myLocation.collectAsState()
    // collect status of live tracking viability
    val isLiveTrackingAvailable by locationViewModel.isLiveTrackingAvailable.collectAsState()
    // collect orientation (heading degrees)
    val azimuth by locationViewModel.headingDegrees.collectAsState()

    var showConfetti by remember { mutableStateOf(false) }
    var showCameraView by remember { mutableStateOf(false) }

    // Used to determine, for which checkpoint user attempts to take a photo
    var photoForCheckpoint by remember { mutableStateOf<CheckpointEntity?>(null) }

    // Set the selected quest ID in the ViewModel
    LaunchedEffect(questId) {
        questViewModel.selectQuest(questId)
    }

    // Request permissions
    val permissionsGranted = requestPermissions()

    if (!permissionsGranted) {
        RejectedPermissionsScreen()
    } else {
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
        var isLiveTrackingSelected by remember { mutableStateOf(false) }

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
                            isLiveTrackingAvailable = isLiveTrackingAvailable,
                            azimuth = azimuth,
                            isLiveTrackingSelected = isLiveTrackingSelected,
                            selectedCheckpoint = selectedCheckpoint,
                            onMapCameraMove = {
                                isLiveTrackingSelected = false
                            },
                            onCheckpointClick = { checkpoint ->
                                selectedCheckpoint = checkpoint
                            }
                        )

                        // Button for centering at current location
                        RecenterButton(
                            context = context,
                            isLiveTrackingAvailable = isLiveTrackingAvailable,
                            isLiveTrackingSelected = isLiveTrackingSelected,
                            onClick = {
                                isLiveTrackingSelected = !isLiveTrackingSelected
                            },
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }
            } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
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
                        .padding(top = 8.dp)
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
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clickable {
                                val targetState =
                                    if (swipeableState.currentValue == BottomSheetState.HalfExpanded) BottomSheetState.Expanded else BottomSheetState.HalfExpanded
                                coroutineScope.launch {
                                    swipeableState.animateTo(targetState)
                                }
                            }
                    ) {

                        // Modified Drag Handle
                        Box(
                            modifier = Modifier
                                .padding(top = 16.dp) // Add padding from the top
                                .size(width = 160.dp, height = 6.dp) // Adjust thickness by changing height
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                .clickable {
                                    val targetState =
                                        if (swipeableState.currentValue == BottomSheetState.HalfExpanded) BottomSheetState.Expanded else BottomSheetState.HalfExpanded
                                    coroutineScope.launch {
                                        swipeableState.animateTo(targetState)
                                    }
                                }
                                .padding(vertical = 8.dp)
                                .align(Alignment.CenterHorizontally)
                        )

                        BottomSheetHeader(
                            selectedQuestDescription = selectedQuest?.description,
                            completedCheckpoints = completedCheckpoints,
                            totalCheckpoints = totalCheckpoints,
                            onExpandCollapse = {
                                val targetState = if (swipeableState.currentValue == BottomSheetState.HalfExpanded) BottomSheetState.Expanded else BottomSheetState.HalfExpanded
                                coroutineScope.launch {
                                    swipeableState.animateTo(targetState)
                                }
                            }
                        )
                        if (bottomSheetState != BottomSheetState.Collapsed) {
                            BottomSheetContent(
                                checkpoints = checkpoints,
                                selectedCheckpoint = selectedCheckpoint,
                                completableCheckpoints = completableCheckpoints,
                                onCheckpointSelected = { checkpoint ->
                                    selectedCheckpoint = checkpoint
                                },
                                onCameraClick = { checkpoint ->
                                    if (checkpoint in completableCheckpoints) {
                                        showCameraView = true
                                        photoForCheckpoint = checkpoint
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Reach the checkpoint to activate the camera.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )

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