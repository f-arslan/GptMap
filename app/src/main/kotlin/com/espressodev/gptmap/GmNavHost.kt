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
import com.espressodev.gptmap.feature.login.navigateToLogin
import com.espressodev.gptmap.feature.map.mapScreen
import com.espressodev.gptmap.feature.map.navigateToMap
import com.espressodev.gptmap.feature.profile.navigateToProfile
import com.espressodev.gptmap.feature.profile.profileScreen
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

    navController.NavigationListener()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        mapScreen(
            navigateToStreetView = navController::navigateToStreetView,
            navigateToFavourite = navController::navigateToFavourite,
            navigateToScreenshot = navController::navigateToScreenshot,
            navigateToScreenshotGallery = navController::navigateToScreenshotGallery,
            navigateToProfile = navController::navigateToProfile
        )
        loginScreen(
            navigateToMap = navController::navigateToMap,
            navigateToRegister = navController::navigateToRegister,
            navigateToForgotPassword = navController::navigateToForgotPassword
        )
        registerScreen(
            navigateToLogin = navController::navigateToLogin,
            navigateToMap = navController::navigateToMap
        )
        forgotPasswordScreen(navigateToLogin = navController::navigateToLogin)
        streetViewScreen(
            popUp = navController::popBackStack,
            navigateToScreenshot = navController::navigateToScreenshot
        )
        favouriteScreen(
            popUp = navController::popBackStack,
            navigateToMap = navController::navigateToMap
        )
        screenshotScreen(
            popUp = navController::popBackStack,
            navigateToMap = navController::navigateToMap
        )
        screenshotGalleryScreen(popUp = navController::popBackStack)
        profileScreen(
            popUp = navController::popBackStack,
            navigateToLogin = navController::navigateToLogin
        )
    }
}

@Composable
private fun NavHostController.NavigationListener() {
    LaunchedEffect(this) {
        this@NavigationListener.addOnDestinationChangedListener { controller, destination, arguments ->
            println("Current back stack: $destination")
            println("Parent back stack: ${controller.previousBackStackEntry?.destination}")
            println("Arguments: $arguments")
        }
    }
}
