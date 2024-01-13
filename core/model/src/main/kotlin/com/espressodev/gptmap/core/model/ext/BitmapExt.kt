package com.espressodev.gptmap.core.model.ext

import android.graphics.Bitmap
import android.os.Build
import java.io.ByteArrayOutputStream

fun Bitmap.resizeImage(width: Int, height: Int): Bitmap {
    return Bitmap.createScaledBitmap(this, width, height, true)
}

fun Bitmap.compressImage(): ByteArray = ByteArrayOutputStream().use { stream ->
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        compress(Bitmap.CompressFormat.WEBP_LOSSY, 50, stream)
    } else {
        compress(Bitmap.CompressFormat.JPEG, 50, stream)
    }
    stream.toByteArray()
}