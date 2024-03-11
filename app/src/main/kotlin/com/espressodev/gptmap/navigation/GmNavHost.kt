package com.espressodev.gptmap.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.espressodev.gptmap.GmAppState
import com.espressodev.gptmap.feature.delete_profile.deleteProfileScreen
import com.espressodev.gptmap.feature.delete_profile.navigateToDeleteProfile
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
import com.espressodev.gptmap.feature.snapTo_script.navigateToSnapToScript
import com.espressodev.gptmap.feature.snapTo_script.snapToScriptScreen
import com.espressodev.gptmap.feature.street_view.navigateToStreetView
import com.espressodev.gptmap.feature.street_view.streetViewScreen
import com.espressodev.gptmap.feature.verify_auth.navigateToVerifyAuth
import com.espressodev.gptmap.feature.verify_auth.verifyAuthScreen

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
            navigateToProfile = navController::navigateToProfile,
            navigateToSnapToScript = navController::navigateToSnapToScript,
            navigateToGallery = {
                appState.navigateToTopLevelDestination(TopLevelDestination.SCREENSHOT_GALLERY)
            }
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
            popUp = navController::safePopBackStack,
            navigateToMap = navController::navigateToMap
        )
        screenshotScreen(popUp = navController::safePopBackStack)
        screenshotGalleryScreen(
            popUp = navController::safePopBackStack,
            navigateToSnapToScript = navController::navigateToSnapToScript,
        )
        snapToScriptScreen()
        profileScreen(
            popUp = navController::safePopBackStack,
            navigateToLogin = navController::navigateToLogin,
            navigateToInfo = navController::navigateToInfo,
            navigateToDelete = navController::navigateToVerifyAuth,
            nestedGraphs = {
                infoScreen(popUp = navController::safePopBackStack)
                deleteProfileScreen(
                    popUp = {
                        navController.popBackStack()
                        navController.safePopBackStack()
                    },
                    navigateToLogin = navController::navigateToLogin
                )
                verifyAuthScreen(
                    popUp = navController::safePopBackStack,
                    navigateToDelete = navController::navigateToDeleteProfile
                )
            }
        )
    }

    // Debug purposes
    // navController.NavigationListener()
}

fun NavHostController.safePopBackStack() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED)
        popBackStack()
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
