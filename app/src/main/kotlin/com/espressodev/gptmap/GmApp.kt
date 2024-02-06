package com.espressodev.gptmap

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.espressodev.gptmap.core.common.NetworkMonitor
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.feature.login.LoginRoute
import com.espressodev.gptmap.feature.screenshot_gallery.ScreenshotGalleryGraph
import com.espressodev.gptmap.navigation.GmNavHost
import com.espressodev.gptmap.navigation.TopLevelDestination
import kotlinx.coroutines.CoroutineScope
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GmApp(
    networkMonitor: NetworkMonitor,
    accountState: AccountState,
    appState: GmAppState = rememberAppState(networkMonitor = networkMonitor)
) {
    val startDestination = when (accountState) {
        AccountState.UserAlreadySignIn -> ScreenshotGalleryGraph
        else -> LoginRoute
    }

    val isOffline by appState.isOffline.collectAsStateWithLifecycle()
    val message = stringResource(id = AppText.not_connected)
    LaunchedEffect(isOffline) {
        if (isOffline) {
            appState.snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Indefinite
            )
        }
    }

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = appState.snackbarHostState,
                    modifier = Modifier.padding(4.dp),
                    snackbar = { snackbarData -> Snackbar(snackbarData) },
                )
            },
            bottomBar = {
                GmBottomNavigation(
                    destinations = appState.topLevelDestination,
                    onNavigateToDestination = appState::navigateToTopLevelDestination,
                    currentDestination = appState.currentDestination,
                    currentTopLevelDestination = appState.currentTopLevelDestination
                )
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            GmNavHost(
                appState = appState,
                startDestination = startDestination
            )
        }
    }
}

@Composable
fun GmBottomNavigation(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    currentTopLevelDestination: TopLevelDestination?,
    modifier: Modifier = Modifier,
) {
    if (currentTopLevelDestination == null) return
    BottomAppBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        for (destination in destinations) {
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            GmNavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = destination.unSelectedIcon,
                        contentDescription = stringResource(id = destination.contentDesc)
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = stringResource(id = destination.contentDesc),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                label = {
                    Text(text = stringResource(id = destination.contentDesc))
                }
            )
        }
    }
}

@Composable
fun RowScope.GmNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: @Composable () -> Unit = icon,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
    )
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, ignoreCase = true) == true
    } == true

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

@Composable
fun rememberAppState(
    networkMonitor: NetworkMonitor,
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(navController, snackbarHostState, coroutineScope) {
    GmAppState(
        networkMonitor,
        navController,
        snackbarHostState,
        snackbarManager,
        resources,
        coroutineScope
    )
}
