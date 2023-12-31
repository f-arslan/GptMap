package com.espressodev.gptmap.core.screen_capture.composable

import android.graphics.Bitmap

sealed class ImageResult {
    data object Initial : ImageResult()
    data class Error(val exception: Exception) : ImageResult()
    data class Success(val data: Bitmap) : ImageResult()
}