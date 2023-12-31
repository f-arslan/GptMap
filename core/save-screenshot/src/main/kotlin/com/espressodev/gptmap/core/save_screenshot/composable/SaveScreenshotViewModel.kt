package com.espressodev.gptmap.core.save_screenshot.composable

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.save_screenshot.SaveScreenshotService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


enum class ScreenState {
    Idle, Success
}

data class SaveScreenshotUiState(
    val screenState: ScreenState = ScreenState.Idle,
    val isButtonVisible: Boolean = true
)

@HiltViewModel
class ScreenshotViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    logService: LogService
) : GmViewModel(logService) {

    private val _uiState = MutableStateFlow(SaveScreenshotUiState())
    val uiState = _uiState.asStateFlow()

    private val serviceStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                SaveScreenshotService.ACTION_SERVICE_STARTED -> {
                    _uiState.update { it.copy(isButtonVisible = false) }
                }
                SaveScreenshotService.ACTION_SERVICE_STOPPED -> {
                    _uiState.update { it.copy(screenState = ScreenState.Success) }
                }
            }
        }
    }


    init {
        initializeScreenCaptureBroadcastReceiver()
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun initializeScreenCaptureBroadcastReceiver() = launchCatching {
        val filter = IntentFilter().apply {
            addAction(SaveScreenshotService.ACTION_SERVICE_STARTED)
            addAction(SaveScreenshotService.ACTION_SERVICE_STOPPED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            applicationContext.registerReceiver(
                serviceStateReceiver,
                filter,
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            applicationContext.registerReceiver(serviceStateReceiver, filter)
        }
    }

    fun resetScreenState() {
        _uiState.update { it.copy(screenState = ScreenState.Idle, isButtonVisible = true) }
    }
}