package com.espressodev.gptmap.core.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import javax.inject.Inject

class LoadImagesFromInternalStorageUseCase @Inject constructor(private val context: Context) {
    operator fun invoke(imageIds: List<String>): List<Bitmap> {
        return imageIds.mapNotNull { imageId ->
            loadImageFromInternalStorage(imageId)
        }
    }

    private fun loadImageFromInternalStorage(imageId: String): Bitmap? {
        val fileName = context.fileList().find { it.startsWith(imageId) } ?: return null
        return try {
            context.openFileInput(fileName).use { fis ->
                BitmapFactory.decodeStream(fis)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}