package com.espressodev.gptmap.feature.favourite

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val FavouriteRoute = "favourite_route"

fun NavController.navigateToFavourite(navOptions: NavOptions? = null) {
    navigate(FavouriteRoute, navOptions)
}

fun NavGraphBuilder.favouriteScreen(popUp: () -> Unit, navigateToMap: (String) -> Unit) {
    composable(FavouriteRoute) {
        FavouriteRoute(popUp = popUp, navigateToMap = navigateToMap)
    }
}
