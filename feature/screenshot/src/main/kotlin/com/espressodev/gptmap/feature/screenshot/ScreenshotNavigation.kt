package com.espressodev.gptmap.feature.screenshot

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Screenshot

fun NavController.navigateToScreenshot(navOptions: NavOptions? = null) {
    navigate(Screenshot, navOptions)
}

fun NavGraphBuilder.screenshotScreen(popUp: () -> Unit) {
    composable<Screenshot> {
        ScreenshotRoute(popUp = popUp)
    }
}
