package com.espressodev.gptmap.feature.image_analyses

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val imageAnalysesRoute = "image_analyses_route"

fun NavController.navigateToListImageAnalyses() {
    navigate(imageAnalysesRoute)
}

fun NavGraphBuilder.imageAnalysesScreen(
    popUp: () -> Unit,
    navigateToImageAnalysis: (String) -> Unit
) {
    composable(imageAnalysesRoute) {
        ImageAnalysesRoute(popUp, navigateToImageAnalysis)
    }
}