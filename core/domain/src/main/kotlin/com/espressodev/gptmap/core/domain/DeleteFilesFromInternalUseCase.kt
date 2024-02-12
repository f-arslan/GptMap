package com.espressodev.gptmap.core.domain

import android.content.Context
import android.util.Log
import com.espressodev.gptmap.core.ext.runCatchingWithContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import java.io.File
import javax.inject.Inject

class DeleteFilesFromInternalUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(filenames: List<String>, dir: String) =
        runCatchingWithContext(ioDispatcher) {
            val externalFilesDir = context.getExternalFilesDir(null)
            val imagesDirectory = File(externalFilesDir, dir)

            if (!imagesDirectory.exists()) {
                return@runCatchingWithContext
            }

            filenames.forEach { filename ->
                val file = File(imagesDirectory, "$filename.jpg")
                if (file.exists() && !file.delete()) {
                    Log.e("FileDeletion", "Failed to delete file: $filename.jpg")
                }
            }
        }
}
