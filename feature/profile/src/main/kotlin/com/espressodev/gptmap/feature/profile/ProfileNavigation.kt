package com.espressodev.gptmap.feature.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val PROFILE_ROUTE = "profile_route"

fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    navigate(PROFILE_ROUTE, navOptions)
}

fun NavGraphBuilder.profileScreen(popUp: () -> Unit, navigateToLogin: () -> Unit) {
    composable(PROFILE_ROUTE) {
        ProfileRoute(popUp = popUp, navigateToLogin = navigateToLogin)
    }
}