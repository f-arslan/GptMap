package com.espressodev.gptmap.core.designsystem.ext

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
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
