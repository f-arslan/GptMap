package com.espressodev.gptmap.feature.delete_profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val DeleteProfileRoute = "delete_profile_route"

fun NavController.navigateToDeleteProfile(navOptions: NavOptions? = null) {
    navigate(DeleteProfileRoute, navOptions)
}

fun NavGraphBuilder.deleteProfileScreen(popUp: () -> Unit, navigateToLogin: () -> Unit) {
    composable(DeleteProfileRoute) {
        DeleteProfileRoute(popUp = popUp, navigateToLogin = navigateToLogin)
    }
}
