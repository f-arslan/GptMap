package com.espressodev.gptmap.core.designsystem.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color(0xFFC2C2C2).copy(alpha = 0.8f),
            Color(0xFFC2C2C2).copy(alpha = 0.1f),
            Color(0xFFC2C2C2).copy(alpha = 0.8f),
        )

        val transition = rememberInfiniteTransition(label = "shimmer transition")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(1000), repeatMode = RepeatMode.Reverse
            ), label = "shimmer animation"
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

val darkBottomOverlayBrush: Brush = Brush.verticalGradient(
    colors = listOf(
        Color.Black.copy(alpha = 0.7f),
        Color.Transparent,
        Color.Transparent,
        Color.Transparent
    ),
    startY = Float.POSITIVE_INFINITY,
    endY = 0f
)
