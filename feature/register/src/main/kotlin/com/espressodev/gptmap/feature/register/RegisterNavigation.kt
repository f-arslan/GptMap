package com.espressodev.gptmap.feature.register

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Register

fun NavController.navigateToRegister(navOptions: NavOptions? = null) {
    navigate(Register, navOptions)
}

fun NavGraphBuilder.registerScreen(
    navigateToLogin: () -> Unit,
    navigateToMap: () -> Unit
) {
    composable<Register> {
        RegisterRoute(
            navigateToLogin = navigateToLogin,
            navigateToMap = navigateToMap
        )
    }
}
