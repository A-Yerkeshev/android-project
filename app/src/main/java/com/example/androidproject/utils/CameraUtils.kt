package com.example.androidproject.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.content.Context
import android.os.Environment
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
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

// Saves photo to device's storage
fun savePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onCompleted: (String) -> Unit
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(App.appContext),
        object: OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val name = photoFileName()

                val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val photoFile = File(picturesDir, name)

                val bitmap = image.toBitmap()
                image.close()

                try {
                    FileOutputStream(photoFile).use { fos ->
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, fos)
                    }
                    onCompleted(photoFile.absolutePath)
                } catch (e: Exception) {
                    Log.e("DBG", "Failed to save photo to file", e)
                    onCompleted("") // Return empty path
                }
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