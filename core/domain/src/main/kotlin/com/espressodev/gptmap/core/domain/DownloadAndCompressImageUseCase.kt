package com.espressodev.gptmap.core.domain

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import com.espressodev.gptmap.core.model.ext.compressImage
import com.espressodev.gptmap.core.model.ext.resizeImage
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
            bitmap.resizeImage(320, 180).compressImage()
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
}
