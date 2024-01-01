package com.espressodev.gptmap.feature.image_analysis

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable

const val imageAnalysisRoute = "image_analysis_route"

fun NavController.navigateToImageAnalysis(navOptionsBuilder: NavOptionsBuilder.() -> Unit) {
    navigate(imageAnalysisRoute, navOptionsBuilder)
}

fun NavGraphBuilder.imageAnalysisScreen(popUp: () -> Unit) {
    composable(imageAnalysisRoute) {
        ImageAnalysisRoute(popUp = popUp)
    }
}