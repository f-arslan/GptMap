package com.espressodev.gptmap.feature.street_view

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument


const val LATITUDE_ID = "latitudeId"
const val LONGITUDE_ID = "longitudeId"
const val streetViewRoute = "streetViewRoute"

fun NavController.navigateToStreetView(latitude: Float, longitude: Float) {
    navigate("$streetViewRoute/$latitude/$longitude")
}

fun NavGraphBuilder.streetViewScreen(popUp: () -> Unit, navigateToScreenshot: () -> Unit) {
    composable(
        route = "$streetViewRoute/{$LATITUDE_ID}/{$LONGITUDE_ID}",
        arguments = listOf(
            navArgument(LATITUDE_ID) { type = NavType.FloatType },
            navArgument(LONGITUDE_ID) { type = NavType.FloatType }
        )
    ) { backStackEntry ->
        val latitude =
            backStackEntry.arguments?.getFloat(LATITUDE_ID)?.toDouble() ?: return@composable
        val longitude =
            backStackEntry.arguments?.getFloat(LONGITUDE_ID)?.toDouble() ?: return@composable
        StreetViewRoute(
            latitude = latitude,
            longitude = longitude,
            popUp = popUp,
            navigateToScreenshot = navigateToScreenshot
        )
    }
}
