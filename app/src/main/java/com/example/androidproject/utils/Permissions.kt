@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.androidproject.utils

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

// Function that return a boolean value indicating whether the user has granted the location and camera
// permission or not.
// Requests permissions if they were not granted yet.
@Composable
fun requestPermissions(): Boolean {
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(locationPermissionState.status) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    return locationPermissionState.status.isGranted && cameraPermissionState.status.isGranted
}