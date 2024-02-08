package com.espressodev.gptmap.feature.snapTo_script

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val IMAGE_ID = "imageId"
const val SnapToScriptRoute = "snapTo_script_route"
const val SnapToScriptRouteWithArg = "$SnapToScriptRoute/{$IMAGE_ID}"

fun NavController.navigateToSnapToScript(imageId: String, navOptions: NavOptions? = null) {
    navigate("$SnapToScriptRoute/$imageId", navOptions)
}

fun NavGraphBuilder.snapToScriptScreen(popUp: () -> Unit) {
    composable(
        route = SnapToScriptRouteWithArg,
        arguments = listOf(navArgument(IMAGE_ID) { type = NavType.StringType })
    ) {
        SnapToScriptRoute()
    }
}
