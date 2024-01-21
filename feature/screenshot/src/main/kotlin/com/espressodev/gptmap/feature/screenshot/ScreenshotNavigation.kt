package com.espressodev.gptmap.feature.screenshot

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val ScreenshotRoute = "screenshot_route"

fun NavController.navigateToScreenshot(navOptions: NavOptions? = null) {
    navigate(ScreenshotRoute, navOptions)
}

fun NavGraphBuilder.screenshotScreen(popUp: () -> Unit, navigateToMap: () -> Unit) {
    composable(ScreenshotRoute) {
        ScreenshotRoute(popUp = popUp, navigateToMap)
    }
}
