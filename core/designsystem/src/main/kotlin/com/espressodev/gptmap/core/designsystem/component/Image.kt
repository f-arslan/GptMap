package com.espressodev.gptmap.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.espressodev.gptmap.core.designsystem.Constants

@Composable
fun ClickableShimmerImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    shadowElevation: Dp = 0.dp,
    onClick: () -> Unit = {}
) {
    val showShimmer = remember { mutableStateOf(value = true) }
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(Constants.HIGH_PADDING))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { if (!showShimmer.value) onClick() },
        shadowElevation = shadowElevation
    ) {
        AsyncImage(
            model = imageUrl,
            modifier = Modifier
                .fillMaxSize()
                .background(shimmerBrush(targetValue = 1300f, showShimmer = showShimmer.value)),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onSuccess = { showShimmer.value = false }
        )
    }
}