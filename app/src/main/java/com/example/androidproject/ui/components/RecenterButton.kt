package com.example.androidproject.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.androidproject.R

@Composable
fun RecenterButton(
    context: Context,
    isLiveTrackingAvailable: Boolean?,
    isLiveTrackingSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            if (isLiveTrackingAvailable != false) {
//                isLiveTrackingSelected = !isLiveTrackingSelected
                onClick()
            } else {
                Toast.makeText(context, "Live tracking is currently unavailable", Toast.LENGTH_LONG).show()
            }
        },
        modifier = modifier
            .size(76.dp)
//            .align(Alignment.BottomEnd)
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                isLiveTrackingAvailable == false -> Color.Gray
                isLiveTrackingSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surface
            },
//            if (isLiveTrackingSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = when {
                isLiveTrackingAvailable == false -> MaterialTheme.colorScheme.primary
                isLiveTrackingSelected -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onPrimary
            }
//            if (isLiveTrackingSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
        ),
        shape = CircleShape,
        contentPadding = PaddingValues(4.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_rounded_my_location),
            contentDescription = "Center to my position",
            tint = if (isLiveTrackingAvailable != false) {
                if (isLiveTrackingSelected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onPrimary
            },
//            (isLiveTrackingSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(36.dp)
        )
    }
}