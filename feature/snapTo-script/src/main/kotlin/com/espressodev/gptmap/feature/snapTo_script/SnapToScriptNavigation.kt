package com.espressodev.gptmap.feature.snapTo_script

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

const val IMAGE_ID = "imageId"

@Serializable
data class SnapToScript(val imageId: String)

fun NavController.navigateToSnapToScript(imageId: String, navOptions: NavOptions? = null) {
    navigate(SnapToScript(imageId), navOptions)
}

fun NavGraphBuilder.snapToScriptScreen() {
    composable<SnapToScript> {
        SnapToScriptRoute()
    }
}
