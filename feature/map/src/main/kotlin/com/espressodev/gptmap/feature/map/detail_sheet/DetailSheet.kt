package com.espressodev.gptmap.feature.map.detail_sheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PLUS_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.SMALL_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.VERY_HIGH_PADDING


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailSheet() {
    ModalBottomSheet(onDismissRequest = {}) {

    }
}

@Preview(showBackground = true)
@Composable
fun BottomCard() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clipPolygon(MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = HIGH_PADDING)
                    .padding(bottom = HIGH_PADDING)
            ) {
                Text(
                    text = "Istanbul",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Eminonu, Turkey".uppercase(),
                    modifier = Modifier.offset(y = SMALL_PADDING.times(-1)),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(VERY_HIGH_PADDING))
                Text(
                    text = "\"Spires touch the sky, Bosphorus whispers tales—a city's heartbeat, where continents embrace, Istanbul's poetic dance.\"",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = VERY_HIGH_PADDING.value.sp
                )
                Spacer(modifier = Modifier.height(VERY_HIGH_PADDING))
                Text(
                    text = "Enchanting Istanbul, where East meets West in a vibrant blend of culture and history.",
                    lineHeight = HIGH_PLUS_PADDING.value.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 5,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

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







