package com.espressodev.gptmap.feature.map

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val mapRoute = "map_route"
const val FAVOURITE_ID = "favouriteId"
fun NavController.navigateToMap(favouriteId: String = "default") {
    navigate("$mapRoute/$favouriteId") {
        launchSingleTop = true
        popUpTo(0) { inclusive = true }
    }
}

fun NavGraphBuilder.mapScreen(
    navigateToStreetView: (Float, Float) -> Unit,
    navigateToFavourite: () -> Unit
) {
    composable(
        route = "$mapRoute/{$FAVOURITE_ID}",
        arguments = listOf(navArgument(FAVOURITE_ID) { type = NavType.StringType })
    ) {
        val favouriteId = it.arguments?.getString(FAVOURITE_ID) ?: "default"
        MapRoute(
            navigateToStreetView = navigateToStreetView,
            navigateToFavourite = navigateToFavourite,
            favouriteId = favouriteId
        )
    }
}