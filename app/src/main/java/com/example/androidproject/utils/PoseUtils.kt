package com.example.androidproject.utils

import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import kotlinx.coroutines.tasks.await

// Initialize pose detector once
private val poseDetector by lazy {
    val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.SINGLE_IMAGE_MODE)
        .build()
    PoseDetection.getClient(options)
}

/**
 * Process a bitmap to check if a thumb-up gesture is detected.
 * we consider "thumb up" if left thumb is above left wrist
 * or right thumb is above right wrist.
 */
suspend fun detectThumbsUp(context: Context, bitmap: Bitmap): Boolean {
    val image = InputImage.fromBitmap(bitmap, 0)
    val pose = try {
        poseDetector.process(image).await()
    } catch (e: Exception) {
        null
    }

    if (pose == null) return false

    return isThumbUp(pose)
}

private fun isThumbUp(pose: Pose): Boolean {
    val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
    val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
    val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
    val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)

    // Check if left thumb is above left wrist
    val leftThumbUp = if (leftThumb != null && leftWrist != null) {
        leftThumb.position.y < leftWrist.position.y
    } else false

    // Check if right thumb is above right wrist
    val rightThumbUp = if (rightThumb != null && rightWrist != null) {
        rightThumb.position.y < rightWrist.position.y
    } else false

    return leftThumbUp || rightThumbUp
}