package com.example.androidproject.ui.screens

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.androidproject.App
import com.example.androidproject.R
import com.example.androidproject.data.models.CheckpointEntity
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.util.GeoPoint

@Composable
fun ShowMap(
    checkpoints: List<CheckpointEntity>,
    myLocation: Location?,
    isLiveTracking: Boolean,
//    cameraState: CameraState,
    selectedCheckpoint: CheckpointEntity?,
    onCheckpointClick: (CheckpointEntity) -> Unit
) {
//    val context = LocalContext.current
    val context = App.appContext

    val cameraState = rememberCameraState()
    var isCameraInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(isCameraInitialized, isLiveTracking, myLocation) {
        if (!isCameraInitialized && myLocation != null) {
            cameraState.geoPoint = GeoPoint(myLocation.latitude, myLocation.longitude)
            cameraState.zoom = 18.0

            isCameraInitialized = true
        }

        if (isLiveTracking && myLocation != null) {
            cameraState.animateTo(GeoPoint(myLocation.latitude, myLocation.longitude))
//            cameraState.zoom = 18.0
        }
    }

    LaunchedEffect(selectedCheckpoint) {
        if (selectedCheckpoint != null) {
            cameraState.geoPoint = GeoPoint(selectedCheckpoint.lat, selectedCheckpoint.long)
            cameraState.zoom = 18.0
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
        ) {
            key(myLocation) {
                if (myLocation != null) {
                    Marker(
                        state = rememberMarkerState(
                            geoPoint = GeoPoint(myLocation.latitude, myLocation.longitude)
                        ),
                        icon = ContextCompat.getDrawable(context, R.drawable.ic_my_location_marker),
                        title = "Your Location"
                    )
                }
            }

            key(checkpoints) {
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
}