package com.example.androidproject.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.androidproject.R

// This toolbar is used in conjunction with CameraPreviw and contains row with camera controls,
// like photo capture button and close button.
@Composable
fun CameraControls(
    onPhotoCapture: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "", modifier = Modifier
            .size(64.dp))
        IconButton(onClick = onPhotoCapture) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                contentDescription = "Take photo",
                tint = Color.White,
                modifier = Modifier
                    .size(64.dp)
            )
        }
        IconButton(onClick = onClose) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_close_24),
                contentDescription = "Close camera",
                tint = Color.White,
                modifier = Modifier
                    .size(64.dp)
            )
        }
    }
}