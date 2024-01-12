package com.espressodev.gptmap.feature.image_analysis

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val IMAGE_ANALYSIS_ROUTE = "image_analysis_route"
const val IMAGE_ID = "imageId"
fun NavController.navigateToImageAnalysis(
    imageId: String = "default",
    navOptionsBuilder: NavOptionsBuilder.() -> Unit
) {
    println(navOptionsBuilder)
    navigate("$IMAGE_ANALYSIS_ROUTE/$imageId", navOptionsBuilder)
}

fun NavGraphBuilder.imageAnalysisScreen(popUp: () -> Unit) {
    composable(
        route = "$IMAGE_ANALYSIS_ROUTE/{$IMAGE_ID}",
        arguments = listOf(navArgument(IMAGE_ID) { type = NavType.StringType })
    ) {
        val imageId = it.arguments?.getString(IMAGE_ID) ?: "default"
        ImageAnalysisRoute(imageId = imageId, popUp = popUp)
    }
}