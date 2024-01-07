package com.espressodev.gptmap.core.domain

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class DownloadAndCompressImageUseCase {
    suspend operator fun invoke(imageUrl: String) = withContext(Dispatchers.IO) {
        runCatching {
            val bitmap = downloadImage(imageUrl)
            val resizedBitmap = resizeImage(bitmap)
            val compressedByteArray = compressImage(resizedBitmap)
            compressedByteArray
        }
    }

    private fun downloadImage(imageUrl: String): Bitmap {
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        return connection.inputStream.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    }

    private fun resizeImage(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, 320, 180, true)
    }

    private fun compressImage(bitmap: Bitmap): ByteArray = ByteArrayOutputStream().use { stream ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 50, stream)
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        }
        stream.toByteArray()
    }
}
