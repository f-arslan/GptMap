package com.espressodev.gptmap

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.espressodev.gptmap.feature.favourite.favouriteRoute
import com.espressodev.gptmap.feature.favourite.favouriteScreen
import com.espressodev.gptmap.feature.forgot_password.forgotPasswordRoute
import com.espressodev.gptmap.feature.forgot_password.forgotPasswordScreen
import com.espressodev.gptmap.feature.login.loginRoute
import com.espressodev.gptmap.feature.login.loginScreen
import com.espressodev.gptmap.feature.map.mapRoute
import com.espressodev.gptmap.feature.map.mapScreen
import com.espressodev.gptmap.feature.register.registerRoute
import com.espressodev.gptmap.feature.register.registerScreen
import com.espressodev.gptmap.feature.street_view.navigateToStreetView
import com.espressodev.gptmap.feature.street_view.streetViewScreen

@Composable
fun GmNavHost(
    appState: GmAppState,
    modifier: Modifier = Modifier,
    startDestination: String = loginRoute
) {
    NavHost(
        modifier = modifier,
        navController = appState.navController,
        startDestination = startDestination
    ) {
        mapScreen(
            navigateToStreetView = { latitude, longitude ->
                appState.navController.navigateToStreetView(latitude, longitude)
            },
            navigateToFavourite = { appState.navigate(favouriteRoute) }
        )
        loginScreen(
            navigateToMap = { appState.clearAndNavigate(mapRoute) },
            navigateToRegister = { appState.navigate(registerRoute) },
            navigateToForgotPassword = { appState.navigate(forgotPasswordRoute) }
        )
        registerScreen(
            navigateToLogin = { appState.clearAndNavigate(loginRoute) },
            navigateToMap = { appState.clearAndNavigate(mapRoute) }
        )
        forgotPasswordScreen(navigateToLogin = { appState.clearAndNavigate(loginRoute) })
        streetViewScreen(popUp = appState::popUp)
        favouriteScreen(popUp = appState::popUp)
    }
}
