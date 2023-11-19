package com.espressodev.gptmap.feature.map.detail_sheet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.espressodev.gptmap.core.common.ext.clipPolygon
import com.espressodev.gptmap.core.designsystem.Constants.HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MAX_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.SMALL_PADDING
import com.espressodev.gptmap.core.designsystem.Constants.VERY_HIGH_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.SquareButton
import com.espressodev.gptmap.core.model.chatgpt.Content
import com.espressodev.gptmap.feature.map.R.string as AppText


@Composable
internal fun DetailSheet(content: Content, onDismiss: () -> Unit) {
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
            DetailButtons()
            Text(
                text = content.toPoeticDescWithDecor(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                lineHeight = MAX_PADDING.value.sp
            )
            Spacer(modifier = Modifier.height(VERY_HIGH_PADDING))
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
private fun DetailButtons() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(HIGH_PADDING),
        modifier = Modifier.padding(bottom = HIGH_PADDING)
    ) {
        SquareButton(icon = GmIcons.StreetViewDefault, AppText.street_view, onClick = {})
        SquareButton(icon = GmIcons.FavouriteOutlined, AppText.add_favourite, onClick = {})
    }
}











