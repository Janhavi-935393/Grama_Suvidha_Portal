package com.gramasuvidha.portal.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gramasuvidha.portal.R

@Composable
fun ProgressBarColored(progress: Int, showLabel: Boolean = false) {
    val color = when {
        progress >= 100 -> Color(0xFF4CAF50)
        progress > 50 -> Color(0xFF2196F3)
        progress > 20 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }

    Column {
        if (showLabel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(R.string.progress), style = MaterialTheme.typography.labelSmall)
                Text(text = stringResource(R.string.progress_value, progress), style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        LinearProgressIndicator(
            progress = progress / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
