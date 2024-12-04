package com.example.androidproject.ui.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.androidproject.R
import com.example.androidproject.data.models.CheckpointEntity

@Composable
fun BottomSheetContent(
    checkpoints: List<CheckpointEntity>,
    selectedCheckpoint: CheckpointEntity?,
    completableCheckpoints: List<CheckpointEntity>,
    onCheckpointSelected: (CheckpointEntity) -> Unit,
    onCameraClick: (CheckpointEntity) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 8.dp,
            end = 16.dp,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        )
    ) {
        items(checkpoints) { checkpoint ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 4.dp
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    // Highlight selected checkpoint
                    Text(
                        text = checkpoint.name,
                        color = if (checkpoint == selectedCheckpoint) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .clickable { onCheckpointSelected(checkpoint) }
                            .weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    val btnColor = when {
                        checkpoint.completed -> Color.Green
                        checkpoint in completableCheckpoints -> MaterialTheme.colorScheme.primary
                        else -> Color.Gray
                    }

                    Button(
                        onClick = { onCameraClick(checkpoint) },
                        modifier = Modifier
                            .size(76.dp)
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(btnColor),
                        shape = CircleShape,
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        val iconResource = if (checkpoint.completed) {
                            R.drawable.ic_checkpoint_completed
                        } else {
                            R.drawable.baseline_photo_camera_24
                        }

                        Icon(
                            painter = painterResource(id = iconResource),
                            contentDescription = "Take a photo",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    }
}