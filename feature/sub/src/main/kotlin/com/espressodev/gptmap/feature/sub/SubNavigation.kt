package com.espressodev.gptmap.feature.sub

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val SUB_ROUTE = "sub_route"

fun NavController.navigateSubRoute(navOptions: NavOptions? = null) {
    navigate(SUB_ROUTE, navOptions)
}

fun NavGraphBuilder.subScreen() {
    composable(SUB_ROUTE) {
        SubRoute()
    }
}
