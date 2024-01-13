package com.espressodev.gptmap.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ShimmerImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    shadowElevation: Dp = 0.dp,
) {
    val showShimmer = remember { mutableStateOf(value = true) }
    Surface(shadowElevation = shadowElevation, modifier = modifier) {
        AsyncImage(
            model = imageUrl,
            modifier = Modifier
                .fillMaxSize()
                .background(shimmerBrush(showShimmer = showShimmer.value, targetValue = 1300f)),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onSuccess = { showShimmer.value = false },
        )
    }
}
