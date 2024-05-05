package com.espressodev.gptmap.feature.info

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object Info

fun NavController.navigateToInfo(navOptions: NavOptions? = null) {
    navigate(Info, navOptions)
}

fun NavGraphBuilder.infoScreen(popUp: () -> Unit) {
    composable<Info> {
        InfoRoute(popUp = popUp)
    }
}
