package com.espressodev.gptmap.feature.map

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.espressodev.gptmap.core.common.ext.clipPolygon
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MAX_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.SMALL_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.VERY_HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.VERY_SMALL_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.ClickableShimmerImage
import com.espressodev.gptmap.core.designsystem.component.SquareButton
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.unsplash.LocationImage
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
internal fun BoxScope.DetailSheet(
    location: Location,
    onEvent: (MapUiEvent) -> Unit,
    onStreetViewClick: () -> Unit,
) {
    BackHandler { onEvent(MapUiEvent.OnDetailSheetBackClick) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clipPolygon(MaterialTheme.colorScheme.surface)
            .align(Alignment.BottomCenter)

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = HIGH_PADDING)
                .padding(bottom = VERY_HIGH_PADDING)
        ) {
            Text(
                text = location.content.city,
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = location.content.toDistrictAndCountry().uppercase(),
                modifier = Modifier.offset(y = SMALL_PADDING * -1),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(MEDIUM_PADDING))
            DetailButtons(
                addToFavouriteButtonState = location.addToFavouriteButtonState,
                onStreetViewClick = onStreetViewClick,
                onFavouriteClick = { onEvent(MapUiEvent.OnFavouriteClick) }
            )
            Text(
                text = location.content.toPoeticDescWithDecor(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                lineHeight = MAX_PADDING.value.sp
            )
            Spacer(modifier = Modifier.height(VERY_HIGH_PADDING))
            LocationImages(
                location.locationImages,
                onClick = { onEvent(MapUiEvent.OnImageClick(it)) }
            )
            Text(
                text = location.content.normalDescription,
                lineHeight = VERY_HIGH_PADDING.value.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun LocationImages(
    images: List<LocationImage>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(MEDIUM_PADDING),
        modifier = modifier.padding(bottom = MEDIUM_PADDING)
    ) {
        items(2) { index ->
            ClickableShimmerImage(
                images[index].imageUrl, modifier = Modifier
                    .size(160.dp, 100.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) { onClick(index) }
        }
    }
}

@Composable
fun BoxScope.UnsplashBanner(name: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
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
private fun DetailButtons(
    addToFavouriteButtonState: Boolean,
    onStreetViewClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(HIGH_PADDING),
        modifier = modifier.padding(bottom = HIGH_PADDING)
    ) {
        SquareButton(
            iconId = AppDrawable.street_view,
            contentDesc = AppText.street_view,
            onClick = onStreetViewClick
        )
        AnimatedVisibility(
            addToFavouriteButtonState,
            exit = slideOutVertically(targetOffsetY = { fullHeight: Int -> -fullHeight })
        ) {
            SquareButton(
                icon = GmIcons.FavouriteOutlined,
                contentDesc = AppText.add_favourite,
                onClick = onFavouriteClick
            )
        }
    }
}
