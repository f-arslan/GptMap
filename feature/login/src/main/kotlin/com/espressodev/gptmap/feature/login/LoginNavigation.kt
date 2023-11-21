package com.espressodev.gptmap.feature.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val loginRoute = "login_route"

fun NavController.navigateToLogin(navOptions: NavOptions? = null) {
    navigate(loginRoute, navOptions)
}

fun NavGraphBuilder.loginScreen(
    navigateToMap: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToForgotPassword: () -> Unit
) {
    composable(loginRoute) {
        LoginRoute(
            navigateToMap = navigateToMap,
            navigateToRegister = navigateToRegister,
            navigateToForgotPassword = navigateToForgotPassword
        )
    }
}