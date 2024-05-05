package com.espressodev.gptmap.feature.screenshot_gallery

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object ScreenshotGallery

fun NavController.navigateToScreenshotGallery(navOptions: NavOptions? = null) {
    navigate(ScreenshotGallery, navOptions = navOptions)
}

fun NavGraphBuilder.screenshotGalleryScreen(
    popUp: () -> Unit,
    navigateToSnapToScript: (String) -> Unit,
) {
    composable<ScreenshotGallery> {
        ScreenshotGalleryRoute(popUp = popUp, navigateToSnapToScript = navigateToSnapToScript)
    }
}
