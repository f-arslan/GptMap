package com.espressodev.gptmap.feature.screenshot_gallery

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.TextType
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.core.designsystem.component.LoadingAnimation
import com.espressodev.gptmap.core.designsystem.component.ShimmerImage
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.model.ImageSummary
import com.espressodev.gptmap.core.model.Response
import java.time.LocalDateTime
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenshotGalleryRoute(
    popUp: () -> Unit,
    viewModel: ScreenshotGalleryViewModel = hiltViewModel()
) {
    val imageAnalysesResponse by viewModel.imageAnalyses.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            GmTopAppBar(
                textType = TextType.Res(AppText.image_analyses),
                icon = IconType.Vector(GmIcons.ImageSearchDefault),
                onBackClick = popUp
            )
        }
    ) {
        with(imageAnalysesResponse) {
            when (this) {
                is Response.Failure -> {
                    LoadingAnimation(animId = AppRaw.confused_man_404)
                }

                Response.Loading -> {}
                is Response.Success -> {
                    ScreenshotGalleryScreen(
                        modifier = Modifier.padding(it),
                        images = data,
                        onImageClick = { imageId ->

                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenshotGalleryScreen(
    images: List<ImageSummary>,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        items(images, key = { it.id }) { imageAnalysisSummary ->
            ImageCard(
                imageSummary = imageAnalysisSummary,
                onClick = { onImageClick(imageAnalysisSummary.id) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ImageCard(
    imageSummary: ImageSummary,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Log.d("ImageAnalysisCard", "imageAnalysisSummary: $imageSummary")

    ElevatedCard(onClick = onClick, modifier = modifier) {
        Column {
            ShimmerImage(
                imageSummary.imageUrl,
                modifier = Modifier.aspectRatio(1f),
            )
            Text(
                text = imageSummary.title,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .basicMarquee(iterations = Int.MAX_VALUE),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenshotGalleryPreview() {
    GptmapTheme {
        ImageCard(
            imageSummary = ImageSummary(
                id = "epicuri",
                imageUrl = "https://duckduckgo.com/?q=comprehensam",
                title = "nunc",
                date = LocalDateTime.now()
            ),
            onClick = {}
        )
    }
}