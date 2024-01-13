package com.espressodev.gptmap.feature.image_analyses

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SCREENSHOT_GALLERY_ROUTE = "screenshot_gallery_route"

fun NavController.navigateToScreenshotGallery() {
    navigate(SCREENSHOT_GALLERY_ROUTE)
}

fun NavGraphBuilder.screenshotGalleryScreen(
    popUp: () -> Unit,
) {
    composable(SCREENSHOT_GALLERY_ROUTE) {
        ScreenshotGalleryRoute(popUp)
    }
}
