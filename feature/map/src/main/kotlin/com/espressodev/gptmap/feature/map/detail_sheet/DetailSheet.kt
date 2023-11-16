package com.espressodev.gptmap.feature.map.detail_sheet

import android.graphics.PointF
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailSheet() {
    ModalBottomSheet(onDismissRequest = {}) {

    }
}

val PurpleBackgroundColor = Color(0XFF2c1d40)
val BarColor = Color(0xFF534568)

@Preview(showBackground = true)
@Composable
fun Chart() {
    Box(
        modifier = Modifier
            .background(PurpleBackgroundColor)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val animationProgress = remember {
            Animatable(0f)
        }
        LaunchedEffect(key1 = graphData) {
            animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 3000)
            )
        }
        Spacer(
            modifier = Modifier
                .padding(8.dp)
                .aspectRatio(3 / 2f)
                .fillMaxSize()
                .drawWithCache {
                    val path = generatePath(graphData, size)
                    val filledPath = Path()

                    filledPath.addPath(path)
                    filledPath.lineTo(size.width, size.height)
                    filledPath.close()

                    val brush = Brush.verticalGradient(
                        listOf(Color.Green.copy(alpha = 0.4f), Color.Transparent)
                    )

                    onDrawBehind {
                        val barWidthPx = 1.dp.toPx()
                        drawRect(BarColor, style = Stroke(barWidthPx))

                        val verticalLines = 4
                        val verticalSize = size.width / (verticalLines + 1)
                        repeat(verticalLines) { i ->
                            val startX = verticalSize * (i + 1)
                            drawLine(
                                BarColor,
                                start = Offset(startX, 0f),
                                end = Offset(startX, size.height),
                                strokeWidth = barWidthPx
                            )
                        }

                        val horizontalLines = 3
                        val sectionSize = size.height / (horizontalLines + 1)
                        repeat(horizontalLines) { i ->
                            val startY = sectionSize * (i + 1)
                            drawLine(
                                BarColor,
                                start = Offset(0f, startY),
                                end = Offset(size.width, startY),
                                strokeWidth = barWidthPx
                            )
                        }
                        clipRect(right = size.width * animationProgress.value) {
                            drawPath(filledPath, brush = brush, style = Fill)
                            drawPath(path, Color.Green, style = Stroke(2.dp.toPx()))
                        }
                    }
                }
        )
    }
}

val graphData: List<Float> = listOf(0.1f, 0.3f, 0.4f, 0.8f, 1f)

fun generatePath(graphData: List<Float>, size: Size): Path {
    val path = Path()

    // Ensure graphData has at least two points
    if (graphData.size < 2) {
        // Handle the case when there are not enough data points
        return path
    }

    val xInterval = size.width / (graphData.size - 1)

    // Move to the initial point
    path.moveTo(0f, size.height)

    for (i in 0 until graphData.size - 1) {
        val x = xInterval * (i + 1)
        val y = size.height - (graphData[i + 1] * size.height)

        val previousX = xInterval * i
        val previousY = size.height - (graphData[i] * size.height)

        // Calculate control points for a smooth curve
        val controlPoint1 = PointF(previousX + xInterval / 4, previousY)
        val controlPoint2 = PointF(x - xInterval / 4, y)

        // Draw the cubic Bezier curve
        path.cubicTo(
            controlPoint1.x, controlPoint1.y,
            controlPoint2.x, controlPoint2.y,
            x, y
        )
    }


    return path
}


fun generatePathNaive(graphData: List<Float>, size: Size): Path {
    val path = Path()

    // Calculate the distance between each point
    val xInterval = size.width / (graphData.size)
    path.moveTo(x = 0f, y = size.height)

    graphData.forEachIndexed { i, balance ->
        // Calculate the x position for each point
        val x = (i + 1) * xInterval

        // Assuming that 'balance' is a percentage of the total height
        val y = size.height * (1 - balance)


        path.lineTo(x, y)
    }
    return path
}


















