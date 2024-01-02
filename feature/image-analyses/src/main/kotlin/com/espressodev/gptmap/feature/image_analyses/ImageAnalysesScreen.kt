package com.espressodev.gptmap.feature.image_analyses

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.designsystem.R.string as AppText
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable

@Composable
fun ImageAnalysesRoute(popUp: () -> Unit, viewModel: ImageAnalysesViewModel = hiltViewModel()) {
    val imageAnalyses by viewModel.imageAnalyses.collectAsStateWithLifecycle()
    Scaffold(topBar = {
        GmTopAppBar(title = AppText.image_analyses, icon = GmIcons.ImageSearch, onBackClick = popUp)
    }) {
        ImageAnalysesScreen(modifier = Modifier.padding(it), imageAnalyses = imageAnalyses)
    }
}


@Composable
fun ImageAnalysesScreen(modifier: Modifier = Modifier, imageAnalyses: List<ImageAnalysis>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(imageAnalyses, key = { key -> key.id }) { imageAnalysis ->
            ImageAnalysisCard(imageAnalysis = imageAnalysis)
        }
    }
}


@Composable
fun ImageAnalysisCard(imageAnalysis: ImageAnalysis) {
    Card {
        Image(
            painter = painterResource(id = AppDrawable.istanbul),
            contentDescription = null,
            modifier = Modifier.aspectRatio(1f),
            contentScale = ContentScale.Crop
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ImageAnalysesPreview() {
    GptmapTheme {


    }
}