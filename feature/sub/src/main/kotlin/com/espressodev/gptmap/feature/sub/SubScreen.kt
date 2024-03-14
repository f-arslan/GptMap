package com.espressodev.gptmap.feature.sub

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
internal fun SubRoute(viewModel: SubViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Box(modifier = Modifier.fillMaxSize()) {
        SubScreen(
            uiState,
            onMonthlyClick = viewModel::onMonthlyClick,
            onAnnualClick = viewModel::onAnnualClick,
            onSubClick = viewModel::onSubClick
        )
    }
}

@Composable
internal fun SubScreen(
    uiState: SubUiState,
    onMonthlyClick: () -> Unit,
    onAnnualClick: () -> Unit,
    onSubClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = AppText.sub_header),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val monthlyStyle = getSubCardStyle(
                isSelected = uiState.selectedCard == CardType.Monthly,
                type = CardType.Monthly,
                monthlyPrice = uiState.monthlyPrice,
                annualPrice = uiState.annualPrice
            )
            val annualStyle = getSubCardStyle(
                isSelected = uiState.selectedCard == CardType.Annual,
                type = CardType.Annual,
                monthlyPrice = uiState.monthlyPrice,
                annualPrice = uiState.annualPrice
            )
            monthlyStyle.SubCard(
                onClick = onMonthlyClick,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(16 / 13f)
            )
            annualStyle.SubCard(
                onClick = onAnnualClick,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(16 / 13f)
            )
        }
        SubFeatures()
        Spacer(modifier = Modifier.weight(1f))
        SubButton(onSubClick)
    }
}

@Composable
fun SubButton(onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(id = AppText.sub_button))
    }
}

@Composable
fun SubFeatures() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FeatureText(textId = AppText.sub_body_1)
        FeatureText(textId = AppText.sub_body_2)
        FeatureText(textId = AppText.sub_body_3)
    }
}

@Composable
fun FeatureText(@StringRes textId: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = GmIcons.DoneDefault,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.padding(2.dp)
        )
        Text(
            text = stringResource(id = textId),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
internal fun SubCardStyle.SubCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .border(
                width = borderStroke,
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = text),
                    color = textColor,
                    style = MaterialTheme.typography.titleMedium
                )
                if (isSelected) {
                    Icon(
                        imageVector = GmIcons.CheckCircleFilled,
                        contentDescription = stringResource(id = AppText.selected_plan),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$$price",
                color = textColor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                letterSpacing = (0.8).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (type == CardType.Annual) {
                DiscountLabel(percentage = percentage, isSelected = isSelected)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = stringResource(id = subText), color = textColor)
        }
    }
}

@Composable
internal fun DiscountLabel(percentage: Int, isSelected: Boolean, modifier: Modifier = Modifier) {
    val background =
        if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surface
    val textColor =
        if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .padding(vertical = 2.dp, horizontal = 8.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SAVE $percentage%",
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun getSubCardStyle(
    isSelected: Boolean,
    type: CardType,
    monthlyPrice: Double,
    annualPrice: Double
): SubCardStyle {
    val background =
        if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val borderStroke = if (isSelected) 0.dp else 1.dp
    val textColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.surface
    val text = if (type == CardType.Monthly) AppText.monthly else AppText.annual
    val subText = if (type == CardType.Monthly) AppText.billed_monthly else AppText.billed_annually
    val price = if (type == CardType.Monthly) monthlyPrice else annualPrice
    val percentage = calculateDiscountPercentage(monthlyPrice, annualPrice).getOrElse { 0 }

    return SubCardStyle(
        background = background,
        borderStroke = borderStroke,
        textColor = textColor,
        text = text,
        subText = subText,
        type = type,
        price = price,
        percentage = percentage,
        isSelected = isSelected
    )
}

data class SubCardStyle(
    val background: Color,
    val borderStroke: Dp,
    val textColor: Color,
    val text: Int,
    val subText: Int,
    val type: CardType,
    val price: Double,
    val percentage: Int,
    val isSelected: Boolean
)

fun calculateDiscountPercentage(monthlyPrice: Double, annualPrice: Double) = runCatching {
    val totalMonthlyCost = monthlyPrice * 12
    val savings = totalMonthlyCost - annualPrice
    ((savings / totalMonthlyCost) * 100).toInt()
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    device = "id:pixel"
)
@Composable
fun SubPreview() {
    GptmapTheme {
        SubScreen(
            uiState = SubUiState(
                selectedCard = CardType.Annual,
                monthlyPrice = 4.99,
                annualPrice = 34.99
            ), onMonthlyClick = {}, onAnnualClick = {}, {}
        )
    }
}
