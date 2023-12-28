package com.espressodev.gptmap.feature.screenshot

import android.graphics.Bitmap
import android.view.View
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt


@Composable
fun ScreenshotScreen(viewModel: ScreenshotViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.imageResult) {
        if (uiState.imageResult is ImageResult.Success || uiState.imageResult is ImageResult.Error) {
            viewModel.onDialogStateChanged(dialogState = true)
        }
    }

    ScreenshotField(
        uiState = uiState,
        onImageResultChange = viewModel::onImageStateChanged,
        onBitmapStateChange = viewModel::onBitmapStateChanged,
        onCallbackStateChange = viewModel::onCallbackStateChanged
    )

    if (uiState.dialogState)
        ImageAlertDialog(imageResult = uiState.imageResult) {
            viewModel.onDialogStateChanged(dialogState = false)
        }
}

@Composable
private fun ScreenshotField(
    uiState: ScreenshotUiState,
    onImageResultChange: (ImageResult) -> Unit,
    onBitmapStateChange: (Bitmap?) -> Unit,
    onCallbackStateChange: (callback: (() -> Unit)?) -> Unit
) {
    val size = 200.dp
    val localDensity = LocalDensity.current
    val squareSizePx = with(localDensity) { size.toPx() }
    val scope = rememberCoroutineScope()

    // State to hold the position of the square
    val offset = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }

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
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawIntoCanvas {
                val transparentSquare = Path().apply {
                    addRect(
                        Rect(
                            offset = Offset(offset.value.x, offset.value.y),
                            size = Size(squareSizePx, squareSizePx)
                        )
                    )
                }
                clipPath(transparentSquare, clipOp = ClipOp.Difference) {
                    drawRect(SolidColor(Color(0x80000000)))
                }
            }
        }
        Text(text = "Hello AGAIN", modifier = Modifier.align(Alignment.Center))
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
            onImageResultChange = onImageResultChange,
            onBitmapStateChange = onBitmapStateChange,
            onCallbackStateChange = onCallbackStateChange
        )
    }
}

@Composable
fun ScreenshotSquare(
    modifier: Modifier = Modifier,
    uiState: ScreenshotUiState,
    onImageResultChange: (ImageResult) -> Unit,
    onBitmapStateChange: (Bitmap?) -> Unit,
    onCallbackStateChange: (callback: (() -> Unit)?) -> Unit
) {
    val view: View = LocalView.current
    var composableBounds by remember { mutableStateOf<Rect?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
        onCallbackStateChange {
            composableBounds?.let { bounds ->
                if (bounds.width == 0f || bounds.height == 0f) return@let
                scope.launch {
                    // Launch a coroutine to perform the screenshot
                    val imageResult = withContext(Dispatchers.IO) {
                        view.screenshot(bounds)
                    }
                    onImageResultChange(imageResult)

                    if (imageResult is ImageResult.Success) {
                        onBitmapStateChange(imageResult.data)
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            uiState.bitmapState?.let { bmp ->
                if (!bmp.isRecycled) {
                    bmp.recycle()
                }
            }
            onBitmapStateChange(null)
            onCallbackStateChange(null)
        }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned {
                composableBounds = it.boundsInWindow()
            }
    )
}

@Composable
private fun ImageAlertDialog(imageResult: ImageResult, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            FilledTonalButton(onClick = { onDismiss() }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            FilledTonalButton(onClick = { onDismiss() }) {
                Text(text = "Dismiss")
            }
        },
        text = {
            when (imageResult) {
                is ImageResult.Success -> {
                    Image(bitmap = imageResult.data.asImageBitmap(), contentDescription = null)
                }

                else -> {}
            }
        }
    )
}