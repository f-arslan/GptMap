package com.espressodev.gptmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.espressodev.gptmap.core.common.NetworkMonitor
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private val viewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        var isEmailVerified by mutableStateOf(AccountState.Loading)

        scopeWithLifecycle {
            viewModel.accountService.collect {
                isEmailVerified = it
            }
        }

        splashScreen.setKeepOnScreenCondition {
            isEmailVerified == AccountState.Loading
        }

        enableEdgeToEdge()
        setContent {
            GptmapTheme {
                if (isEmailVerified != AccountState.Loading)
                    GmApp(networkMonitor, isEmailVerified)
            }
        }
    }

    private fun scopeWithLifecycle(block: suspend CoroutineScope.() -> Unit) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block = block)
        }
    }
}
