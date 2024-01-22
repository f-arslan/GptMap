package com.espressodev.gptmap

import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import com.espressodev.gptmap.core.common.network_monitor.NetworkMonitor
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.common.snackbar.SnackbarMessage.Companion.toMessage
import com.espressodev.gptmap.feature.favourite.FavouriteRoute
import com.espressodev.gptmap.feature.favourite.navigateToFavourite
import com.espressodev.gptmap.feature.map.MapRouteWithArg
import com.espressodev.gptmap.feature.map.navigateToMap
import com.espressodev.gptmap.feature.screenshot_gallery.ScreenshotGalleryRoute
import com.espressodev.gptmap.feature.screenshot_gallery.navigateToScreenshotGallery
import com.espressodev.gptmap.navigation.TopLevelDestination
import com.espressodev.gptmap.navigation.TopLevelDestination.FAVOURITE
import com.espressodev.gptmap.navigation.TopLevelDestination.MAP
import com.espressodev.gptmap.navigation.TopLevelDestination.SCREENSHOT_GALLERY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Stable
class GmAppState(
    networkMonitor: NetworkMonitor,
    val navController: NavHostController,
    val snackbarHostState: SnackbarHostState,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {
    init {
        coroutineScope.launch {
            snackbarManager.snackbarMessages.filterNotNull().collect { snackbarMessage ->
                val text = snackbarMessage.toMessage(resources)
                snackbarHostState.showSnackbar(text, withDismissAction = true)
                snackbarManager.clean()
            }
        }
    }

    val topLevelDestination: List<TopLevelDestination> = TopLevelDestination.entries

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            MapRouteWithArg -> MAP
            ScreenshotGalleryRoute -> SCREENSHOT_GALLERY
            FavouriteRoute -> FAVOURITE
            else -> null
        }

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(MapRouteWithArg) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        when (topLevelDestination) {
            MAP -> navController.navigateToMap(navOptions = topLevelNavOptions)
            SCREENSHOT_GALLERY -> navController.navigateToScreenshotGallery(topLevelNavOptions)
            FAVOURITE -> navController.navigateToFavourite(topLevelNavOptions)
        }
    }
}
