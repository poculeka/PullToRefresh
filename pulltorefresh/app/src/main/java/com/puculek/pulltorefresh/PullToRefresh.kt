package com.puculek.pulltorefresh

import android.util.Log
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.CombinedModifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.delay


@Composable
fun SwipeRefreshLayout(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    val max by remember { mutableStateOf(400f) }
    val minimumOffsetToRefresh by remember { mutableStateOf(250f) }
    var d by remember { mutableStateOf(0f) }
    var isRefreshingInternal by remember { mutableStateOf(false) }
    var isFinishingRefresh by remember { mutableStateOf(false) }
    var isResettingScroll by remember { mutableStateOf(false) }
    var scrollToReset by remember { mutableStateOf(0f) }
    Log.d("FFVII", "d: $d")
    val scale by animateFloatAsState(
        targetValue = if (isFinishingRefresh) 0f else 1f,
        finishedListener = {
            Log.d("FFVII", "finished")
            d = 0f
            isFinishingRefresh = false
        })
    val offset by animateFloatAsState(
        targetValue = if (isRefreshing || isFinishingRefresh) d - minimumOffsetToRefresh else 0f
    )
    val resettingScrollOffset by animateFloatAsState(
        targetValue = if (isResettingScroll) scrollToReset else 0f,
        finishedListener = {
            Log.d("FFVII", "finishedListener")
            if (isResettingScroll) {
                d = 0f
                isResettingScroll = false
            }
        })

    if (isResettingScroll) {
        d -= resettingScrollOffset
    }

    if (!isRefreshing && isRefreshingInternal) {
        isFinishingRefresh = true
        isRefreshingInternal = false
    }

    val inner = Modifier
        .nestedScroll(
            object : NestedScrollConnection {
                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    Log.d("FFVII", "postScroll: consumed: $consumed, available: $available")
                    if (!isRefreshing && source == NestedScrollSource.Drag) {
                        val diff = if (d + available.y > max) {
                            available.y - (d + available.y - max)
                        } else {
                            available.y
                        }
                        d += diff
                        return Offset(0f, diff)
                    }
                    return super.onPostScroll(consumed, available, source)
                }

                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    if (!isRefreshing && source == NestedScrollSource.Drag) {
                        if (available.y < 0 && d > 0) {
                            val diff = if (d + available.y < 0) {
                                Log.d("FFVII", "prescroll")
                                d = 0f
                                d
                            } else {
                                d += available.y
                                available.y
                            }
//                            d += diff
                            isFinishingRefresh = false
                            return Offset.Zero.copy(y = diff)
                        }
                    }
                    return super.onPreScroll(available, source)
                }

                override suspend fun onPostFling(
                    consumed: Velocity,
                    available: Velocity
                ): Velocity {
                    if (!isRefreshing) {
                        if (d > minimumOffsetToRefresh) {
                            onRefresh()
                            Log.d("FFIX", "onRefresh")
                            isRefreshingInternal = true
                        } else {
                            isResettingScroll = true
                            scrollToReset = d
//                        d = 0f
                        }
                    }
                    return super.onPostFling(consumed, available)
                }
            }
        )

    Box(
        modifier = CombinedModifier(
            inner = inner,
            outer = modifier
        )
    ) {
        content()

        val absoluteOffset = with(LocalDensity.current) {
            val offsetPx = if (isRefreshing || isFinishingRefresh) {
                offset
            } else {
                0f
            }
            (-48).dp + (d - offsetPx).toDp()
        }

        PullToRefreshProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .absoluteOffset(y = absoluteOffset)
                .scale(scale)
                .rotate(if (d > 200f) (d - 200) else 0f),
            progress = if (!isRefreshing) d / max * 0.85f else null
        )
    }
}

@Preview
@Composable
fun SwipeRefreshLayoutPreview() {
    var isRefreshing: Boolean by remember { mutableStateOf(false) }
    Log.d("FFVII", "isRefreshing: $isRefreshing")
    val scope = rememberCoroutineScope()
    SwipeRefreshLayout(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            scope.launch {
                delay(1000)
                isRefreshing = false
            }

        }
    ) {
        LazyColumn(
        ) {
            (0..20).map {
                item {
                    Text(
                        text = "scrollState: ${1} WSLFDKFSDL SDLFK S:DLF KSD:LF KSD:LF KSDL:FK SLG KERGS K: $it",
                        modifier = Modifier
                            .padding(64.dp)
                            .background(Color.Cyan)
                            .padding(24.dp)
                    )
                }
            }
        }
    }
}