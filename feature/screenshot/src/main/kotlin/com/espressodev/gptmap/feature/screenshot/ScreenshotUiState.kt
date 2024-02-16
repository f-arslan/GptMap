package com.espressodev.gptmap.feature.screenshot

import android.graphics.Bitmap

enum class ScreenState {
    Initial, AfterSelectingTheField
}

data class ScreenshotUiState(
    val isCameraButtonVisible: Boolean = true,
    val bitmap: Bitmap? = null,
    val screenState: ScreenState = ScreenState.Initial,
    val imageResult: ImageResult = ImageResult.Initial,
    val callback: (() -> Unit)? = null,
    val title: String = "",
    val isSaveStateStarted: Boolean = false
)

sealed class ScreenshotUiEvent {
    data class OnBitmapChanged(val bitmap: Bitmap?) : ScreenshotUiEvent()
    data class OnCallbackChanged(val callback: (() -> Unit)?) : ScreenshotUiEvent()
    data class OnImageResultChanged(val imageResult: ImageResult) : ScreenshotUiEvent()
    data class OnTitleChanged(val value: String) : ScreenshotUiEvent()
    data object OnCaptureClicked : ScreenshotUiEvent()
    data object OnSaveClicked : ScreenshotUiEvent()
}

sealed class ImageResult {
    data object Initial : ImageResult()
    data class Success(val data: Bitmap) : ImageResult()
}
