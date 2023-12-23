package com.espressodev.gptmap.core.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class SaveImageToInternalStorageUseCase @Inject constructor(private val context: Context) {
    suspend fun invoke(imageUrl: String, fileId: String) = withContext(Dispatchers.IO) {
        val bitmap = downloadImage(imageUrl)
        val resizedBitmap = resizeImage(bitmap)
        val compressedBitmap = compressImage(resizedBitmap)
        saveToInternalStorage(compressedBitmap, fileId)
    }

    private fun downloadImage(imageUrl: String): Bitmap =
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.inputStream.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            throw e
        }

    private fun resizeImage(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, 160, 90, true)
    }

    private fun compressImage(bitmap: Bitmap): Bitmap = ByteArrayOutputStream().use { stream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        val byteArray = stream.toByteArray()
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap, filename: String) {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { fos ->
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }
    }
}