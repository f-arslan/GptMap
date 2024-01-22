package com.espressodev.gptmap.feature.map

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val MapRoute = "map_route"
const val FAVOURITE_ID = "favId"
const val MapRouteWithArg = "$MapRoute/{$FAVOURITE_ID}"
fun NavController.navigateToMap(favouriteId: String = "default", navOptions: NavOptions? = null) {
    navigate("$MapRoute/$favouriteId", navOptions = navOptions)
}

fun NavGraphBuilder.mapScreen(
    navigateToStreetView: (Float, Float) -> Unit,
    navigateToScreenshot: () -> Unit,
    navigateToProfile: () -> Unit
) {
    composable(
        route = "$MapRoute/{$FAVOURITE_ID}",
        arguments = listOf(navArgument(FAVOURITE_ID) { type = NavType.StringType })
    ) {
        val favouriteId = it.arguments?.getString(FAVOURITE_ID) ?: "default"
        MapRoute(
            navigateToStreetView = navigateToStreetView,
            navigateToScreenshot = navigateToScreenshot,
            navigateToProfile = navigateToProfile,
            favouriteId = favouriteId
        )
    }
}
