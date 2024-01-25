package com.espressodev.gptmap.core.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LoadImagesFromInternalStorageUseCase @Inject constructor(@ApplicationContext private val context: Context) {
    operator fun invoke(imageIds: List<String>): List<Bitmap> {
        return imageIds.mapNotNull { imageId ->
            loadImageFromInternalStorage(imageId).getOrNull()
        }
    }

    private fun loadImageFromInternalStorage(imageId: String): Result<Bitmap> {
        val fileName = context.fileList().find { it.startsWith(imageId) }
        return runCatching {
            context.openFileInput(fileName).use { fis ->
                BitmapFactory.decodeStream(fis)
            }
        }
    }
}
