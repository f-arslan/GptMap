package com.espressodev.gptmap.feature.map

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import kotlinx.serialization.Serializable

const val FavouriteId = "favId"

@Serializable
data class Map(val favId: String = "favId")
fun Map.toDestinationString() = "Map?$favId={$favId}"

fun NavController.navigateToMap(
    favouriteId: String = "default",
    navOptions: NavOptions? = navOptions {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
) {
    navigate(Map(favouriteId), navOptions)
}

fun NavGraphBuilder.mapScreen(
    navigateToStreetView: (Float, Float) -> Unit,
    navigateToScreenshot: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToSnapToScript: (String) -> Unit,
    navigateToGallery: () -> Unit
) {
    composable<Map> {
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
