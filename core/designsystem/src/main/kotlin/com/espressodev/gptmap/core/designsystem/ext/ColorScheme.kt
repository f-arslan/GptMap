package com.espressodev.gptmap.core.designsystem.ext

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ln

/**
 * Calculates the surface tint color at a given elevation.
 *
 * @param elevation The elevation to calculate the color for.
 * @return The calculated color.
 */
fun ColorScheme.surfaceTintColorAtElevation(
    elevation: Dp,
): Color {
    if (elevation == 0.dp) return surfaceVariant
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return surfaceTint.copy(alpha = alpha).compositeOver(surfaceVariant)
}