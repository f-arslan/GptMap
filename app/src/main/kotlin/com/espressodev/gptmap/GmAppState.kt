package com.espressodev.gptmap

import android.content.res.Resources
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import com.espressodev.gptmap.core.common.NetworkMonitor
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.common.snackbar.SnackbarMessage.Companion.toMessage
import com.espressodev.gptmap.feature.favourite.Favourite
import com.espressodev.gptmap.feature.favourite.navigateToFavourite
import com.espressodev.gptmap.feature.map.Map
import com.espressodev.gptmap.feature.map.navigateToMap
import com.espressodev.gptmap.feature.map.toDestinationString
import com.espressodev.gptmap.feature.screenshot_gallery.ScreenshotGallery
import com.espressodev.gptmap.feature.screenshot_gallery.navigateToScreenshotGallery
import com.espressodev.gptmap.navigation.TopLevelDestination
import com.espressodev.gptmap.navigation.TopLevelDestination.FAVOURITE
import com.espressodev.gptmap.navigation.TopLevelDestination.MAP
import com.espressodev.gptmap.navigation.TopLevelDestination.SCREENSHOTGALLERY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
class GmAppState(
    networkMonitor: NetworkMonitor,
    val navController: NavHostController,
    val snackbarHostState: SnackbarHostState,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope,
) {
    init {
        coroutineScope.launch {
            snackbarManager.snackbarMessages.filterNotNull().collect { snackbarMessage ->
                val text = snackbarMessage.first.toMessage(resources)
                snackbarHostState.showSnackbar(
                    text,
                    withDismissAction = true,
                    duration = snackbarMessage.second ?: SnackbarDuration.Short
                )
                snackbarManager.clean()
            }
        }
    }

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val topLevelDestination: List<TopLevelDestination> = TopLevelDestination.entries

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route?.substringAfterLast(".")) {
            Map().toDestinationString() -> MAP
            ScreenshotGallery.toString() -> SCREENSHOTGALLERY
            Favourite.toString() -> FAVOURITE
            else -> null
        }

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo(Map()) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        when (topLevelDestination) {
            MAP -> navController.navigateToMap(navOptions = topLevelNavOptions)
            SCREENSHOTGALLERY -> navController.navigateToScreenshotGallery(topLevelNavOptions)
            FAVOURITE -> navController.navigateToFavourite(topLevelNavOptions)
        }
    }
}
