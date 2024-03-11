package com.espressodev.gptmap.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun ExtFloActionButton(
    @DrawableRes icon: Int,
    @StringRes label: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Image(
            painter = painterResource(icon),
            contentDescription = null
        )
        Spacer(Modifier.width(8.dp))
        Text(stringResource(label), style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun ExploreWithAiButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = AppDrawable.ai_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .offset(x = (-8).dp),
            )
            Text(
                text = stringResource(id = AppText.explore_with_ai),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun DefaultButton(
    @StringRes text: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Button(
        modifier = modifier.defaultMinSize(56.dp),
        onClick = {
            keyboardController?.hide()
            onClick()
        },
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun GmTonalIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Surface(
        modifier = modifier
            .size(40.dp),
        shape = CircleShape,
        border = BorderStroke(1.dp, color)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(6.dp)
        )
    }
}

@Composable
fun GmDraggableButton(
    icon: IconType,
    modifier: Modifier = Modifier,
    initialAlignment: Alignment = Alignment.CenterEnd,
    onClick: () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
) {
    val localDensity = LocalDensity.current
    val screenWidth = with(localDensity) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val screenHeight = with(localDensity) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val buttonSizePx = with(localDensity) { 56.dp.toPx() } // FAB size
    val marginPx = with(localDensity) { 8.dp.toPx() }
    val scope = rememberCoroutineScope()

    val (initialXOffsetPx, initialYOffsetPx) = when (initialAlignment) {
        Alignment.CenterStart -> Pair(marginPx, (screenHeight - buttonSizePx) / 2)
        Alignment.CenterEnd -> Pair(
            screenWidth - buttonSizePx - marginPx,
            (screenHeight - buttonSizePx) / 2
        )

        else -> Pair(
            screenWidth - buttonSizePx - marginPx,
            (screenHeight - buttonSizePx) / 2
        )
    }

    val bottomMarginPx = with(localDensity) { 160.dp.toPx() }
    val offset = remember(localDensity) {
        Animatable(
            initialValue = Offset(
                x = initialXOffsetPx.coerceIn(0f, screenWidth - buttonSizePx),
                y = initialYOffsetPx.coerceIn(0f, screenHeight - buttonSizePx - bottomMarginPx)
            ),
            typeConverter = Offset.VectorConverter
        )
    }

    FloatingActionButton(
        onClick = onClick,
        containerColor = containerColor,
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
        modifier = modifier
            .zIndex(1f)
            .offset { IntOffset(offset.value.x.roundToInt(), offset.value.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        val leftDistance = offset.value.x
                        val rightDistance = screenWidth - offset.value.x - buttonSizePx

                        val targetX = if (leftDistance < rightDistance) {
                            marginPx
                        } else {
                            screenWidth - buttonSizePx - marginPx
                        }

                        scope.launch {
                            offset.animateTo(
                                targetValue = Offset(targetX, offset.value.y),
                                animationSpec = spring()
                            )
                        }
                    }
                ) { change, dragAmount ->
                    change.consume()
                    val newX = (offset.value.x + dragAmount.x).coerceIn(
                        marginPx,
                        screenWidth - buttonSizePx - marginPx
                    )
                    val newY = (offset.value.y + dragAmount.y).coerceIn(
                        marginPx,
                        screenHeight - buttonSizePx - bottomMarginPx
                    )
                    scope.launch {
                        offset.snapTo(Offset(newX, newY))
                    }
                }
            }
    ) {
        when (icon) {
            is IconType.Bitmap -> {
                GradientOverImage(painterId = icon.painterId)
            }

            is IconType.Vector -> {
                Icon(
                    imageVector = icon.imageVector,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
fun GradientOverImage(@DrawableRes painterId: Int, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite Transition animation")
    val bgColor by infiniteTransition.animateColor(
        initialValue = Color(0xFF9C27B0),
        targetValue = Color(0xFFFF9800),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bgColor animation"
    )
    Icon(
        painter = painterResource(id = painterId),
        contentDescription = null,
        modifier = modifier.size(48.dp),
        tint = bgColor
    )
}

@Composable
fun SquareButton(
    @StringRes contentDesc: Int,
    onClick: () -> Unit,
    icon: IconType,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    contentPaddings: PaddingValues = PaddingValues(0.dp),
    size: Dp = 56.dp,
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(size),
        shape = shape,
        contentPadding = contentPaddings,
    ) {
        when (icon) {
            is IconType.Bitmap -> {
                Icon(
                    painter = painterResource(id = icon.painterId),
                    contentDescription = stringResource(id = contentDesc),
                    modifier = Modifier.size(32.dp)
                )
            }

            is IconType.Vector -> {
                Icon(
                    imageVector = icon.imageVector,
                    contentDescription = stringResource(id = contentDesc),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun PinButton(onClick: () -> Unit, icon: ImageVector, modifier: Modifier = Modifier) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = AppText.pin)
        )
    }
}

@Composable
fun BackButton(onClick: () -> Unit) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = Modifier
            .zIndex(1f)
            .statusBarsPadding()
            .padding(8.dp)
    ) {
        Icon(imageVector = GmIcons.ArrowBackOutlined, contentDescription = null)
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonPreview() {
    GptmapTheme {
    }
}
