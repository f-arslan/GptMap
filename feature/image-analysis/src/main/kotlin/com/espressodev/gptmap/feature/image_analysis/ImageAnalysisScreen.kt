package com.espressodev.gptmap.feature.image_analysis

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw
import com.espressodev.gptmap.core.designsystem.component.GmCircularIndicator
import com.espressodev.gptmap.core.designsystem.component.LoadingAnimation
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.Response

@Composable
fun ImageAnalysisRoute(
    popUp: () -> Unit,
    imageId: String,
    viewModel: ImageAnalysisViewModel = hiltViewModel(),
) {
    val imageAnalysis by viewModel.imageAnalysis.collectAsStateWithLifecycle()
    Log.d("ImageAnalysisRoute", "imageAnalysis: $imageAnalysis")

    with(imageAnalysis) {
        when (this) {
            is Response.Failure -> LoadingAnimation(animId = AppRaw.confused_man_404)
            Response.Loading -> GmCircularIndicator()
            is Response.Success -> ImageAnalysisScreen(data)
        }
    }

    LaunchedEffect(key1 = imageId) {
        if (imageId != "default")
            viewModel.initializeImageAnalysis(imageId)
    }
}

@Composable
fun ImageAnalysisScreen(imageAnalysis: ImageAnalysis) {

}