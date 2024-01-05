package com.espressodev.gptmap.feature.favourite

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val FAVOURITE_ROUTE = "favourite_route"

fun NavController.navigateToFavourite(navOptions: NavOptions? = null) {
    navigate(FAVOURITE_ROUTE, navOptions)
}

fun NavGraphBuilder.favouriteScreen(popUp: () -> Unit, navigateToMap: (String) -> Unit) {
    composable(FAVOURITE_ROUTE) {
        FavouriteRoute(popUp = popUp, navigateToMap = navigateToMap)
    }
}
