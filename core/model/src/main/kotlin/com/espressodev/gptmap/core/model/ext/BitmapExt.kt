package com.espressodev.gptmap.core.model.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import com.espressodev.gptmap.core.model.Constants
import com.espressodev.gptmap.core.model.Constants.COMPRESS_RATE_BEFORE_STORAGE
import com.espressodev.gptmap.core.model.Constants.DOWNLOAD_IMAGE_HEIGHT
import com.espressodev.gptmap.core.model.Constants.DOWNLOAD_IMAGE_TIMEOUT
import com.espressodev.gptmap.core.model.Constants.DOWNLOAD_IMAGE_WIDTH
import com.espressodev.gptmap.core.model.Exceptions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

fun Bitmap.resizeImage(width: Int, height: Int): Bitmap {
    return Bitmap.createScaledBitmap(this, width, height, true)
}

fun Bitmap.compressImage(): ByteArray = ByteArrayOutputStream().use { stream ->
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        compress(Bitmap.CompressFormat.WEBP_LOSSY, COMPRESS_RATE_BEFORE_STORAGE, stream)
    } else {
        compress(Bitmap.CompressFormat.JPEG, COMPRESS_RATE_BEFORE_STORAGE, stream)
    }
    stream.toByteArray()
}

fun String.downloadImage(): Bitmap {
    val url = URL(this)
    val connection = url.openConnection() as HttpURLConnection
    connection.connectTimeout = DOWNLOAD_IMAGE_TIMEOUT
    connection.readTimeout = DOWNLOAD_IMAGE_TIMEOUT
    return connection.inputStream.use { inputStream ->
        BitmapFactory.decodeStream(inputStream)
    }
}

fun String.downloadResizeAndCompress(
    width: Int = DOWNLOAD_IMAGE_WIDTH,
    height: Int = DOWNLOAD_IMAGE_HEIGHT
): ByteArray = downloadImage().resizeImage(width, height).compressImage()

fun ByteArray.toBitmap(): Bitmap = BitmapFactory.decodeByteArray(this, 0, size)

fun Context.readBitmapFromExternalStorage(directoryName: String, filename: String): Bitmap? {
    val externalFilesDir = this.getExternalFilesDir(null)
    externalFilesDir?.let {
        val file = File(it, "$directoryName/$filename.jpg")
        if (file.exists()) {
            return BitmapFactory.decodeFile(file.absolutePath)
        }
    }
    return null
}

fun Bitmap.saveToInternalStorageIfNotExist(context: Context, filename: String) {
    context.getExternalFilesDir(null)?.let { dir ->
        val imagesDirectory = File(dir, Constants.PHONE_IMAGE_DIR)
        if (!imagesDirectory.exists() && !imagesDirectory.mkdirs()) {
            throw Exceptions.FailedToCreateDirectoryException()
        }
        val file = File(imagesDirectory, "$filename.jpg")
        if (file.exists()) {
            return
        }
        FileOutputStream(file).use { fos ->
            compress(Bitmap.CompressFormat.JPEG, 100, fos)
        }
    } ?: throw Exceptions.FailedToGetDirectoryException()
}
