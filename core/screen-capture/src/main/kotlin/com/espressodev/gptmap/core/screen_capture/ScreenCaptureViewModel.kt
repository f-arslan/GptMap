package com.espressodev.gptmap.core.screen_capture

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class TakingScreenshotProgress {
    Idle, Started, Finished
}

data class ScreenCaptureUiState(
    val isCameraButtonVisible: Boolean = true,
    val takingScreenshotProgress: TakingScreenshotProgress = TakingScreenshotProgress.Idle
)

@HiltViewModel
class ScreenCaptureViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    logService: LogService
) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(ScreenCaptureUiState())
    val uiState = _uiState.asStateFlow()

    private val serviceStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ScreenCaptureService.ACTION_SERVICE_STARTED -> {
                    _uiState.update { it.copy(takingScreenshotProgress = TakingScreenshotProgress.Started) }
                }

                ScreenCaptureService.ACTION_SERVICE_STOPPED -> {
                    _uiState.update { it.copy(takingScreenshotProgress = TakingScreenshotProgress.Finished) }
                }
            }
        }
    }

    init {
        initializeScreenCaptureBroadcastReceiver()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun initializeScreenCaptureBroadcastReceiver() {
        val filter = IntentFilter().apply {
            addAction(ScreenCaptureService.ACTION_SERVICE_STARTED)
            addAction(ScreenCaptureService.ACTION_SERVICE_STOPPED)
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

    override fun onCleared() {
        applicationContext.unregisterReceiver(serviceStateReceiver)
        super.onCleared()
    }
}