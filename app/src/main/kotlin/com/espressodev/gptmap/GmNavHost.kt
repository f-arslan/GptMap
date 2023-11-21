package com.espressodev.gptmap

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.espressodev.gptmap.feature.login.loginRoute
import com.espressodev.gptmap.feature.login.loginScreen
import com.espressodev.gptmap.feature.map.mapRoute
import com.espressodev.gptmap.feature.map.mapScreen
import com.espressodev.gptmap.feature.register.registerRoute
import com.espressodev.gptmap.feature.register.registerScreen

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
        mapScreen(navigateToStreetView = {})
        loginScreen(
            navigateToMap = { appState.clearAndNavigate(mapRoute) },
            navigateToRegister = { appState.navigate(registerRoute) },
            navigateToForgotPassword = { appState.navigate("") }
        )
        registerScreen(
            clearAndNavigateLogin = { appState.clearAndNavigate(loginRoute) },
            clearAndNavigateMap = { appState.clearAndNavigate(mapRoute) }
        )
    }
}