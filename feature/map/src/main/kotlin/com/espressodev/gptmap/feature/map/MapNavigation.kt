package com.espressodev.gptmap.feature.map

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val mapRoute = "map_route"

fun NavController.navigateToMap(navOptions: NavOptions? = null) {
    navigate(mapRoute, navOptions)
}

fun NavGraphBuilder.mapScreen(navigateToStreetView: (Float, Float) -> Unit) {
    composable(mapRoute) {
        MapRoute(navigateToStreetView = navigateToStreetView)
    }
}