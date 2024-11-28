package com.example.androidproject.ui.screens

import android.location.Location
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.core.content.ContextCompat
import com.example.androidproject.App
import com.example.androidproject.R
import com.example.androidproject.data.models.CheckpointEntity
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import com.utsman.osmandcompose.rememberOverlayManagerState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.CopyrightOverlay

@Composable
fun ShowMap(
    checkpoints: List<CheckpointEntity>,
    myLocation: Location?,
    isLiveTracking: Boolean,
    selectedCheckpoint: CheckpointEntity?,
    onMapCameraMove: () -> Unit,
    onCheckpointClick: (CheckpointEntity?) -> Unit
) {
//    val context = LocalContext.current
    val context = App.appContext

    val cameraState = rememberCameraState{
        geoPoint = GeoPoint(60.17057, 24.941521) // Central Railway Station
        zoom = 20.0
    }

    var isCameraInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(isCameraInitialized, isLiveTracking, myLocation) {
        // camera center to the current location on 1st load
        if (!isCameraInitialized && myLocation != null) {
            cameraState.geoPoint = GeoPoint(myLocation.latitude, myLocation.longitude)

            isCameraInitialized = true
        }

        // camera center to current location if live tracking is on
        if (isLiveTracking && myLocation != null) {
            cameraState.animateTo(GeoPoint(myLocation.latitude, myLocation.longitude))
        }
    }

    // unselect live tracking if user move map camera away from current location more than 2 meters
    LaunchedEffect(cameraState.geoPoint) {
        if (myLocation != null) {
            if (isLiveTracking &&
                cameraState.geoPoint.distanceToAsDouble(GeoPoint(myLocation.latitude, myLocation.longitude)) > 2.0 ) {
                onMapCameraMove()
            }
        }
    }

    // camera center on selected checkpoint
    LaunchedEffect(selectedCheckpoint) {
        if (selectedCheckpoint != null) {
            cameraState.geoPoint = GeoPoint(selectedCheckpoint.lat, selectedCheckpoint.long)
            cameraState.zoom = 20.0
        }
    }

    // define properties with remember with default value
    var mapProperties by remember {
        mutableStateOf(DefaultMapProperties)
    }
    // setup mapProperties in side effect
    SideEffect {
        mapProperties = mapProperties
            .copy(isTilesScaledToDpi = false) // default is false
            .copy(tileSources = TileSourceFactory.MAPNIK)
            .copy(isEnableRotationGesture = false)
            .copy(zoomButtonVisibility = ZoomButtonVisibility.ALWAYS)
            .copy(maxZoomLevel = 22.0) // default is 26.0
            .copy(isFlingEnable = false) // fling gesture on map, default is true
            .copy(isAnimating = true) // default is true
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
            properties = mapProperties,
            onMapClick = {
                onCheckpointClick(null) // unselect currently selected checkpoint if user clicks on map
            }
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