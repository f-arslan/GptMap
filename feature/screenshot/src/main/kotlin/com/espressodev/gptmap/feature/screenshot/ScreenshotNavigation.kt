package com.espressodev.gptmap.feature.screenshot

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val screenshotRoute = "screenshot_route"

fun NavController.navigateToScreenshot(navOptions: NavOptions? = null) {
    navigate(screenshotRoute)
}

fun NavGraphBuilder.screenshotScreen(popUp: () -> Unit) {
    composable(screenshotRoute) {
        ScreenshotScreen(popUp = popUp)
    }
}