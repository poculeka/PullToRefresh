package com.puculek.pulltorefresh

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun PullToRefreshProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.padding(4.dp),
            shape = CircleShape,
            elevation = 6.dp,
            backgroundColor = Color.White
        ) {
            if (progress == null) {
                CircularProgressIndicator(
                    modifier = Modifier.scale(.66f),
                    strokeWidth = ProgressIndicatorDefaults.StrokeWidth * 1.25f,
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.scale(.66f),
                    strokeWidth = ProgressIndicatorDefaults.StrokeWidth * 1.25f,
                    progress = progress
                )
            }
        }
    }
}