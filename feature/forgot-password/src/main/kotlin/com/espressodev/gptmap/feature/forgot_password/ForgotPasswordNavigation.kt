package com.espressodev.gptmap.feature.forgot_password

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val forgotPasswordRoute = "forgot_password_route"

fun NavController.navigateToForgotPassword(navOptions: NavOptions? = null) {
    navigate(forgotPasswordRoute, navOptions)
}

fun NavGraphBuilder.forgotPasswordScreen(
    navigateToLogin: () -> Unit,
) {
    composable(route = forgotPasswordRoute) {
        ForgotPasswordRoute(
            clearAndNavigateLogin = navigateToLogin,
        )
    }
}