package com.example.androidproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.androidproject.ui.theme.HopeaSilver
import com.example.androidproject.ui.theme.KesaYellow
import com.example.androidproject.ui.theme.White

// Header of the expandable bottom sheet, which used on the QuestDetailsScreen
// and is always visible, regardless of the sheet's state.
@Composable
fun BottomSheetHeader(
    selectedQuestDescription: String?,
    completedCheckpoints: Int,
    totalCheckpoints: Int,
    onExpandCollapse: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onExpandCollapse)
    ) {
        Text(
            text = selectedQuestDescription ?: "Quest Details",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
            color = White
        )

        Surface(
            color = KesaYellow,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = "$completedCheckpoints / $totalCheckpoints visited",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
