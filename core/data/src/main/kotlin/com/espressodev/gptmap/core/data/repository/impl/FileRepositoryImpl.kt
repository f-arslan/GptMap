package com.espressodev.gptmap.core.data.repository.impl

import android.content.Context
import android.util.Log
import com.espressodev.gptmap.core.data.repository.FileRepository
import com.espressodev.gptmap.core.data.util.runCatchingWithContext
import com.espressodev.gptmap.core.datastore.DataStoreService
import com.espressodev.gptmap.core.model.di.Dispatcher
import com.espressodev.gptmap.core.model.di.GmDispatchers.IO
import com.espressodev.gptmap.core.model.ext.downloadResizeAndCompress
import com.espressodev.gptmap.core.model.ext.saveToInternalStorageIfNotExist
import com.espressodev.gptmap.core.model.ext.toBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
class FileRepositoryImpl @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val dataStoreService: DataStoreService
) : FileRepository {
    override suspend fun deleteFilesFromInternal(
        filenames: List<String>,
        dir: String
    ): Result<Unit> = runCatchingWithContext(ioDispatcher) {
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

    override suspend fun saveImageToInternal(
        imageUrl: String,
        fileId: String,
        size: Int
    ): Result<Unit> = runCatchingWithContext(ioDispatcher) {
        launch {
            imageUrl.downloadResizeAndCompress(width = size, height = size)
                .toBitmap()
                .saveToInternalStorageIfNotExist(context, fileId)
        }
        launch {
            dataStoreService.setLatestImageUrl(imageUrl)
        }
        Unit
    }
}
