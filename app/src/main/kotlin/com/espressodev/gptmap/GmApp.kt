package com.espressodev.gptmap

import android.content.res.Resources
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.espressodev.gptmap.core.common.network_monitor.NetworkMonitor
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.designsystem.Constants.MEDIUM_PADDING
import com.espressodev.gptmap.feature.login.LOGIN_ROUTE
import com.espressodev.gptmap.feature.map.MAP_ROUTE
import kotlinx.coroutines.CoroutineScope

@Composable
fun GmApp(
    networkMonitor: NetworkMonitor,
    accountState: AccountState,
    appState: GmAppState = rememberAppState(networkMonitor = networkMonitor)
) {
    val startDestination = when (accountState) {
        AccountState.UserAlreadySignIn -> "$MAP_ROUTE/{favouriteId}"
        else -> LOGIN_ROUTE
    }

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = appState.snackbarHostState,
                    modifier = Modifier.padding(MEDIUM_PADDING),
                    snackbar = { snackbarData -> Snackbar(snackbarData) },
                )
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            GmNavHost(
                appState = appState,
                modifier = Modifier.padding(it),
                startDestination = startDestination
            )
        }
    }
}

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
