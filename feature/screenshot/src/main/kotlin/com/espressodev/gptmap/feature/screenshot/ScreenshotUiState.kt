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
    val callback: (() -> Unit)? = null
) 