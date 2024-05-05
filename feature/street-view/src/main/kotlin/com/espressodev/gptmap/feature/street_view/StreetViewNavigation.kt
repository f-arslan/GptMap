package com.espressodev.gptmap.feature.street_view

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

const val LATITUDE_ID = "latitudeId"
const val LONGITUDE_ID = "longitudeId"

@Serializable
data class StreetView(val latitude: Float, val longitude: Float)

fun NavController.navigateToStreetView(latitude: Float, longitude: Float) {
    navigate(StreetView(latitude, longitude))
}

fun NavGraphBuilder.streetViewScreen(popUp: () -> Unit, navigateToScreenshot: () -> Unit) {
    composable<StreetView> {
        StreetViewRoute(popUp = popUp, navigateToScreenshot = navigateToScreenshot)
    }
}
