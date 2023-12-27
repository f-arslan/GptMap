package com.espressodev.gptmap.feature.favourite

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val favouriteRoute = "favourite_route"

fun NavController.navigateToFavourite(navOptions: NavOptions? = null) {
    navigate(favouriteRoute, navOptions)
}

fun NavGraphBuilder.favouriteScreen(popUp: () -> Unit, navigateToMap: (String) -> Unit) {
    composable(favouriteRoute) {
        FavouriteRoute(popUp = popUp, navigateToMap = navigateToMap)
    }
}