package com.espressodev.gptmap.feature.map

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions

const val MapRoute = "map_route"
const val FavouriteId = "favId"
const val MapRouteWithArg = "$MapRoute/{$FavouriteId}"
fun NavController.navigateToMap(
    favouriteId: String = "default",
    navOptions: NavOptions? = navOptions {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
) {
    navigate("$MapRoute/$favouriteId", navOptions)
}

fun NavGraphBuilder.mapScreen(
    navigateToStreetView: (Float, Float) -> Unit,
    navigateToScreenshot: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToSnapToScript: (String) -> Unit,
    navigateToGallery: () -> Unit
) {
    composable(
        route = "$MapRoute/{$FavouriteId}",
        arguments = listOf(navArgument(FavouriteId) { type = NavType.StringType })
    ) {
        MapRoute(
            navigateToStreetView = { locPair ->
                navigateToStreetView(locPair.first, locPair.second)
            },
            navigateToScreenshot = navigateToScreenshot,
            navigateToProfile = navigateToProfile,
            navigateToSnapToScript = navigateToSnapToScript,
            navigateToGallery = navigateToGallery,
        )
    }
}
