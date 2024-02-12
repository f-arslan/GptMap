package com.espressodev.gptmap.core.domain

import android.content.Context
import com.espressodev.gptmap.core.common.DataStoreService
import com.espressodev.gptmap.core.ext.runCatchingWithContext
import com.espressodev.gptmap.core.model.ext.saveToInternalStorageIfNotExist
import com.espressodev.gptmap.core.model.ext.toBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class SaveImageToInternalStorageUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    private val downloadAndCompressImageUseCase: DownloadAndCompressImageUseCase,
    private val dataStoreService: DataStoreService
) {
    suspend operator fun invoke(imageUrl: String, fileId: String, size: Int) =
        runCatchingWithContext(ioDispatcher) {
            launch {
                downloadAndCompressImageUseCase(imageUrl = imageUrl, width = size, height = size)
                    .getOrThrow()
                    .toBitmap()
                    .saveToInternalStorageIfNotExist(context, fileId)
            }
            launch {
                dataStoreService.setImageUrl(imageUrl)
            }
            Unit
        }
}
