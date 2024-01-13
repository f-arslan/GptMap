package com.espressodev.gptmap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.espressodev.gptmap.feature.favourite.favouriteScreen
import com.espressodev.gptmap.feature.favourite.navigateToFavourite
import com.espressodev.gptmap.feature.forgot_password.forgotPasswordScreen
import com.espressodev.gptmap.feature.forgot_password.navigateToForgotPassword
import com.espressodev.gptmap.feature.login.LOGIN_ROUTE
import com.espressodev.gptmap.feature.login.loginScreen
import com.espressodev.gptmap.feature.map.MAP_ROUTE
import com.espressodev.gptmap.feature.map.mapScreen
import com.espressodev.gptmap.feature.map.navigateToMap
import com.espressodev.gptmap.feature.register.navigateToRegister
import com.espressodev.gptmap.feature.register.registerScreen
import com.espressodev.gptmap.feature.screenshot.navigateToScreenshot
import com.espressodev.gptmap.feature.screenshot.screenshotScreen
import com.espressodev.gptmap.feature.screenshot_gallery.navigateToScreenshotGallery
import com.espressodev.gptmap.feature.screenshot_gallery.screenshotGalleryScreen
import com.espressodev.gptmap.feature.street_view.navigateToStreetView
import com.espressodev.gptmap.feature.street_view.streetViewScreen

@Composable
fun GmNavHost(
    appState: GmAppState,
    modifier: Modifier = Modifier,
    startDestination: String = LOGIN_ROUTE
) {
    val navController = appState.navController

    NavigationListener(navController)

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        mapScreen(
            navigateToStreetView = { latitude, longitude ->
                navController.navigateToStreetView(latitude, longitude)
            },
            navigateToFavourite = navController::navigateToFavourite,
            navigateToScreenshot = navController::navigateToScreenshot,
            navigateToScreenshotGallery = navController::navigateToScreenshotGallery
        )
        loginScreen(
            navigateToMap = navController::navigateToMap,
            navigateToRegister = navController::navigateToRegister,
            navigateToForgotPassword = navController::navigateToForgotPassword
        )
        registerScreen(
            navigateToLogin = { appState.clearAndNavigate(LOGIN_ROUTE) },
            navigateToMap = { appState.clearAndNavigate(MAP_ROUTE) }
        )
        forgotPasswordScreen(navigateToLogin = { appState.clearAndNavigate(LOGIN_ROUTE) })
        streetViewScreen(popUp = appState::popUp)
        favouriteScreen(
            popUp = appState::popUp,
            navigateToMap = { favouriteId -> appState.navController.navigateToMap(favouriteId) }
        )
        screenshotScreen(popUp = appState::popUp, navigateToMap = navController::navigateToMap)
        screenshotGalleryScreen(popUp = appState::popUp)
    }
}

@Composable
private fun NavigationListener(navController: NavHostController) {
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            println("Current back stack: $destination")
            println("Parent back stack: ${controller.previousBackStackEntry?.destination}")
            println("Arguments: $arguments")
        }
    }
}
