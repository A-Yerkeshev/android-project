@file:OptIn(ExperimentalPermissionsApi::class)

package com.example.androidproject.utils

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

// Functions that return a boolean value indicating whether the user has granted the permission or not.
// Requests permissions if they were not granted yet.

@Composable
fun locationPermission(): Boolean {
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(locationPermissionState.status) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    return locationPermissionState.status.isGranted
}

@Composable
fun cameraPermission(): Boolean {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(cameraPermissionState.status) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    return cameraPermissionState.status.isGranted
}