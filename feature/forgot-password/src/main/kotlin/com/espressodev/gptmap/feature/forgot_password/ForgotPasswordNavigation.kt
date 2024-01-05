package com.espressodev.gptmap.feature.forgot_password

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val FORGOT_PASSWORD_ROUTE = "forgot_password_route"

fun NavController.navigateToForgotPassword(navOptions: NavOptions? = null) {
    navigate(FORGOT_PASSWORD_ROUTE, navOptions)
}

fun NavGraphBuilder.forgotPasswordScreen(
    navigateToLogin: () -> Unit,
) {
    composable(route = FORGOT_PASSWORD_ROUTE) {
        ForgotPasswordRoute(
            clearAndNavigateLogin = navigateToLogin,
        )
    }
}
