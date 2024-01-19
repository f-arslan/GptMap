package com.espressodev.gptmap.feature.map

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val MAP_ROUTE = "map_route"
const val FAVOURITE_ID = "favouriteId"
fun NavController.navigateToMap(favouriteId: String = "default") {
    navigate("$MAP_ROUTE/$favouriteId") {
        launchSingleTop = true
        popUpTo(0) { inclusive = true }
    }
}

fun NavGraphBuilder.mapScreen(
    navigateToStreetView: (Float, Float) -> Unit,
    navigateToFavourite: () -> Unit,
    navigateToScreenshot: () -> Unit,
    navigateToScreenshotGallery: () -> Unit,
    navigateToProfile: () -> Unit
) {
    composable(
        route = "$MAP_ROUTE/{$FAVOURITE_ID}",
        arguments = listOf(navArgument(FAVOURITE_ID) { type = NavType.StringType })
    ) {
        val favouriteId = it.arguments?.getString(FAVOURITE_ID) ?: "default"
        MapRoute(
            navigateToStreetView = navigateToStreetView,
            navigateToFavourite = navigateToFavourite,
            favouriteId = favouriteId,
            navigateToScreenshot = navigateToScreenshot,
            navigateToScreenshotGallery = navigateToScreenshotGallery,
            navigateToProfile = navigateToProfile
        )
    }
}
