package com.espressodev.gptmap.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.espressodev.gptmap.core.designsystem.Constants
import com.espressodev.gptmap.core.designsystem.Constants.BIG_BUTTON_SIZE
import com.espressodev.gptmap.core.designsystem.Constants.BUTTON_SIZE
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.NO_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MapSearchButton(
    onClick: () -> Unit,
    icon: ImageVector = GmIcons.SearchDefault,
    shape: Shape = RoundedCornerShape(HIGH_PADDING),
    buttonEnabledState: Boolean,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Button(
        shape = shape,
        enabled = buttonEnabledState,
        onClick = {
            keyboardController?.hide()
            onClick()
        },
        modifier = Modifier.size(BUTTON_SIZE),
        contentPadding = PaddingValues(NO_PADDING)
    ) {
        Icon(icon, stringResource(id = AppText.search))
    }
}

@Composable
fun ExtFloActionButton(
    @DrawableRes icon: Int,
    @StringRes label: Int,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(onClick = onClick) {
        Image(
            painter = painterResource(icon),
            contentDescription = null
        )
        Spacer(Modifier.width(MEDIUM_PADDING))
        Text(stringResource(label), style = MaterialTheme.typography.titleMedium)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DefaultButton(@StringRes text: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Button(
        modifier = modifier.defaultMinSize(BUTTON_SIZE),
        onClick = {
            keyboardController?.hide()
            onClick()
        },
        shape = RoundedCornerShape(HIGH_PADDING),
    ) {
        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}


@Composable
fun GmDraggableButton(icon: ImageVector, onClick: () -> Unit) {
    val localDensity = LocalDensity.current
    val screenWidth = with(localDensity) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val screenHeight = with(localDensity) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val buttonSizePx = with(localDensity) { 56.dp.toPx() } // Assuming 56.dp is the FAB size
    val marginPx = with(localDensity) { 8.dp.toPx() }
    val scope = rememberCoroutineScope()

    val initialXOffsetPx = screenWidth - buttonSizePx - marginPx
    val initialYOffsetPx = (screenHeight - buttonSizePx) / 2
    val bottomMarginPx = with(localDensity) { 160.dp.toPx() }


    val offset = remember(localDensity) {
        Animatable(
            Offset(
                x = initialXOffsetPx.coerceIn(0f, screenWidth - buttonSizePx),
                y = initialYOffsetPx.coerceIn(0f, screenHeight - buttonSizePx - bottomMarginPx)
            ),
            Offset.VectorConverter
        )
    }

    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier
            .zIndex(4f)
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
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(48.dp))
    }

}

@Composable
fun SquareButton(
    @StringRes contentDesc: Int,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    @DrawableRes iconId: Int? = null,
    shape: RoundedCornerShape = RoundedCornerShape(HIGH_PADDING),
    contentPaddings: PaddingValues = PaddingValues(NO_PADDING),
    size: Dp = BIG_BUTTON_SIZE
) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .size(size),
        shape = shape,
        contentPadding = contentPaddings,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        if (icon != null)
            Icon(
                imageVector = icon,
                contentDescription = stringResource(id = contentDesc),
                modifier = Modifier.size(Constants.MAX_PADDING)
            )
        else if (iconId != null)
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = stringResource(id = contentDesc),
                modifier = Modifier.size(Constants.MAX_PADDING)
            )
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtonPreview() {
    ExtFloActionButton(icon = AppDrawable.google, label = AppText.continue_google) {

    }
}