package com.espressodev.gptmap.feature.screenshot_gallery

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val ScreenshotGalleryRoute = "screenshot_gallery_route"

fun NavController.navigateToScreenshotGallery(navOptions: NavOptions? = null) {
    navigate(ScreenshotGalleryRoute, navOptions = navOptions)
}

fun NavGraphBuilder.screenshotGalleryScreen(
    popUp: () -> Unit,
) {
    composable(ScreenshotGalleryRoute) {
        ScreenshotGalleryRoute(popUp)
    }
}
