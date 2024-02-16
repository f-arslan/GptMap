package com.espressodev.gptmap.feature.street_view

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
    ) {
        StreetViewRoute(popUp = popUp, navigateToScreenshot = navigateToScreenshot)
    }
}
