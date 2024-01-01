package com.espressodev.gptmap.feature.image_analysis

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val imageAnalysisRoute = "image_analysis_route"

fun NavController.navigateToImageAnalysis(navOptions: NavOptions? = null) {
    navigate(imageAnalysisRoute, navOptions)
}

fun NavGraphBuilder.imageAnalysisScreen(popUp: () -> Unit) {
    composable(imageAnalysisRoute) {
        ImageAnalysisRoute(popUp = popUp)
    }
}