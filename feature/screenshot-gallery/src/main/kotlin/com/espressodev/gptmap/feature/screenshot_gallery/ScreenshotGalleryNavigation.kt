package com.espressodev.gptmap.feature.screenshot_gallery

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation

const val ScreenshotGalleryGraph = "screenshot_gallery_graph"
const val ScreenshotGalleryRoute = "screenshot_gallery_route"

fun NavController.navigateToScreenshotGallery(navOptions: NavOptions? = null) {
    navigate(ScreenshotGalleryRoute, navOptions = navOptions)
}

fun NavGraphBuilder.screenshotGalleryScreen(
    popUp: () -> Unit,
    navigateToSnapToScript: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(route = ScreenshotGalleryGraph, startDestination = ScreenshotGalleryRoute) {
        composable(ScreenshotGalleryRoute) {
            ScreenshotGalleryRoute(popUp = popUp, navigateToSnapToScript = navigateToSnapToScript)
        }
        nestedGraphs()
    }
}
