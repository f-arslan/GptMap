package com.espressodev.gptmap.feature.favourite

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Favourite

fun NavController.navigateToFavourite(navOptions: NavOptions? = null) {
    navigate(Favourite, navOptions)
}

fun NavGraphBuilder.favouriteScreen(
    popUp: () -> Unit,
    navigateToMap: (String) -> Unit,
) {
    composable<Favourite> {
        FavouriteRoute(popUp = popUp, navigateToMap = navigateToMap)
    }
}
