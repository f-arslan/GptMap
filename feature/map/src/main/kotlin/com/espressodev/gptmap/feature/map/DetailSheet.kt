package com.espressodev.gptmap.feature.map

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.espressodev.gptmap.core.common.ext.clipPolygon
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MAX_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.SMALL_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.VERY_HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.SquareButton
import com.espressodev.gptmap.core.model.LocationImage
import com.espressodev.gptmap.core.model.chatgpt.Content
import com.espressodev.gptmap.feature.map.R.string as AppText
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable

@Composable
internal fun DetailSheet(
    content: Content,
    images: List<LocationImage>,
    onDismiss: () -> Unit,
    onStreetViewClick: () -> Unit,
    onFavouriteClick: () -> Unit
) {
    BackHandler {
        onDismiss()
    }
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
                modifier = Modifier.offset(y = SMALL_PADDING.times(-1)),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(MEDIUM_PADDING))
            DetailButtons(onStreetViewClick, onFavouriteClick)
            Text(
                text = content.toPoeticDescWithDecor(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                lineHeight = MAX_PADDING.value.sp
            )
            Spacer(modifier = Modifier.height(VERY_HIGH_PADDING))
            LocationImages(images)
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
fun LocationImages(images: List<LocationImage>) {
    LazyRow {
        items(images) {
            ImageCard()
        }
    }
}

val colors =
    listOf(
        Color(0xFFFF595A),
        Color(0xFFFFC766),
        Color(0xFF35A07F),
        Color(0xFF35A07F),
        Color(0xFFFFC766),
        Color(0xFFFF595A)
    )
val brush = Brush.linearGradient(colors)

@Composable
fun ImageCard() {
    Image(
        painter = painterResource(id = AppDrawable.istanbul),
        contentDescription = null,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .aspectRatio(16 / 10f)
            .border(
                2.dp,
                brush = brush,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(1.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun DetailButtons(onStreetViewClick: () -> Unit, onFavouriteClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(HIGH_PADDING),
        modifier = Modifier.padding(bottom = HIGH_PADDING)
    ) {
        SquareButton(
            icon = GmIcons.StreetViewDefault,
            AppText.street_view,
            onClick = onStreetViewClick
        )
        SquareButton(
            icon = GmIcons.FavouriteOutlined,
            AppText.add_favourite,
            onClick = onFavouriteClick
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailSheetPreview() {
    ImageCard()
}








