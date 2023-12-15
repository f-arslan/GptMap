package com.espressodev.gptmap.feature.map

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.espressodev.gptmap.core.common.ext.clipPolygon
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MAX_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.SMALL_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.VERY_HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.VERY_SMALL_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.SquareButton
import com.espressodev.gptmap.core.model.LocationImage
import com.espressodev.gptmap.core.model.chatgpt.Content
import kotlin.math.roundToInt
import com.espressodev.gptmap.feature.map.R.string as AppText
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable

@Composable
internal fun DetailSheet(
    content: Content,
    images: List<LocationImage>,
    onEvent: (MapUiEvent) -> Unit,
    onStreetViewClick: () -> Unit,
) {
    BackHandler { onEvent(MapUiEvent.OnDismissBottomSheet) }
    Box(modifier = Modifier.clipPolygon(MaterialTheme.colorScheme.surface)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = HIGH_PADDING)
                .padding(bottom = VERY_HIGH_PADDING)
        ) {
            Text(
                text = content.city,
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = content.toDistrictAndCountry().uppercase(),
                modifier = Modifier.offset(y = SMALL_PADDING * -1),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(MEDIUM_PADDING))
            DetailButtons(
                onStreetViewClick = onStreetViewClick,
                onFavouriteClick = { onEvent(MapUiEvent.OnFavouriteClick) }
            )
            Text(
                text = content.toPoeticDescWithDecor(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                lineHeight = MAX_PADDING.value.sp
            )
            Spacer(modifier = Modifier.height(VERY_HIGH_PADDING))
            LocationImages(images, onClick = { onEvent(MapUiEvent.OnImageClick(it)) })
            Text(
                text = content.normalDescription,
                lineHeight = VERY_HIGH_PADDING.value.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 7,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun LocationImages(images: List<LocationImage>, onClick: (Int) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
        modifier = Modifier.padding(bottom = MEDIUM_PADDING)
    ) {
        items(2) { index ->
            ImageCard(images[index], modifier = Modifier.size(160.dp, 100.dp)) { onClick(index) }
        }
    }
}

@Composable
fun ImageCard(image: LocationImage, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val showShimmer = remember { mutableStateOf(value = true) }
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(MEDIUM_PADDING))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { if (!showShimmer.value) onClick() },
    ) {
        AsyncImage(
            model = image.imageUrl,
            modifier = Modifier
                .background(shimmerBrush(targetValue = 1300f, showShimmer = showShimmer.value))
                .then(modifier),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onSuccess = { showShimmer.value = false }
        )
    }
}

@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color(0xFFC2C2C2).copy(alpha = 0.8f),
            Color(0xFFC2C2C2).copy(alpha = 0.1f),
            Color(0xFFC2C2C2).copy(alpha = 0.8f),
        )

        val transition = rememberInfiniteTransition(label = "shimmer transition")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(1000), repeatMode = RepeatMode.Reverse
            ), label = "shimmer animation"
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

@Composable
fun BoxScope.UnsplashBanner(name: String) {
    Surface(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = MEDIUM_PADDING, end = MEDIUM_PADDING),
        shape = RoundedCornerShape(SMALL_PADDING),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    ) {
        Text(
            text = "$name on Unsplash",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 8.sp,
            modifier = Modifier.padding(VERY_SMALL_PADDING)
        )
    }
}

@Composable
private fun DetailButtons(onStreetViewClick: () -> Unit, onFavouriteClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(HIGH_PADDING),
        modifier = Modifier.padding(bottom = HIGH_PADDING)
    ) {
        SquareButton(
            iconId = AppDrawable.street_view,
            contentDesc = AppText.street_view,
            onClick = onStreetViewClick
        )
        SquareButton(
            icon = GmIcons.FavouriteOutlined,
            contentDesc = AppText.add_favourite,
            onClick = onFavouriteClick
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailSheetPreview() {
    var offsetY by remember { mutableFloatStateOf(value = 0f) }
    val composableHeight = 500.dp
    val maxOffsetY = with(LocalDensity.current) { composableHeight.toPx() * 0.85f } // 90% of the composable's height

    Log.d("DetailSheetPreview", "offsetY: $offsetY")
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier
                .offset { IntOffset(0, offsetY.roundToInt().coerceIn(0, maxOffsetY.roundToInt())) }
                .clipPolygon(Color.DarkGray)
                .height(composableHeight)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetY = (offsetY + dragAmount.y).coerceIn(0f, maxOffsetY)
                    }
                }
        )
    }
}

// TODO: ADD BUTTON WHEN THE USER FULLY MINIMIZES THE BOTTOM SHEET






