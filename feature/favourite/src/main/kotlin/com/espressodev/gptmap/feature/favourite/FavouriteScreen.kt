package com.espressodev.gptmap.feature.favourite

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.SMALL_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.AppWrapper
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable

@Composable
fun FavouriteRoute() {

}


@Composable
fun FavouriteScreen() {
    Scaffold {

    }
}


@Composable
fun FavouriteCard() {
    ElevatedCard {
        Column(modifier = Modifier.width(320.dp)) {
            Image(
                painter = painterResource(id = AppDrawable.istanbul),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MEDIUM_PADDING),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(3f)
                        .padding(end = MEDIUM_PADDING),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING)
                ) {
                    Icon(
                        imageVector = GmIcons.LocationCityOutlined,
                        contentDescription = null,
                        modifier = Modifier.size(HIGH_PADDING)
                    )
                    Text(
                        text = "Istanbul, Turkey sdadasdsadasasddasasd",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    modifier = Modifier.weight(2f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = GmIcons.MyLocationOutlined,
                        contentDescription = null,
                        modifier = Modifier.size(MEDIUM_HIGH_PADDING),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "48.142°, 42.163°",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FavouritePreview() {
    GptmapTheme {
        FavouriteCard()
    }
}
