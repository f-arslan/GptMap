package com.espressodev.gptmap.feature.screenshot

import android.app.Activity
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import androidx.compose.ui.geometry.Rect
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed class ScreenshotError(message: String) : Exception(message) {
    class DestinationInvalidError : ScreenshotError("The destination isn't a valid copy target.")
    class SourceInvalidError : ScreenshotError("It is not possible to copy from the source.")
    class TimeoutError :
        ScreenshotError("A timeout occurred while trying to acquire a buffer from the source.")

    class SourceNoDataError : ScreenshotError("The source has nothing to copy from.")
    class UnknownError : ScreenshotError("The pixel copy request failed with an unknown error.")
}

suspend fun View.screenshot(bounds: Rect): ImageResult =
    suspendCancellableCoroutine { continuation ->
        try {
            val bitmap = Bitmap.createBitmap(
                bounds.width.toInt(),
                bounds.height.toInt(),
                Bitmap.Config.ARGB_8888
            )

            // if it's a fragment, it can throw an error. We are using it in compose, so it's an activity
            PixelCopy.request(
                (this.context as Activity).window,
                android.graphics.Rect(
                    bounds.left.toInt(),
                    bounds.top.toInt(),
                    bounds.right.toInt(),
                    bounds.bottom.toInt()
                ),
                bitmap,
                { result ->
                    when (result) {
                        PixelCopy.SUCCESS -> continuation.resume(ImageResult.Success(bitmap))
                        PixelCopy.ERROR_DESTINATION_INVALID -> continuation.resumeWithException(
                            ScreenshotError.DestinationInvalidError()
                        )

                        PixelCopy.ERROR_SOURCE_INVALID -> continuation.resumeWithException(
                            ScreenshotError.SourceInvalidError()
                        )

                        PixelCopy.ERROR_TIMEOUT -> continuation.resumeWithException(ScreenshotError.TimeoutError())
                        PixelCopy.ERROR_SOURCE_NO_DATA -> continuation.resumeWithException(
                            ScreenshotError.SourceNoDataError()
                        )

                        else -> continuation.resumeWithException(ScreenshotError.UnknownError())
                    }
                },
                Handler(Looper.getMainLooper())
            )
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }
