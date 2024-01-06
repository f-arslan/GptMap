package com.espressodev.gptmap.feature.image_analyses

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.ClickableShimmerImage
import com.espressodev.gptmap.core.designsystem.component.GmCircularIndicator
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.core.designsystem.component.LoadingAnimation
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun ImageAnalysesRoute(popUp: () -> Unit, viewModel: ImageAnalysesViewModel = hiltViewModel()) {
    val imageAnalysesResponse by viewModel.imageAnalyses.collectAsStateWithLifecycle()
    Log.d("ImageAnalysesRoute", "imageAnalysesResponse: $imageAnalysesResponse")
    Scaffold(topBar = {
        GmTopAppBar(title = AppText.image_analyses, icon = GmIcons.ImageSearch, onBackClick = popUp)
    }) {
        with(imageAnalysesResponse) {
            when (this) {
                is Response.Failure -> {
                    LoadingAnimation(animId = AppRaw.confused_man_404)
                }

                Response.Loading -> GmCircularIndicator()
                is Response.Success -> {
                    ImageAnalysesScreen(
                        modifier = Modifier.padding(it),
                        imageAnalyses = data
                    )
                }
            }
        }
    }
}

@Composable
fun ImageAnalysesScreen(imageAnalyses: List<ImageAnalysis>, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        items(imageAnalyses, key = { key -> key.id }) { imageAnalysis ->
            ClickableShimmerImage(
                imageAnalysis.imageId,
                modifier = Modifier.aspectRatio(1f),
                shadowElevation = 4.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageAnalysesPreview() {
    GptmapTheme {


    }
}