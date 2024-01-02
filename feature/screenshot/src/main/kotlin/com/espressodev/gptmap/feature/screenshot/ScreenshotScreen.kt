package com.espressodev.gptmap.feature.screenshot

import android.util.Log
import android.view.View
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.R
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.feature.screenshot.ScreenState.AfterSelectingTheField
import com.espressodev.gptmap.feature.screenshot.ScreenState.Initial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun ScreenshotScreen(
    viewModel: ScreenshotViewModel = hiltViewModel(),
    popUp: () -> Unit,
    navigateToImageAnalysis: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            GmTopAppBar(
                title = AppText.edit_screenshot,
                icon = GmIcons.ScreenshotDefault,
                onBackClick = popUp
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    when (uiState.screenState) {
                        Initial -> InitialBottomBar(onClick = { viewModel.onEvent(ScreenshotUiEvent.OnCaptureClicked) })
                        AfterSelectingTheField -> AfterBottomBar(
                            onCancelClick = popUp,
                            onSaveClick = { viewModel.onEvent(ScreenshotUiEvent.OnSaveClicked) }
                        )
                    }
                }
            }
        }
    ) {
        ScreenCaptureScreen(
            uiState = uiState,
            modifier = Modifier.padding(it),
            onEvent = { viewModel.onEvent(it, navigateToImageAnalysis = navigateToImageAnalysis) }
        )
    }
}


@Composable
private fun InitialBottomBar(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = stringResource(id = R.string.capture))
    }
}

@Composable
private fun AfterBottomBar(onCancelClick: () -> Unit, onSaveClick: () -> Unit) {
    OutlinedButton(onClick = onCancelClick) {
        Icon(imageVector = GmIcons.CancelOutlined, stringResource(id = R.string.cancel))
    }
    Spacer(modifier = Modifier.width(16.dp))
    Button(onClick = onSaveClick) {
        Icon(imageVector = GmIcons.DoneDefault, stringResource(id = R.string.done))
    }
}


@Composable
private fun ScreenCaptureScreen(
    uiState: ScreenshotUiState,
    modifier: Modifier = Modifier,
    onEvent: (ScreenshotUiEvent) -> Unit
) {
    when (uiState.screenState) {
        Initial -> {
            ScreenshotGallery(
                modifier = modifier,
                onEvent = onEvent,
                screenState = uiState.screenState
            )
        }

        AfterSelectingTheField -> {
            EditScreenshot(
                imageResult = uiState.imageResult,
                modifier = modifier
            )
        }
    }
}

@Composable
fun EditScreenshot(
    modifier: Modifier = Modifier,
    imageResult: ImageResult,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (imageResult is ImageResult.Success) {
            Image(
                bitmap = imageResult.data.asImageBitmap(),
                contentDescription = stringResource(id = R.string.selected_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ScreenshotGallery(
    modifier: Modifier = Modifier,
    onEvent: (ScreenshotUiEvent) -> Unit,
    screenState: ScreenState
) {
    val size = 300.dp
    val localDensity = LocalDensity.current
    val squareSizePx = with(localDensity) { size.toPx() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val imagePath = remember {
        context.getExternalFilesDir(null)?.absolutePath?.let { "${it}/screenshots/screenshot.png" }
    }
    val offset = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }

    Box(
        modifier = modifier
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
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imagePath)
                .build(),
            contentDescription = stringResource(id = R.string.selected_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
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
            onEvent = onEvent,
            screenState = screenState
        )
    }
}

@Composable
fun ScreenshotBox(
    modifier: Modifier = Modifier,
    onEvent: (ScreenshotUiEvent) -> Unit,
    screenState: ScreenState,
) {
    val view: View = LocalView.current
    var composableBounds by remember { mutableStateOf<Rect?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(screenState) {
        onEvent(ScreenshotUiEvent.OnCallbackChanged {
            composableBounds?.let { bounds ->
                if (bounds.width == 0f || bounds.height == 0f) return@let
                scope.launch {
                    // Launch a coroutine to perform the screenshot
                    val imageResult = withContext(Dispatchers.IO) {
                        view.screenshot(bounds)
                    }
                    onEvent(ScreenshotUiEvent.OnImageResultChanged(imageResult))
                    if (imageResult is ImageResult.Success) {
                        onEvent(ScreenshotUiEvent.OnBitmapChanged(imageResult.data))
                    }
                }
            }
        })
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(ScreenshotUiEvent.OnBitmapChanged(null))
            onEvent(ScreenshotUiEvent.OnCallbackChanged(null))
        }
    }

    Box(
        modifier = modifier
            .onGloballyPositioned {
                composableBounds = it.boundsInWindow()
            }
    )
}