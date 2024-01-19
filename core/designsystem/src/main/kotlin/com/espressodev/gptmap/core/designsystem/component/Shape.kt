package com.espressodev.gptmap.core.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp

@Composable
private fun shimmerColors(): Array<out Pair<Float, Color>> {
    return arrayOf(
        0.0f to MaterialTheme.colorScheme.primaryContainer,
        0.4f to MaterialTheme.colorScheme.primary,
        1f to MaterialTheme.colorScheme.primaryContainer
    )
}

@Composable
fun LetterInCircle(
    letter: Char,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primaryContainer
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Infinity Transition")
    val rotationAnimation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "Rotation Animation"
    )
    val colors = shimmerColors()
    Surface(
        modifier = modifier
            .drawBehind {
                rotate(rotationAnimation.value) {
                    drawCircle(
                        Brush.horizontalGradient(colorStops = colors),
                        style = Stroke(4.dp.toPx())
                    )
                }
            }
            .padding(2.dp)
            .clip(CircleShape),
        color = color
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = letter.uppercase(), style = MaterialTheme.typography.displayLarge)
        }
    }
}
