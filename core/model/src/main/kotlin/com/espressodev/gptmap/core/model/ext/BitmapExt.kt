package com.espressodev.gptmap.core.model.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

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

fun String.downloadImage(): Bitmap {
    val url = URL(this)
    val connection = url.openConnection() as HttpURLConnection
    connection.connectTimeout = 10000
    connection.readTimeout = 10000
    return connection.inputStream.use { inputStream ->
        BitmapFactory.decodeStream(inputStream)
    }
}

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
