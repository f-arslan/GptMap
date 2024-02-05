package com.espressodev.gptmap.feature.image_analysis

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.espressodev.gptmap.feature.screenshot_gallery.ScreenshotGalleryRoute
import com.espressodev.gptmap.feature.screenshot_gallery.ScreenshotGalleryViewModel

const val SnapToScriptRoute = "snapTo_script_route"

fun NavController.navigateToSnapToScript(navOptions: NavOptions? = null) {
    navigate(SnapToScriptRoute, navOptions)
}

fun NavGraphBuilder.snapToScriptScreen(popUp: () -> Unit, navController: NavHostController) {
    composable(SnapToScriptRoute) { navBackStackEntry ->
        val parentEntry = remember(navBackStackEntry) {
            navController.getBackStackEntry(ScreenshotGalleryRoute)
        }
        val parentViewModel = hiltViewModel<ScreenshotGalleryViewModel>(viewModelStoreOwner = parentEntry)
        SnapToScriptRoute(popUp = popUp, viewModel = parentViewModel)
    }
}
