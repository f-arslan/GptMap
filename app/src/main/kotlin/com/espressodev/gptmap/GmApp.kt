package com.espressodev.gptmap

import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.GmDraggableButton
import com.espressodev.gptmap.feature.map.mapRoute
import com.espressodev.gptmap.feature.street_view.streetViewRoute
import kotlinx.coroutines.CoroutineScope

@Composable
fun GmApp() {
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        val appState = rememberAppState()
        val currentBackStackEntry by appState.navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStackEntry?.destination?.route
        var screenshotButtonState by remember { mutableStateOf(value = false) }
        LaunchedEffect(currentDestination) {
            screenshotButtonState = currentDestination?.isScreenshotButtonInDestination() == true
        }
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = appState.snackbarHostState,
                    modifier = Modifier.padding(MEDIUM_PADDING),
                    snackbar = { snackbarData -> Snackbar(snackbarData) },
                )
            },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                GmNavHost(appState = appState, modifier = Modifier.padding(it))
                AnimatedVisibility(screenshotButtonState) {
                    GmDraggableButton(icon = GmIcons.CameraFilled, onClick = {})
                }
            }
        }
    }
}

val screenshotAvailableRoutes = listOf(mapRoute, streetViewRoute)

private fun String.isScreenshotButtonInDestination() =
    screenshotAvailableRoutes.any { route -> this.contains(route, ignoreCase = true) }

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(navController, snackbarHostState, coroutineScope) {
    GmAppState(
        navController,
        snackbarHostState,
        snackbarManager,
        resources,
        coroutineScope
    )
}
