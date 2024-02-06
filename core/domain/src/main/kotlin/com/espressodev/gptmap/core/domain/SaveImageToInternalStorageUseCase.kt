package com.espressodev.gptmap.core.domain

import android.content.Context
import android.graphics.Bitmap
import com.espressodev.gptmap.core.model.Exceptions.FailedToCreateDirectoryException
import com.espressodev.gptmap.core.model.Exceptions.FailedToGetDirectoryException
import com.espressodev.gptmap.core.model.ext.toBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class SaveImageToInternalStorageUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    private val downloadAndCompressImageUseCase: DownloadAndCompressImageUseCase
) {
    suspend operator fun invoke(imageUrl: String, fileId: String) = runCatching {
        withContext(ioDispatcher) {
            val imageData = downloadAndCompressImageUseCase(imageUrl).getOrThrow().toBitmap()
            saveToInternalStorageIfNotExist(imageData, fileId)
        }
    }

    private fun saveToInternalStorageIfNotExist(bitmap: Bitmap, filename: String) {
        context.getExternalFilesDir(null)?.let { dir ->
            val imagesDirectory = File(dir, "images")
            if (!imagesDirectory.exists() && !imagesDirectory.mkdirs()) {
                throw FailedToCreateDirectoryException()
            }
            val file = File(imagesDirectory, "$filename.jpg")
            if (file.exists()) {
                return
            }
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }
        } ?: throw FailedToGetDirectoryException()
    }
}
