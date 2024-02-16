package com.espressodev.gptmap.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.R
import com.espressodev.gptmap.core.designsystem.ext.gradientBackground
import kotlin.math.roundToInt

@Composable
fun ShimmerImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    shadowElevation: Dp = 0.dp,
    onSuccess: () -> Unit = {},
) {
    val showShimmer = remember { mutableStateOf(value = true) }
    Surface(shadowElevation = shadowElevation, modifier = modifier) {
        AsyncImage(
            model = imageUrl,
            modifier = Modifier
                .fillMaxSize()
                .background(shimmerBrush(showShimmer = showShimmer.value, targetValue = 1300f)),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onSuccess = {
                showShimmer.value = false
                onSuccess()
            },
        )
    }
}

@Composable
fun DraggableImage(
    imageUrl: String,
    onPinClick: () -> Unit,
    isScreenshot: Boolean,
    modifier: Modifier = Modifier
) {
    var parentSize by remember { mutableStateOf(IntSize.Zero) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }

    val imageSize = with(LocalDensity.current) { 175.dp.roundToPx() }
    val biggerImageWidth = if (isScreenshot) 275.dp else 350.dp
    val smallWidth = if (isScreenshot) 175.dp else 250.dp

    var isFullScreen by remember { mutableStateOf(value = false) }
    if (isFullScreen) {
        GalleryView(
            imageUrl = imageUrl,
            biggerImageWidth = biggerImageWidth,
            onDismiss = { isFullScreen = false }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .systemBarsPadding()
            .padding(bottom = 72.dp)
            .onSizeChanged { size -> parentSize = size }
    ) {
        Box(
            modifier = Modifier
                .offset {
                    val maxX = parentSize.width - imageSize.toFloat()
                    val maxY = parentSize.height - imageSize.toFloat()

                    val constrainedX = offset.x.coerceIn(0f, maxX)
                    val constrainedY = offset.y.coerceIn(0f, maxY)

                    IntOffset(constrainedX.roundToInt(), constrainedY.roundToInt())
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        val proposedOffset = offset + dragAmount
                        val maxX = parentSize.width - imageSize.toFloat()
                        val maxY = parentSize.height - imageSize.toFloat()

                        offset = Offset(
                            x = proposedOffset.x.coerceIn(0f, maxX),
                            y = proposedOffset.y.coerceIn(0f, maxY)
                        )
                        change.consume()
                    }
                }
                .clickable { isFullScreen = true }
        ) {
            NaiveImage(imageUrl, width = smallWidth)
            PinButton(
                onClick = onPinClick,
                icon = GmIcons.PushPinOutlined,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
fun NaiveImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    width: Dp = 175.dp,
    height: Dp = 175.dp
) {
    Surface(
        modifier = modifier
            .width(width)
            .height(height)
            .shadow(2.dp, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(id = R.string.selected_image),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun DefaultImage(
    imageUrl: String,
    onPinClick: () -> Unit,
    onFullScreenClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 175.dp,
    width: Dp = 175.dp
) {
    val isDarkTheme = isSystemInDarkTheme()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp))
            .gradientBackground(isDarkTheme)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.statusBarsPadding()) {
                Surface(
                    modifier = Modifier
                        .height(height)
                        .width(width)
                        .shadow(2.dp, RoundedCornerShape(8.dp))
                        .clickable(onClick = onFullScreenClick),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = stringResource(id = R.string.selected_image),
                        contentScale = ContentScale.Crop
                    )
                }
                PinButton(
                    onClick = onPinClick,
                    icon = GmIcons.PushPinOutlined,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
fun GalleryView(
    imageUrl: String,
    biggerImageWidth: Dp,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            NaiveImage(imageUrl = imageUrl, width = biggerImageWidth, height = 275.dp)
        }
    }
}

@Composable
fun BotImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ai_icon),
        contentDescription = null,
        modifier = modifier.size(20.dp).offset(y = 4.dp)
    )
}
