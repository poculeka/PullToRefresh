package com.puculek.pulltorefresh

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class PullToRefreshTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testShouldRefresh() {
        val keyLabel = "0"
        val maxOffset = 300f
        val minOffsetToRefresh = 150f
        var onRefreshCalled = false

        composeTestRule.setContent {
            PullToRefresh(
                isRefreshing = false,
                maxOffset = maxOffset,
                minRefreshOffset = minOffsetToRefresh,
                onRefresh = {
                    onRefreshCalled = true
                }, content = {
                    Content()
                })
        }

        composeTestRule
            .onNodeWithText(keyLabel).performGesture {
                swipe(Offset.Zero, Offset.Zero.copy(y = maxOffset))
            }
        composeTestRule.waitForIdle()
        Assert.assertEquals(onRefreshCalled, true)
    }

    @Test
    fun testShouldNotRefresh() {
        val keyLabel = "0"
        val maxOffset = 300f
        val minOffsetToRefresh = 150f
        val offset = 100f
        var onRefreshCalled: Boolean = false

        composeTestRule.setContent {
            PullToRefresh(
                isRefreshing = false,
                maxOffset = maxOffset,
                minRefreshOffset = minOffsetToRefresh,
                onRefresh = {
                    onRefreshCalled = true
                }, content = {
                    Content()
                })
        }

        composeTestRule
            .onNodeWithText(keyLabel).performGesture {
                swipe(Offset.Zero, Offset.Zero.copy(y = offset))
            }
        composeTestRule.waitForIdle()
        Assert.assertFalse(onRefreshCalled)
    }


    @Composable
    fun Content() {
        LazyColumn {
            (0..10).map {
                item {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .background(Color.Yellow)
                            .padding(16.dp)
                    ) {
                        Text("$it", fontSize = 24.sp)
                        Text(
                            text = """
                            Lorem ipsum dolor sit amet, consectetur adipiscing elit,
                            ssed do eiusmod tempor incididunt ut labore et dolore magna aliqua
                        """.trimIndent()
                        )
                    }
                }
            }
        }
    }
}