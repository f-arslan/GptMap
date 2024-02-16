package com.espressodev.gptmap.feature.screenshot

import android.view.View
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.R
import com.espressodev.gptmap.core.designsystem.TextType
import com.espressodev.gptmap.core.designsystem.component.DefaultTextField
import com.espressodev.gptmap.core.designsystem.component.GmProgressIndicator
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.feature.screenshot.ScreenState.AfterSelectingTheField
import com.espressodev.gptmap.feature.screenshot.ScreenState.Initial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenshotRoute(
    popUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScreenshotViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onCaptureClickEvent by rememberUpdatedState(
        newValue = { viewModel.onEvent(ScreenshotUiEvent.OnCaptureClicked) },
    )
    val onSaveClickEvent by rememberUpdatedState(
        newValue = { viewModel.onEvent(ScreenshotUiEvent.OnSaveClicked, popUp) },
    )
    Scaffold(
        topBar = {
            GmTopAppBar(
                text = TextType.Res(AppText.edit_screenshot),
                icon = IconType.Vector(GmIcons.ScreenshotFilled),
                onBackClick = popUp,
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ) {
                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    when (uiState.screenState) {
                        Initial ->
                            Button(onClick = onCaptureClickEvent) {
                                Text(text = stringResource(id = R.string.capture))
                            }

                        AfterSelectingTheField ->
                            AfterBottomBar(
                                onCancelClick = popUp,
                                onSaveClick = {
                                    onSaveClickEvent()
                                },
                            )
                    }
                }
            }
        },
        modifier = modifier.statusBarsPadding(),
    ) {
        ScreenshotScreen(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(it),
        )
    }

    if (uiState.isSaveStateStarted) {
        GmProgressIndicator()
    }
}

@Composable
private fun AfterBottomBar(
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        OutlinedButton(onClick = onCancelClick) {
            Icon(imageVector = GmIcons.CancelOutlined, stringResource(id = R.string.cancel))
        }
        Spacer(Modifier.width(16.dp))
        Button(onClick = onSaveClick) {
            Icon(imageVector = GmIcons.DoneDefault, stringResource(id = R.string.done))
        }
    }
}

@Composable
private fun ScreenshotScreen(
    uiState: ScreenshotUiState,
    onEvent: (ScreenshotUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState.screenState) {
        Initial -> {
            ScreenshotGallery(
                modifier = modifier,
                onEvent = onEvent,
                screenState = uiState.screenState,
            )
        }

        AfterSelectingTheField -> {
            EditScreenshot(
                title = uiState.title,
                imageResult = uiState.imageResult,
                onValueChange = { onEvent(ScreenshotUiEvent.OnTitleChanged(it)) },
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun EditScreenshot(
    title: String,
    imageResult: ImageResult,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (imageResult is ImageResult.Success) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier =
            modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
        ) {
            DefaultTextField(
                value = title,
                label = AppText.title,
                leadingIcon = GmIcons.TitleDefault,
                onValueChange = onValueChange,
            )
            Image(
                bitmap = imageResult.data.asImageBitmap(),
                contentDescription = stringResource(id = R.string.selected_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ScreenshotGallery(
    onEvent: (ScreenshotUiEvent) -> Unit,
    screenState: ScreenState,
    modifier: Modifier = Modifier,
) {
    val size = 300.dp
    val localDensity = LocalDensity.current
    val squareSizePx = with(localDensity) { size.toPx() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val localConfiguration = LocalConfiguration.current
    val imagePath =
        context.getExternalFilesDir(null)?.absolutePath
            ?.let { "$it/screenshots/screenshot.png" }
    val screenSize = with(localDensity) {
        localConfiguration.screenWidthDp.dp.toPx() to localConfiguration.screenHeightDp.dp.toPx()
    }
    val initialOffset =
        Offset(
            x = (screenSize.first - squareSizePx) / 2,
            y = (screenSize.second - squareSizePx * 3 / 2) / 2,
        )
    val offset = remember { Animatable(initialOffset, Offset.VectorConverter) }

    Box(
        modifier =
        modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    scope.launch {
                        val parentSize = this@pointerInput.size
                        val newX =
                            (offset.value.x + dragAmount.x)
                                .coerceIn(0f, parentSize.width - squareSizePx)
                        val newY =
                            (offset.value.y + dragAmount.y)
                                .coerceIn(0f, parentSize.height - squareSizePx)
                        offset.snapTo(Offset(newX, newY))
                    }
                }
            },
    ) {
        AsyncImage(
            model =
            ImageRequest.Builder(context)
                .data(imagePath)
                .build(),
            contentDescription = stringResource(id = R.string.selected_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth(),
        )
        Canvas(
            modifier =
            Modifier
                .matchParentSize(),
        ) {
            drawIntoCanvas {
                val transparentSquare =
                    Path().apply {
                        addRect(
                            Rect(
                                offset = Offset(offset.value.x, offset.value.y),
                                size = Size(squareSizePx, squareSizePx),
                            ),
                        )
                    }
                clipPath(transparentSquare, clipOp = ClipOp.Difference) {
                    drawRect(SolidColor(Color(0x80000000)))
                }
            }
        }
        ScreenshotBox(
            onEvent = onEvent,
            screenState = screenState,
            modifier =
            Modifier
                .offset {
                    IntOffset(
                        offset.value.x.roundToInt(),
                        offset.value.y.roundToInt(),
                    )
                }
                .size(size),
        )
    }
}

@Composable
private fun ScreenshotBox(
    onEvent: (ScreenshotUiEvent) -> Unit,
    screenState: ScreenState,
    modifier: Modifier = Modifier,
) {
    val view: View = LocalView.current
    var composableBounds by remember { mutableStateOf<Rect?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(screenState) {
        onEvent(
            ScreenshotUiEvent.OnCallbackChanged {
                composableBounds?.let { bounds ->
                    if (bounds.width == 0f || bounds.height == 0f) return@let
                    scope.launch {
                        val imageResult =
                            withContext(Dispatchers.IO) {
                                view.screenshot(bounds)
                            }
                        onEvent(ScreenshotUiEvent.OnImageResultChanged(imageResult))
                        if (imageResult is ImageResult.Success) {
                            onEvent(ScreenshotUiEvent.OnBitmapChanged(imageResult.data))
                        }
                    }
                }
            },
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(ScreenshotUiEvent.OnBitmapChanged(null))
            onEvent(ScreenshotUiEvent.OnCallbackChanged(null))
        }
    }

    Box(
        modifier =
        modifier
            .onGloballyPositioned {
                composableBounds = it.boundsInWindow()
            }
            .screenshotPreviewOverlay(),
    )
}

fun Modifier.screenshotPreviewOverlay(
    borderWidth: Dp = 3.dp,
    borderColor: Color = Color.White,
) = this.then(
    Modifier.drawWithContent {
        drawContent()

        val strokeWidthPx = borderWidth.toPx()
        val cornerOffset = 2.dp.toPx()

        val thirdWidth = (size.width + strokeWidthPx) / 3
        val thirdHeight = (size.height + strokeWidthPx) / 3
        val twoThirdWidth = (size.width + strokeWidthPx) / 3 * 2
        val twoThirdHeight = (size.height + strokeWidthPx) / 3 * 2

        val path =
            Path().apply {
                // Top-left
                moveTo(thirdWidth, -cornerOffset)
                lineTo(-cornerOffset, -cornerOffset)
                lineTo(-cornerOffset, thirdHeight)

                // Bottom-left
                moveTo(-cornerOffset, twoThirdHeight)
                lineTo(-cornerOffset, size.height + cornerOffset)
                lineTo(thirdWidth, size.height + cornerOffset)

                // Top-right
                moveTo(twoThirdWidth, -cornerOffset)
                lineTo(size.width + cornerOffset, -cornerOffset)
                lineTo(size.width + cornerOffset, thirdHeight)

                // Bottom-right
                moveTo(size.width + cornerOffset, twoThirdHeight)
                lineTo(size.width + cornerOffset, size.height + cornerOffset)
                lineTo(twoThirdWidth, size.height + cornerOffset)
            }

        drawPath(
            path = path,
            color = borderColor,
            style =
            Stroke(
                width = strokeWidthPx,
            ),
        )
    },
)

@Preview(showBackground = true)
@Composable
private fun ScreenshotPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(200.dp)
                .screenshotPreviewOverlay(borderColor = Color.Red),
        )
        Box(
            Modifier
                .size(200.dp)
                .background(Color.Blue),
        )
    }
}
