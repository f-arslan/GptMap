package com.espressodev.gptmap.feature.screenshot

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


enum class TakingScreenshotProgress {
    Idle, OnProgress
}

enum class ScreenState {
    Initial, AfterTakingScreenshot, AfterSelectingTheField
}

data class ScreenCaptureUiState(
    val isCameraButtonVisible: Boolean = true,
    val takingScreenshotProgress: TakingScreenshotProgress = TakingScreenshotProgress.Idle,
    val bitmap: Bitmap? = null,
    val screenState: ScreenState = ScreenState.Initial
)


sealed class ImageResult {
    data object Initial : ImageResult()
    data class Error(val exception: Exception) : ImageResult()
    data class Success(val data: Bitmap) : ImageResult()
}


@Composable
fun rememberScreenshotState() = remember {
    ScreenshotState()
}

class ScreenshotState {
    val imageState = mutableStateOf<ImageResult>(ImageResult.Initial)

    val bitmapState = mutableStateOf<Bitmap?>(null)

    internal var callback: (() -> Unit)? = null

    fun capture() {
        callback?.invoke()
    }
}