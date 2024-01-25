package com.espressodev.gptmap.feature.info

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val InfoRoute = "info_route"

fun NavController.navigateToInfo(navOptions: NavOptions? = null) {
    navigate(InfoRoute, navOptions)
}

fun NavGraphBuilder.infoScreen(popUp: () -> Unit) {
    composable(InfoRoute) {
        InfoRoute(popUp = popUp)
    }
}
