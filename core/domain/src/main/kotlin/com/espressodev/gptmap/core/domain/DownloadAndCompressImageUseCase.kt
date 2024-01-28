package com.espressodev.gptmap.core.domain

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.espressodev.gptmap.core.model.ext.compressImage
import com.espressodev.gptmap.core.model.ext.resizeImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class DownloadAndCompressImageUseCase @Inject constructor(private val ioDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(imageUrl: String) = withContext(ioDispatcher) {
        runCatching {
            val bitmap = downloadImage(imageUrl)
            bitmap.resizeImage(320, 180).compressImage()
        }
    }

    private fun downloadImage(imageUrl: String): Bitmap {
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        return connection.inputStream.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    }
}
