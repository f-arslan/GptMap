package com.espressodev.gptmap.feature.image_analyses

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
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
import com.espressodev.gptmap.core.model.ImageAnalysisSummary
import com.espressodev.gptmap.core.model.Response
import java.time.LocalDateTime
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun ImageAnalysesRoute(
    popUp: () -> Unit,
    navigateToImageAnalysis: (String) -> Unit,
    viewModel: ImageAnalysesViewModel = hiltViewModel()
) {
    val imageAnalysesResponse by viewModel.imageAnalyses.collectAsStateWithLifecycle()
    Scaffold(topBar = {
        GmTopAppBar(title = AppText.image_analyses, icon = GmIcons.ImageSearchDefault, onBackClick = popUp)
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
                        imageAnalyses = data,
                        onImageClick = navigateToImageAnalysis
                    )
                }
            }
        }
    }
}

@Composable
fun ImageAnalysesScreen(
    imageAnalyses: List<ImageAnalysisSummary>,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        items(imageAnalyses, key = { key -> key.id }) { imageAnalysisSummary ->
            ImageAnalysisCard(
                imageAnalysisSummary = imageAnalysisSummary,
                onClick = { onImageClick(imageAnalysisSummary.id) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageAnalysisCard(
    imageAnalysisSummary: ImageAnalysisSummary,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shadowElevation = 4.dp
    ) {
        Column {
            ClickableShimmerImage(
                imageAnalysisSummary.imageUrl,
                modifier = Modifier.aspectRatio(1f),
            )
            Text(
                text = imageAnalysisSummary.title,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .basicMarquee(),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageAnalysesPreview() {
    GptmapTheme {
        ImageAnalysisCard(
            imageAnalysisSummary = ImageAnalysisSummary(
                id = "epicuri",
                imageUrl = "https://duckduckgo.com/?q=comprehensam",
                title = "nunc",
                date = LocalDateTime.now()
            ),
            onClick = {}
        )
    }
}