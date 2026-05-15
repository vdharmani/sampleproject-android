package com.vdharmani.starter.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Full-screen scrim with a centered progress spinner. Render conditionally on
 * the same Box that holds your screen content:
 *
 * ```kotlin
 * Box {
 *     ScreenContent(...)
 *     if (state.isLoading) AppLoader()
 * }
 * ```
 */
@Composable
fun AppLoader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f)),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}
