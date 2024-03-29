package com.espressodev.gptmap.feature.forgot_password

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val ForgotPasswordRoute = "forgot_password_route"

fun NavController.navigateToForgotPassword(navOptions: NavOptions? = null) {
    navigate(ForgotPasswordRoute, navOptions)
}

fun NavGraphBuilder.forgotPasswordScreen(
    navigateToLogin: () -> Unit,
) {
    composable(route = ForgotPasswordRoute) {
        ForgotPasswordRoute(
            clearAndNavigateLogin = navigateToLogin,
        )
    }
}
