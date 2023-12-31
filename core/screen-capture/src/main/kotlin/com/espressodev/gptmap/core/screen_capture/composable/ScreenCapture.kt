package com.espressodev.gptmap.core.screen_capture.composable

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.media.projection.MediaProjectionManager
import android.util.Log
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.GmDraggableButton
import com.espressodev.gptmap.core.screen_capture.ScreenCaptureService
import com.espressodev.gptmap.core.screen_capture.composable.ScreenState.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun BoxScope.ScreenCapture(viewModel: ScreenCaptureViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Log.i("ScreenCapture", "uiState: $uiState")

    ScreenCaptureScreen(uiState = uiState, onImageCaptured = viewModel::onImageCaptured)
}

@Composable
private fun BoxScope.ScreenCaptureScreen(
    uiState: ScreenCaptureUiState,
    onImageCaptured: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val mediaProjectionManager =
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    val screenCaptureIntent = remember { mediaProjectionManager.createScreenCaptureIntent() }
    val screenshotState = rememberScreenshotState()
    val screenCaptureLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                context.startService(
                    data?.let { intent ->
                        ScreenCaptureService.getStartIntent(
                            context,
                            result.resultCode,
                            intent
                        )
                    }
                )
            }
        }

    LaunchedEffect(screenshotState.imageState) {
        screenshotState.imageState.value.also { imageResult ->
            if (imageResult is ImageResult.Success)
                onImageCaptured(imageResult.data)
        }
    }


    when (uiState.screenState) {
        Initial -> {
            GmDraggableButton(
                icon = GmIcons.CameraFilled,
                onClick = {
                    screenCaptureLauncher.launch(screenCaptureIntent)
                }
            )
        }

        AfterTakingScreenshot -> {
            ScreenshotGallery(screenshotState = screenshotState, uiState.bitmap)
        }

        AfterCapturingTheImage -> {
            uiState.bitmap?.also { bitmap ->
                EditScreenshot(bitmap = bitmap)
            }
        }
    }
}

@Composable
fun EditScreenshot(bitmap: Bitmap) {
}

@Composable
fun ScreenshotGallery(screenshotState: ScreenshotState, bitmap: Bitmap?) {
    val size = 300.dp
    val localDensity = LocalDensity.current
    val squareSizePx = with(localDensity) { size.toPx() }
    val scope = rememberCoroutineScope()

    // State to hold the position of the square
    val offset = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(4f)
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
        bitmap?.let {
            Image(
                bitmap = bitmap.asImageBitmap(),
                null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
        Canvas(
            modifier = Modifier
                .matchParentSize()
        ) {
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
        ScreenshotBox(
            modifier = Modifier
                .offset {
                    IntOffset(
                        offset.value.x.roundToInt(),
                        offset.value.y.roundToInt()
                    )
                }
                .size(size),
            screenshotState = screenshotState
        )
        ExtendedFloatingActionButton(
            onClick = { screenshotState.capture() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text(text = stringResource(id = AppText.capture))
        }
    }
}

@Composable
fun ScreenshotBox(
    modifier: Modifier = Modifier,
    screenshotState: ScreenshotState,
) {
    val view: View = LocalView.current
    var composableBounds by remember { mutableStateOf<Rect?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(screenshotState) {
        screenshotState.callback = {
            composableBounds?.let { bounds ->
                if (bounds.width == 0f || bounds.height == 0f) return@let
                scope.launch {
                    // Launch a coroutine to perform the screenshot
                    val imageResult = withContext(Dispatchers.IO) {
                        view.screenshot(bounds)
                    }
                    screenshotState.imageState.value = imageResult

                    if (imageResult is ImageResult.Success) {
                        screenshotState.bitmapState.value = imageResult.data
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            screenshotState.bitmapState.value?.let { bmp ->
                if (!bmp.isRecycled) {
                    bmp.recycle()
                }
            }
            screenshotState.bitmapState.value = null
            screenshotState.callback = null
        }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned {
                composableBounds = it.boundsInWindow()
            }
    )
}