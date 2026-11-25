package com.pixelfrost.launcher.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun GrainOverlay(
    modifier: Modifier = Modifier,
    alpha: Float = 0.025f,
) {
    val dots = remember {
        val random = Random(42)
        List(1500) {
            Triple(random.nextFloat(), random.nextFloat(), random.nextBoolean())
        }
    }
    Canvas(modifier = modifier) {
        val dotSize = 1.dp.toPx()
        dots.forEach { (x, y, light) ->
            drawRect(
                color = if (light) Color.White.copy(alpha = alpha) else Color.Black.copy(alpha = alpha),
                topLeft = Offset(x * size.width, y * size.height),
                size = Size(dotSize, dotSize),
            )
        }
    }
}
