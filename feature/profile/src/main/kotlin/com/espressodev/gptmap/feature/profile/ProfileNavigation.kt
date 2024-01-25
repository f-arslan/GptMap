package com.espressodev.gptmap.feature.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val ProfileRoute = "profile_route"

fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    navigate(ProfileRoute, navOptions)
}

fun NavGraphBuilder.profileScreen(
    popUp: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToInfo: () -> Unit
) {
    composable(ProfileRoute) {
        ProfileRoute(
            popUp = popUp,
            navigateToLogin = navigateToLogin,
            navigateToInfo = navigateToInfo
        )
    }
}