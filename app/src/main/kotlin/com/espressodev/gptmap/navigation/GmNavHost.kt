package com.espressodev.gptmap.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.espressodev.gptmap.GmAppState
import com.espressodev.gptmap.feature.favourite.favouriteScreen
import com.espressodev.gptmap.feature.forgot_password.forgotPasswordScreen
import com.espressodev.gptmap.feature.forgot_password.navigateToForgotPassword
import com.espressodev.gptmap.feature.info.infoScreen
import com.espressodev.gptmap.feature.info.navigateToInfo
import com.espressodev.gptmap.feature.login.LoginRoute
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
import com.espressodev.gptmap.feature.screenshot_gallery.screenshotGalleryScreen
import com.espressodev.gptmap.feature.street_view.navigateToStreetView
import com.espressodev.gptmap.feature.street_view.streetViewScreen

@Composable
fun GmNavHost(
    appState: GmAppState,
    modifier: Modifier = Modifier,
    startDestination: String = LoginRoute
) {
    val navController = appState.navController
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        mapScreen(
            navigateToStreetView = navController::navigateToStreetView,
            navigateToScreenshot = navController::navigateToScreenshot,
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
            popUp = navController::navigateToMap,
            navigateToScreenshot = navController::navigateToScreenshot
        )
        favouriteScreen(
            popUp = navController::popBackStack,
            navigateToMap = navController::navigateToMap
        )
        screenshotScreen(popUp = navController::popBackStack)
        screenshotGalleryScreen(popUp = navController::popBackStack)
        profileScreen(
            popUp = navController::popBackStack,
            navigateToLogin = navController::navigateToLogin,
            navigateToInfo = navController::navigateToInfo
        )
        infoScreen(popUp = navController::popBackStack)
    }

    // Debug purposes
    // navController.NavigationListener()
}

@SuppressLint("RestrictedApi")
@Composable
private fun NavHostController.NavigationListener() {
    LaunchedEffect(this) {
        this@NavigationListener.addOnDestinationChangedListener { controller, destination, arguments ->
            println("Current back stack: ${destination.route}")
            println("Parent back stack: ${controller.previousBackStackEntry?.destination}")
            println("Arguments: $arguments")
        }
        this@NavigationListener.currentBackStack.collect {
            it.forEach { backStackEntry ->
                println("Back stack: ${backStackEntry.destination.route}")
            }
        }
    }
}
