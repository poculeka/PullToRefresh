package com.puculek.pulltorefresh.samples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.puculek.pulltorefresh.ProgressIndicatorWithArrow

@Preview
@Composable
fun ProgressIndicatorWithArrowPreview() {

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        (0..10).map {
            ProgressIndicatorWithArrow(
                progress = it.toFloat() / 10,
                modifier = Modifier
                    .background(Color.Magenta),
                color = Color.Cyan
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}