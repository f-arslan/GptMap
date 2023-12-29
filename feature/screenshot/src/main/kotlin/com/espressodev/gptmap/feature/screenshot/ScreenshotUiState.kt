package com.espressodev.gptmap.feature.screenshot

import android.graphics.Bitmap

sealed class ImageResult {
    data object Initial : ImageResult()
    data class Error(val exception: Exception) : ImageResult()
    data class Success(val data: Bitmap) : ImageResult()
}

data class ScreenshotUiState(
    val imageResult: ImageResult = ImageResult.Initial,
    val bitmapState: Bitmap? = null,
    val dialogState: Boolean = false,
    val captureTriggerState: Boolean = false
)

sealed class ScreenshotEvent {
    data class OnBitmapStateChanged(val bitmap: Bitmap?) : ScreenshotEvent()
    data class OnDialogStateChanged(val dialogState: Boolean) : ScreenshotEvent()
    data class OnCaptureTriggerStateChanged(val captureTriggerState: Boolean) : ScreenshotEvent()
}