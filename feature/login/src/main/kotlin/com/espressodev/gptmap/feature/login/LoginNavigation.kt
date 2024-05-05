package com.espressodev.gptmap.feature.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Login

fun NavController.navigateToLogin(
    navOptionsBuilder: NavOptionsBuilder.() -> Unit = {
        launchSingleTop = true
        popUpTo(0) { inclusive = true }
    }
) {
    navigate(Login, builder = navOptionsBuilder)
}

fun NavGraphBuilder.loginScreen(
    navigateToMap: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToForgotPassword: () -> Unit
) {
    composable<Login> {
        LoginRoute(
            navigateToMap = navigateToMap,
            navigateToRegister = navigateToRegister,
            navigateToForgotPassword = navigateToForgotPassword
        )
    }
}
