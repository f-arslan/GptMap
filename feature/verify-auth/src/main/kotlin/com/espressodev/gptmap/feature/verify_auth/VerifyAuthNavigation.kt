package com.espressodev.gptmap.feature.verify_auth

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val VerifyAuthRoute = "verify_auth_route"

fun NavController.navigateToVerifyAuth(navOptions: NavOptions? = null) {
    navigate(VerifyAuthRoute, navOptions)
}


fun NavGraphBuilder.verifyAuthScreen(popUp: () -> Unit, navigateToDelete: () -> Unit) {
    composable(VerifyAuthRoute) {
        VerifyAuthRoute(
            popUp = popUp,
            navigateToDelete = navigateToDelete
        )
    }
}