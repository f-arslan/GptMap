package com.espressodev.gptmap.feature.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val LOGIN_ROUTE = "login_route"

fun NavController.navigateToLogin(navOptions: NavOptions? = null) {
    navigate(LOGIN_ROUTE, navOptions)
}

fun NavGraphBuilder.loginScreen(
    navigateToMap: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToForgotPassword: () -> Unit
) {
    composable(LOGIN_ROUTE) {
        LoginRoute(
            navigateToMap = navigateToMap,
            navigateToRegister = navigateToRegister,
            navigateToForgotPassword = navigateToForgotPassword
        )
    }
}