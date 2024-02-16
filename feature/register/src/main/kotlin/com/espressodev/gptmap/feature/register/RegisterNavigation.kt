package com.espressodev.gptmap.feature.register

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val REGISTER_ROUTE = "register_route"

fun NavController.navigateToRegister(navOptions: NavOptions? = null) {
    navigate(REGISTER_ROUTE, navOptions)
}

fun NavGraphBuilder.registerScreen(
    navigateToLogin: () -> Unit,
    navigateToMap: () -> Unit
) {
    composable(REGISTER_ROUTE) {
        RegisterRoute(
            clearAndNavigateLogin = navigateToLogin,
            clearAndNavigateMap = navigateToMap
        )
    }
}
