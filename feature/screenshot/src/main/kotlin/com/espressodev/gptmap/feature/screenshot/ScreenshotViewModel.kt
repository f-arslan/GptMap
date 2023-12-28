package com.espressodev.gptmap.feature.screenshot

import android.graphics.Bitmap
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

    fun captureScreenshot() {
        uiState.value.callback?.invoke()
    }

    fun onImageStateChanged(imageResult: ImageResult) =
        _uiState.update { it.copy(imageResult = imageResult) }


    fun onBitmapStateChanged(bitmap: Bitmap?) =
        _uiState.update { it.copy(bitmapState = bitmap) }

    fun onDialogStateChanged(dialogState: Boolean) =
        _uiState.update { it.copy(dialogState = dialogState) }

    fun onCallbackStateChanged(callback: (() -> Unit)?) =
        _uiState.update { it.copy(callback = callback) }

}