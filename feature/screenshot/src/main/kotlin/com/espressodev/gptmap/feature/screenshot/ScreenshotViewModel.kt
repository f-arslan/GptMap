package com.espressodev.gptmap.feature.screenshot

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class ScreenshotViewModel @Inject constructor(logService: LogService) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(ScreenshotUiState())
    val uiState = _uiState.asStateFlow()
    fun onEvent(event: ScreenshotEvent) {
        when (event) {
            is ScreenshotEvent.OnBitmapStateChanged -> {
                _uiState.update { it.copy(bitmapState = event.bitmap) }
            }
            is ScreenshotEvent.OnCaptureTriggerStateChanged -> {
                _uiState.update { it.copy(captureTriggerState = event.captureTriggerState) }
            }
            is ScreenshotEvent.OnDialogStateChanged -> {
                _uiState.update { it.copy(dialogState = event.dialogState) }
            }
        }
    }
}