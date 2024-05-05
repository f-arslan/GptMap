package com.espressodev.gptmap.feature.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
data object Profile

@Serializable
data object ProfileGraph

fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    navigate(Profile, navOptions)
}

fun NavGraphBuilder.profileScreen(
    popUp: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToInfo: () -> Unit,
    navigateToDelete: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation<ProfileGraph>(startDestination = Profile) {
        composable<Profile> {
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
