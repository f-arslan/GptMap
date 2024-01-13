package com.espressodev.gptmap.feature.screenshot

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val SCREENSHOT_ROUTE = "screenshot_route"

fun NavController.navigateToScreenshot(navOptions: NavOptions? = null) {
    navigate(SCREENSHOT_ROUTE)
}

fun NavGraphBuilder.screenshotScreen(popUp: () -> Unit, navigateToMap: () -> Unit) {
    composable(SCREENSHOT_ROUTE) {
        ScreenshotRoute(popUp = popUp, navigateToMap)
    }
}