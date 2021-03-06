package com.puculek.pulltorefresh

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.CombinedModifier
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp


private const val MAX_OFFSET = 400f
private const val MIN_REFRESH_OFFSET = 250f
private const val PERCENT_INDICATOR_PROGRESS_ON_DRAG = 0.85f
private const val BASE_OFFSET = -48


/**
 * A layout composable with [content].
 *
 * Example usage:
 * @sample com.puculek.pulltorefresh.samples.PullToRefreshPreview
 *
 * @param modifier The modifier to be applied to the layout.
 * @param isRefreshing Flag describing if [PullToRefresh] is refreshing.
 * @param onRefresh Callback to be called if layout is pulled to refresh.
 * @param content The content of the [PullToRefresh].
 */
@Composable
fun PullToRefresh(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    var indicatorOffset by remember { mutableStateOf(0f) }

    // check if isRefreshing has been changed
    var isRefreshingInternal by remember { mutableStateOf(false) }

    // User cancelled dragging before reaching MAX_REFRESH_OFFSET
    var isResettingScroll by remember { mutableStateOf(false) }

    // How much should indicator scroll to reset its position
    var scrollToReset by remember { mutableStateOf(0f) }

    // Trigger for scaling animation
    var isFinishingRefresh by remember { mutableStateOf(false) }

    val scaleAnimation by animateFloatAsState(
        targetValue = if (isFinishingRefresh) 0f else 1f,
        finishedListener = {
            indicatorOffset = 0f
            isFinishingRefresh = false
        })
    val offsetAnimation by animateFloatAsState(
        targetValue = if (isRefreshing || isFinishingRefresh) {
            indicatorOffset - MIN_REFRESH_OFFSET
        } else {
            0f
        }
    )
    val resettingScrollOffsetAnimation by animateFloatAsState(
        targetValue = if (isResettingScroll) {
            scrollToReset
        } else {
            0f
        },
        finishedListener = {
            if (isResettingScroll) {
                indicatorOffset = 0f
                isResettingScroll = false
            }
        })

    if (isResettingScroll) {
        indicatorOffset -= resettingScrollOffsetAnimation
    }
    if (!isRefreshing && isRefreshingInternal) {
        isFinishingRefresh = true
        isRefreshingInternal = false
    }
    if (isRefreshing && !isRefreshingInternal) {
        isRefreshingInternal = true
    }

    val nestedScrollConnection = object : NestedScrollConnection {

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (!isRefreshing && source == NestedScrollSource.Drag) {
                val diff = if (indicatorOffset + available.y > MAX_OFFSET) {
                    available.y - (indicatorOffset + available.y - MAX_OFFSET)
                } else if (indicatorOffset + available.y < 0) {
                    0f
                } else {
                    available.y
                }
                indicatorOffset += diff
                return Offset(0f, diff)
            }
            return super.onPostScroll(consumed, available, source)
        }

        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (!isRefreshing && source == NestedScrollSource.Drag) {
                if (available.y < 0 && indicatorOffset > 0) {
                    val diff = if (indicatorOffset + available.y < 0) {
                        indicatorOffset = 0f
                        indicatorOffset
                    } else {
                        indicatorOffset += available.y
                        available.y
                    }
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
                if (indicatorOffset > MIN_REFRESH_OFFSET) {
                    onRefresh()
                    isRefreshingInternal = true
                } else {
                    isResettingScroll = true
                    scrollToReset = indicatorOffset
                }
            }
            return super.onPostFling(consumed, available)
        }
    }

    Box(
        modifier = CombinedModifier(
            inner = Modifier.nestedScroll(nestedScrollConnection),
            outer = modifier
        )
    ) {
        content()

        val offsetPx = if (isRefreshing || isFinishingRefresh) {
            offsetAnimation
        } else {
            0f
        }
        val absoluteOffset = BASE_OFFSET.dp + with(LocalDensity.current) {
            (indicatorOffset - offsetPx).toDp()
        }

        PullToRefreshProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .absoluteOffset(y = absoluteOffset)
                .scale(scaleAnimation)
                .rotate(if (indicatorOffset > MIN_REFRESH_OFFSET) (indicatorOffset - MIN_REFRESH_OFFSET) else 0f),
            progress = if (!isRefreshing) indicatorOffset / MAX_OFFSET * PERCENT_INDICATOR_PROGRESS_ON_DRAG else null
        )
    }
}