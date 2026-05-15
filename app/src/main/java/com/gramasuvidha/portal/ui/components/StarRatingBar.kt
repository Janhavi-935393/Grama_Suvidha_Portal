package com.gramasuvidha.portal.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gramasuvidha.portal.R

@Composable
fun StarRatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    maxRating: Int = 5,
    isReadOnly: Boolean = false,
    starSize: Dp = 32.dp
) {
    Row {
        for (i in 1..maxRating) {
            Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarOutline,
                contentDescription = stringResource(R.string.rating_desc, i),
                tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier
                    .size(starSize)
                    .clickable(enabled = !isReadOnly) { onRatingChanged(i) }
            )
        }
    }
}
