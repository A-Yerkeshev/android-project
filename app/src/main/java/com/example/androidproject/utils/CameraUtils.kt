package com.example.androidproject.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import com.example.androidproject.App
import com.example.androidproject.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

// Saves photo to device's storage
fun savePhoto(
    controller: LifecycleCameraController,
    onCompleted: () -> Unit
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(App.appContext),
        object: OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val name = photoFileName()

                MediaStore.Images.Media.insertImage(
                    App.appContext.contentResolver,
                    image.toBitmap().rotate(90f),
                    name,
                    name
                )

                image.close()
                onCompleted()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("DBG", "Failed to save photo", exception)
                onCompleted()
            }
        }
    )
}

// Generates a unique file name for the photo based on the current timestamp
private fun photoFileName() =
    SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.UK)
        .format(System.currentTimeMillis()) + ".jpg"

// Rotates a Bitmap image
fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}