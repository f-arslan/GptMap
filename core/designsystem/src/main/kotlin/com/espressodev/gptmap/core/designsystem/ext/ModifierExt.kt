package com.espressodev.gptmap.core.designsystem.ext

import android.annotation.SuppressLint
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.unit.dp
import com.espressodev.gptmap.core.designsystem.theme.md_theme_dark_primary
import com.espressodev.gptmap.core.designsystem.theme.md_theme_dark_primaryContainer
import com.espressodev.gptmap.core.designsystem.theme.md_theme_dark_secondary
import com.espressodev.gptmap.core.designsystem.theme.md_theme_dark_tertiary
import com.espressodev.gptmap.core.designsystem.theme.md_theme_light_primary
import com.espressodev.gptmap.core.designsystem.theme.md_theme_light_primaryContainer
import com.espressodev.gptmap.core.designsystem.theme.md_theme_light_tertiary
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language

fun Modifier.clipPolygon(colour: Color): Modifier = drawBehind {
    val trianglePath = Path().apply {
        moveTo(-32f, 0f)
        lineTo(size.width / 2, -size.height / 16)
        lineTo(size.width + 32f, 0f)
        close()
    }

    drawRect(color = colour)
    drawIntoCanvas { canvas ->
        canvas.drawOutline(
            outline = Outline.Generic(trianglePath),
            paint = Paint().apply {
                color = colour
                pathEffect = PathEffect.cornerPathEffect(16.dp.toPx())
            }
        )
    }
}

@SuppressLint("ModifierNodeInspectableProperties")
private data class GradientBackgroundElement(val color: Color) : ModifierNodeElement<GradientBackgroundNode>() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun create() = GradientBackgroundNode(color)
    override fun update(node: GradientBackgroundNode) = Unit
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class GradientBackgroundNode(color: Color) : DrawModifierNode, Modifier.Node() {
    private val shader = RuntimeShader(ANIMATED_WAVE_SHADER)
    private val shaderBrush = ShaderBrush(shader)
    private val time = mutableFloatStateOf(0f)

    init { setBrush(color) }

    private fun setBrush(newColor: Color) {
        shader.setColorUniform(
            "uBaseColor",
            android.graphics.Color.valueOf(
                newColor.red,
                newColor.green,
                newColor.blue,
                newColor.alpha
            )
        )
    }

    override fun ContentDrawScope.draw() {
        shader.setFloatUniform("uResolution", size.width, size.height)
        shader.setFloatUniform("uTime", time.floatValue)
        drawRect(shaderBrush)
        drawContent()
    }

    override fun onAttach() {
        coroutineScope.launch {
            while (true) {
                withInfiniteAnimationFrameMillis {
                    time.floatValue = it / 1000f
                }
            }
        }
    }
}

val lightThemeGradient = Brush.linearGradient(
    colors = listOf(
        md_theme_light_primary,
        md_theme_light_primaryContainer,
        md_theme_light_tertiary
    )
)

val darkThemeGradient = Brush.linearGradient(
    colors = listOf(
        md_theme_dark_primary,
        md_theme_dark_secondary,
        md_theme_dark_tertiary
    )
)

fun Modifier.gradientBackground(isDarkTheme: Boolean): Modifier {
    val gradientBrush = if (isDarkTheme) darkThemeGradient else lightThemeGradient
    val color = if (isDarkTheme) md_theme_dark_primaryContainer else md_theme_light_primaryContainer
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.then(GradientBackgroundElement(color))
    } else {
        drawWithCache {
            onDrawBehind {
                drawRect(gradientBrush)
            }
        }
    }
}

@Language("AGSL")
val ANIMATED_WAVE_SHADER = """
    uniform float2 uResolution; // Viewport resolution
    uniform float uTime; // Current time for animation
    layout(color) uniform half4 uBaseColor; // Base color of the wave
    
    // Function to calculate the color multiplier based on the Y coordinate and a dynamic factor
    float calculateColorMultiplier(float yPos, float dynamicFactor) {
        return step(yPos, 1.0 + dynamicFactor * 2.0) - step(yPos, dynamicFactor - 0.1);
    }

    float4 main(in float2 fragCoord) {
        const float SPEED_MULTIPLIER = 1.5; // Controls the speed of the wave animation
        const float NUM_LOOPS = 14.0; // Number of wave loops
        const float ENERGY = 0.6; // Controls the intensity of the wave
        
        float2 uv = fragCoord / uResolution.xy; // Normalized coordinates
        float3 baseRgbColor = uBaseColor.rgb; // Extract RGB from the base color
        float timeOffset = uTime * SPEED_MULTIPLIER; // Apply time-based offset
        float horizontalAdjustment = uv.x * 4.3; // Horizontal position adjustment for wave
        float3 loopColorAdjustment = (float3(1.0) - baseRgbColor) / NUM_LOOPS; // Color adjustment per loop
        
        for (float i = 1.0; i <= NUM_LOOPS; i += 1.0) {
            float loopFactor = i * 0.1; // Factor to modify each loop individually
            float sinInput = (timeOffset + horizontalAdjustment) * ENERGY; // Input for the sine function
            float curve = sin(sinInput) * (1.0 - loopFactor) * 0.03; // Calculated curve for wave
            float colorMultiplier = calculateColorMultiplier(uv.y, loopFactor); // Dynamic color multiplier
            baseRgbColor += loopColorAdjustment * colorMultiplier; // Adjust the base color
            
            uv.y += curve; // Apply the curve to the Y coordinate
        }
        
        return float4(baseRgbColor, 1.0); // Output the final color with full opacity
    }
""".trimIndent()
