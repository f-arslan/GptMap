package com.espressodev.gptmap.feature.street_view

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.save_screenshot.SaveScreenshotService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreetViewViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    logService: LogService,
    savedStateHandle: SavedStateHandle
) : GmViewModel(logService) {

    private val latitude: Float = checkNotNull(savedStateHandle[LATITUDE_ID])
    private val longitude: Float = checkNotNull(savedStateHandle[LONGITUDE_ID])

    private val _uiState = MutableStateFlow(
        StreetViewUiState(
            latitude = latitude.toDouble(),
            longitude = longitude.toDouble()
        )
    )
    val uiState = _uiState.asStateFlow()

    private val serviceStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                SaveScreenshotService.ACTION_SERVICE_STARTED -> {}

                SaveScreenshotService.ACTION_SERVICE_STOPPED -> {
                    if (uiState.value.screenshotState == ScreenshotState.STARTED) {
                        viewModelScope.launch {
                            delay(50L)
                            _uiState.update { it.copy(screenshotState = ScreenshotState.FINISHED) }
                        }
                        applicationContext.unregisterReceiver(this)
                    }
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun initializeScreenCaptureBroadcastReceiver() = launchCatching {
        _uiState.update {
            it.copy(
                isScreenshotButtonVisible = false,
                screenshotState = ScreenshotState.STARTED
            )
        }
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

    fun reset() {
        _uiState.update {
            it.copy(
                isScreenshotButtonVisible = true,
                screenshotState = ScreenshotState.IDLE
            )
        }
    }

    override fun onCleared() {
        launchCatching {
            applicationContext.unregisterReceiver(serviceStateReceiver)
        }
        super.onCleared()
    }
}
