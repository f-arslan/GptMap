package com.espressodev.gptmap.feature.register

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val registerRoute = "register_route"

fun NavController.navigateToRegister(navOptions: NavOptions? = null) {
    navigate(registerRoute, navOptions)
}

fun NavGraphBuilder.registerScreen(
    clearAndNavigateLogin: () -> Unit,
    clearAndNavigateMap: () -> Unit
) {
    composable(registerRoute) {
        RegisterRoute(
            clearAndNavigateLogin = clearAndNavigateLogin,
            clearAndNavigateMap = clearAndNavigateMap
        )
    }
}