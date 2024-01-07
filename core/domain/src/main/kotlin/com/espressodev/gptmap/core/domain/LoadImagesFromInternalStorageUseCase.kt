package com.espressodev.gptmap.core.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.espressodev.gptmap.core.model.ext.classTag
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileNotFoundException
import java.io.IOException
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
