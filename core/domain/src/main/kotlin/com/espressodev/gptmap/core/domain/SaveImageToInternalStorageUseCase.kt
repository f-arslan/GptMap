package com.espressodev.gptmap.core.domain

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveImageToInternalStorageUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    private val downloadAndCompressImageUseCase: DownloadAndCompressImageUseCase
) {
    suspend operator fun invoke(imageUrl: String, fileId: String) = withContext(ioDispatcher) {
        val imageData = downloadAndCompressImageUseCase(imageUrl).getOrThrow()
        saveToInternalStorage(imageData, fileId)
    }

    private fun saveToInternalStorage(imageData: ByteArray, filename: String) {
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { fos ->
            fos.write(imageData)
        }
    }
}
