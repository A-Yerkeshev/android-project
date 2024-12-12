package com.example.androidproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.androidproject.R
import com.example.androidproject.data.models.CheckpointEntity

// Content of the expandable bottom sheet, used on the QuestDetailsScreen.
// It holds list of checkpoints of the current quest and allows user to set
// camera to the checkpoint's location by tapping it.
// Each checkpoint has an associated indicator with 3 states:
// - Active camera - user is within the checkpoint's radius and can take a photo;
// - Disabled camera - user is not within the checkpoint's radius;
// - Completed - this checkpoint has already been completed.

@Composable
fun BottomSheetContent(
    checkpoints: List<CheckpointEntity>,
    selectedCheckpoint: CheckpointEntity?,
    completableCheckpoints: List<CheckpointEntity>,
    onCheckpointSelected: (CheckpointEntity) -> Unit,
    onCameraClick: (CheckpointEntity) -> Unit
) {
    LocalContext.current

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
                    .padding(vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)), // Add background here
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 4.dp
                ),
                shape = RoundedCornerShape(8.dp)
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