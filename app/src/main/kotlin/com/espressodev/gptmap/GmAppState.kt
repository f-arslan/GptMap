package com.espressodev.gptmap

import android.content.res.Resources
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import com.espressodev.gptmap.core.common.network_monitor.NetworkMonitor
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.common.snackbar.SnackbarMessage.Companion.toMessage
import com.espressodev.gptmap.core.common.splash_navigation.AfterSplashState
import com.espressodev.gptmap.core.common.splash_navigation.SplashNavigationManager
import com.espressodev.gptmap.feature.map.navigateToMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.junit.After

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
}
