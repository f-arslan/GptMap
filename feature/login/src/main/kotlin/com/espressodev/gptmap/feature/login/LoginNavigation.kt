package com.espressodev.gptmap.feature.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable

const val LOGIN_ROUTE = "login_route"

fun NavController.navigateToLogin(
    navOptionsBuilder: NavOptionsBuilder.() -> Unit = {
        launchSingleTop = true
        popUpTo(0) { inclusive = true }
    }
) {
    navigate(LOGIN_ROUTE, builder = navOptionsBuilder)
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