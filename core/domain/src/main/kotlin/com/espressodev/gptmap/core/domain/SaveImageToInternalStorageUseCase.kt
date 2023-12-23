package com.espressodev.gptmap.core.domain

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveImageToInternalStorageUseCase @Inject constructor(
    private val context: Context,
    private val downloadAndCompressImageUseCase: DownloadAndCompressImageUseCase
) {
    suspend operator fun invoke(imageUrl: String, fileId: String) = withContext(Dispatchers.IO) {
        try {
            downloadAndCompressImageUseCase(imageUrl).onSuccess {
                saveToInternalStorage(it, fileId)
            }.onFailure {
                throw it
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    private fun saveToInternalStorage(imageData: ByteArray, filename: String) {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { fos ->
            fos.write(imageData)
        }
    }
}