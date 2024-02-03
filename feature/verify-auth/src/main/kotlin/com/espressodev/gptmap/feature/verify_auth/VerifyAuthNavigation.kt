package com.espressodev.gptmap.feature.verify_password

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val VerifyPasswordRoute = "verify_password_route"

fun NavController.navigateToVerifyPassword(navOptions: NavOptions? = null) {
    navigate(VerifyPasswordRoute, navOptions)
}


fun NavGraphBuilder.verifyPasswordScreen(popUp: () -> Unit, navigateToDelete: () -> Unit) {
    composable(VerifyPasswordRoute) {
        VerifyPasswordRoute(
            popUp = popUp,
            navigateToDelete = navigateToDelete
        )
    }
}