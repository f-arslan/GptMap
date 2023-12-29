package com.espressodev.gptmap.feature.screenshot

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt



@Composable
fun ScreenshotCaptureArea(
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    onImageCaptured: (Bitmap) -> Unit
) {
    val localDensity = LocalDensity.current
    val offset = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }
    val squareSizePx = with(localDensity) { size.toPx() }
    val scope = rememberCoroutineScope()
    var captureTrigger by remember { mutableStateOf(value = false) }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    scope.launch {
                        val parentSize = this@pointerInput.size
                        val newX = (offset.value.x + dragAmount.x)
                            .coerceIn(0f, parentSize.width - squareSizePx)
                        val newY = (offset.value.y + dragAmount.y)
                            .coerceIn(0f, parentSize.height - squareSizePx)
                        offset.snapTo(Offset(newX, newY))
                    }
                }
            }
    ) {
        CaptureComposableAsImage(
            modifier = Modifier
                .offset {
                    IntOffset(
                        offset.value.x.roundToInt(),
                        offset.value.y.roundToInt()
                    )
                }
                .size(size)
                .border(2.dp, Color.Black),
            onImageCaptured = onImageCaptured,
            captureTrigger = captureTrigger,
            onCaptureTriggerChange = { captureTrigger = it }
        ) {
            // Content to capture
            // This is where you put the content that you want to be able to capture
        }

        // Button to trigger the screenshot
        Button(
            onClick = { captureTrigger = true },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Text(text = "Capture")
        }
    }
}

@Composable
fun BoxScope.CaptureComposableAsImage(
    modifier: Modifier,
    onImageCaptured: (Bitmap) -> Unit,
    captureTrigger: Boolean,
    onCaptureTriggerChange: (Boolean) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val view = LocalView.current
    var bounds by remember { mutableStateOf<android.graphics.Rect?>(null) }

    // Box to capture the bounds and content
    Box(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                bounds = layoutCoordinates.boundsInWindow().toAndroidRect()
            },
        content = content
    )

    LaunchedEffect(captureTrigger) {
        if (captureTrigger && bounds != null) {
            val bitmap = Bitmap.createBitmap(
                bounds!!.width(),
                bounds!!.height(),
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(bitmap)
            canvas.translate(-bounds!!.left.toFloat(), -bounds!!.top.toFloat())
            view.draw(canvas)
            Log.d("Screenshot", "Captured ${bitmap.width}")
            onImageCaptured(bitmap)
            onCaptureTriggerChange(false)
        }
    }
}

// Extension function to convert Compose Rect to Android Rect
fun Rect.toAndroidRect(): android.graphics.Rect {
    return android.graphics.Rect(
        left.roundToInt(),
        top.roundToInt(),
        right.roundToInt(),
        bottom.roundToInt()
    )
}

@Composable
fun ScreenshotScreen(
    viewModel: ScreenshotViewModel = hiltViewModel(),
    content: @Composable BoxScope.() -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.bitmapState) {
        if (uiState.bitmapState != null) {
            viewModel.onEvent(ScreenshotEvent.OnDialogStateChanged(true))
        }
    }

    val size = 200.dp
    val offset = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }
    val localDensity = LocalDensity.current
    val squareSizePx = with(localDensity) { size.toPx() }
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    scope.launch {
                        val parentSize = this@pointerInput.size
                        val newX = (offset.value.x + dragAmount.x)
                            .coerceIn(0f, parentSize.width - squareSizePx)
                        val newY = (offset.value.y + dragAmount.y)
                            .coerceIn(0f, parentSize.height - squareSizePx)
                        offset.snapTo(Offset(newX, newY))
                    }
                }
            }
            .background(Color.Yellow),
    ) {
        ScreenshotField(
            uiState = uiState,
            event = viewModel::onEvent,
            content = content
        )
    }
}

@Composable
private fun BoxScope
    .ScreenshotField(
    uiState: ScreenshotUiState,
    event: (ScreenshotEvent) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val size = 200.dp
    val localDensity = LocalDensity.current
    val squareSizePx = with(localDensity) { size.toPx() }
    val scope = rememberCoroutineScope()

    // State to hold the position of the square
    val offset = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }


//        Canvas(modifier = Modifier.matchParentSize()) {
//            drawIntoCanvas {
//                val transparentSquare = Path().apply {
//                    addRect(
//                        Rect(
//                            offset = Offset(offset.value.x, offset.value.y),
//                            size = Size(squareSizePx, squareSizePx)
//                        )
//                    )
//                }
//                clipPath(transparentSquare, clipOp = ClipOp.Difference) {
//                    drawRect(SolidColor(Color(0x80000000)))
//                }
//            }
//        }
    ScreenshotSquare(
        modifier = Modifier
            .offset {
                IntOffset(
                    offset.value.x.roundToInt(),
                    offset.value.y.roundToInt()
                )
            }
            .size(size),
        uiState = uiState,
        event = event,
        content = content
    )
    Button(
        onClick = { event(ScreenshotEvent.OnCaptureTriggerStateChanged(true)) },
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(8.dp)
    ) {
        Text(text = "Capture")
    }

}

@Composable
fun ScreenshotSquare(
    modifier: Modifier = Modifier,
    uiState: ScreenshotUiState,
    event: (ScreenshotEvent) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val view = LocalView.current
    var bounds by remember { mutableStateOf<android.graphics.Rect?>(null) }
    Box(modifier = Modifier.fillMaxSize(), content = content)
    // Invisible box to capture the bounds
    Box(
        modifier = modifier
            .onGloballyPositioned { layoutCoordinates ->
                bounds = layoutCoordinates
                    .boundsInWindow()
                    .toAndroidRect()
            },
    )

    LaunchedEffect(uiState.captureTriggerState) {
        if (uiState.captureTriggerState && bounds != null) {
            val bitmap = Bitmap.createBitmap(
                bounds!!.width(),
                bounds!!.height(),
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(bitmap)
            // Translate the canvas to the top-left corner of the bounds before drawing
            canvas.translate(-bounds!!.left.toFloat(), -bounds!!.top.toFloat())
            view.draw(canvas)
            event(ScreenshotEvent.OnBitmapStateChanged(bitmap))
            event(ScreenshotEvent.OnCaptureTriggerStateChanged(false))
        }
    }

}