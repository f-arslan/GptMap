package com.espressodev.gptmap.core.screen_capture.composable

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

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