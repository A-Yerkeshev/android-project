package com.example.androidproject.ui.components

import android.location.Location
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
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

// This composable contains map with markers for checkpoints and user location.
// Additionally, it has controls for zooming the map and scrolling camera to the current location.
@Composable
fun ShowMap(
    checkpoints: List<CheckpointEntity>,
    myLocation: Location?,
    locationSignal: Boolean?,
    isLiveTrackingSelected: Boolean,
    selectedCheckpoint: CheckpointEntity?,
    onMapCameraMove: () -> Unit,
    onCheckpointClick: (CheckpointEntity?) -> Unit
) {
    val context = App.appContext
    val cameraState = rememberCameraState{
        geoPoint = GeoPoint(60.17057, 24.941521) // Central Railway Station
        zoom = 20.0
    }
    var isCameraInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(isCameraInitialized, isLiveTrackingSelected, myLocation) {
        // camera center to the current location on 1st load
        if (!isCameraInitialized && myLocation != null) {
            cameraState.geoPoint = GeoPoint(myLocation.latitude, myLocation.longitude)
            isCameraInitialized = true
        }

        // camera center to current location if live tracking is on
        if (isLiveTrackingSelected && myLocation != null) {
            cameraState.animateTo(GeoPoint(myLocation.latitude, myLocation.longitude))
        }
    }

    // unselect live tracking if user move map camera away from current location more than 2 meters
    LaunchedEffect(cameraState.geoPoint) {
        if (myLocation != null) {
            if (isLiveTrackingSelected &&
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
            .copy(tileSources = TileSourceFactory.MAPNIK) // default is null
            .copy(isEnableRotationGesture = false) // rotate map gesture
            .copy(zoomButtonVisibility = ZoomButtonVisibility.ALWAYS) // always, never or fading out
            .copy(maxZoomLevel = 22.0) // default is 26.0 (min default is 9.0)
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

                    // Determine current location's marker color, based on location signal's availability
                    val iconDrawable = if (locationSignal == true)
                        R.drawable.ic_my_location_marker
                    else
                        R.drawable.ic_my_location_marker_disabled

                    Marker(
                        state = rememberMarkerState(
                            geoPoint = GeoPoint(myLocation.latitude, myLocation.longitude)
                        ),
                        icon = ContextCompat.getDrawable(context, iconDrawable),
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