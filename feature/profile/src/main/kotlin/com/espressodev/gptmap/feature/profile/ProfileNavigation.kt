package com.espressodev.gptmap.feature.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation

const val ProfileGraph = "profile_graph"
const val ProfileRoute = "profile_route"

fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    navigate(ProfileRoute, navOptions)
}

fun NavGraphBuilder.profileScreen(
    popUp: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToInfo: () -> Unit,
    navigateToDelete: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(route = ProfileGraph, startDestination = ProfileRoute) {
        composable(ProfileRoute) {
            ProfileRoute(
                popUp = popUp,
                navigateToLogin = navigateToLogin,
                navigateToInfo = navigateToInfo,
                navigateToDelete = navigateToDelete
            )
        }
        nestedGraphs()
    }
}