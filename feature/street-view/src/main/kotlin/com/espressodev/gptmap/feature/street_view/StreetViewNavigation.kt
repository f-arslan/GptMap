package com.espressodev.gptmap.feature.street_view

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument


const val LATITUDE_ID = "latitudeId"
const val LONGITUDE_ID = "longitudeId"
const val StreetViewRoute = "streetViewRoute"
const val StreetViewRouteWithArg = "$StreetViewRoute/{$LATITUDE_ID}/{$LONGITUDE_ID}"
fun NavController.navigateToStreetView(latitude: Float, longitude: Float) {
    navigate("$StreetViewRoute/$latitude/$longitude")
}

fun NavGraphBuilder.streetViewScreen(popUp: () -> Unit, navigateToScreenshot: () -> Unit) {
    composable(
        route = StreetViewRouteWithArg,
        arguments = listOf(
            navArgument(LATITUDE_ID) { type = NavType.FloatType },
            navArgument(LONGITUDE_ID) { type = NavType.FloatType }
        )
    ) { backStackEntry ->
        val latitude =
            backStackEntry.arguments?.getFloat(LATITUDE_ID)?.toDouble() ?: return@composable
        val longitude =
            backStackEntry.arguments?.getFloat(LONGITUDE_ID)?.toDouble() ?: return@composable
        LaunchedEffect(latitude, longitude) {
            Log.d("streetViewScreen", "latitude: $latitude, longitude: $longitude")
        }
        StreetViewRoute(
            latitude = latitude,
            longitude = longitude,
            popUp = popUp,
            navigateToScreenshot = navigateToScreenshot
        )
    }
}
