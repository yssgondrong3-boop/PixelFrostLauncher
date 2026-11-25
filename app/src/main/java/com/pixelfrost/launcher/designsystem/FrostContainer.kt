package com.pixelfrost.launcher.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

@Composable
fun FrostContainer(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 32.dp,
    blurRadius: Dp = 20.dp,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    val highlightBorder = Brush.linearGradient(
        listOf(
            Color.White.copy(alpha = 0.55f),
            Color.White.copy(alpha = 0.15f),
        )
    )
    Box(
        modifier = modifier
            .shadow(elevation = 12.dp, shape = shape, clip = false)
            .clip(shape)
            .hazeEffect(
                state = hazeState,
                style = HazeStyle(
                    backgroundColor = Color.Transparent,
                    tint = HazeTint(Color.White.copy(alpha = 0.18f)),
                    blurRadius = blurRadius,
                ),
            )
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, highlightBorder, shape),
    ) {
        GrainOverlay(modifier = Modifier.matchParentSize())
        content()
    }
}
