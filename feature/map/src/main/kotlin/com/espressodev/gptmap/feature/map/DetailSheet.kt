package com.espressodev.gptmap.feature.map

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.component.ShimmerImage
import com.espressodev.gptmap.core.designsystem.component.SquareButton
import com.espressodev.gptmap.core.designsystem.ext.clipPolygon
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.unsplash.LocationImage
import com.espressodev.gptmap.core.designsystem.R.string as AppText

context (BoxScope)
@Composable
internal fun Location.DetailSheet(
    onEvent: (MapUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onEvent(MapUiEvent.OnDetailSheetBackClick) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clipPolygon(MaterialTheme.colorScheme.surface)
            .align(Alignment.BottomCenter)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = content.city,
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = content.toDistrictAndCountry().uppercase(),
                modifier = Modifier.offset(y = 4.dp * -1),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            DetailButton(
                addToFavouriteButtonState = isAddedToFavourite,
                onFavouriteClick = { onEvent(MapUiEvent.OnFavouriteClick) }
            )
            Text(
                text = content.toPoeticDescWithDecor(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 32.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            LocationImages(
                locationImages,
                onClick = { onEvent(MapUiEvent.OnImageClick(it)) }
            )
            Text(
                text = content.normalDescription,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LocationImages(
    images: List<LocationImage>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(bottom = 8.dp)
    ) {
        items(2) { index ->
            Surface(onClick = { onClick(index) }) {
                ShimmerImage(
                    images[index].imageUrl,
                    modifier = Modifier
                        .size(160.dp, 100.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }
    }
}

@Composable
internal fun BoxScope.UnsplashBanner(name: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 8.dp, end = 8.dp),
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    ) {
        Text(
            text = "$name on Unsplash",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 8.sp,
            modifier = Modifier.padding(2.dp)
        )
    }
}

@Composable
private fun DetailButton(
    addToFavouriteButtonState: Boolean,
    onFavouriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        addToFavouriteButtonState,
        exit = slideOutVertically(targetOffsetY = { fullHeight: Int -> -fullHeight }),
        modifier = modifier.padding(bottom = 16.dp)
    ) {
        SquareButton(
            icon = IconType.Vector(GmIcons.FavouriteOutlined),
            contentDesc = AppText.add_favourite,
            onClick = onFavouriteClick
        )
    }
}
