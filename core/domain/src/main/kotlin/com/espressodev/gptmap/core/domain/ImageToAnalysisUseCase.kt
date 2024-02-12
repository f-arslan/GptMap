package com.espressodev.gptmap.core.domain

import android.content.Context
import com.espressodev.gptmap.core.common.DataStoreService
import com.espressodev.gptmap.core.ext.runCatchingWithContext
import com.espressodev.gptmap.core.model.Constants.DOWNLOAD_IMAGE_HEIGHT_FOR_ANALYSIS
import com.espressodev.gptmap.core.model.Constants.DOWNLOAD_IMAGE_WIDTH_FOR_ANALYSIS
import com.espressodev.gptmap.core.model.ImageType
import com.espressodev.gptmap.core.model.ext.saveToInternalStorageIfNotExist
import com.espressodev.gptmap.core.model.ext.toBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImageToAnalysisUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val downloadAndCompressImageUseCase: DownloadAndCompressImageUseCase,
    private val saveImageAnalysisToStorageUseCase: SaveImageAnalysisToStorageUseCase,
    private val dataStoreService: DataStoreService,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(imageUrl: String) =
        runCatchingWithContext(ioDispatcher) {
            val bitmap =
                downloadAndCompressImageUseCase(
                    imageUrl = imageUrl,
                    width = DOWNLOAD_IMAGE_WIDTH_FOR_ANALYSIS,
                    height = DOWNLOAD_IMAGE_HEIGHT_FOR_ANALYSIS
                ).getOrThrow().toBitmap().also {
                    it.height.also(::println)
                }

            val imageAnalysisId = saveImageAnalysisToStorageUseCase(
                bitmap = bitmap,
                title = "",
                imageWidth = DOWNLOAD_IMAGE_WIDTH_FOR_ANALYSIS,
                imageHeight = DOWNLOAD_IMAGE_HEIGHT_FOR_ANALYSIS,
                imageType = ImageType.Favourite
            ).getOrThrow()

            launch {
                dataStoreService.setLatestImageIdForChat(imageAnalysisId)
            }
            launch {
                dataStoreService.setImageUrl(imageUrl)
            }
            launch {
                bitmap.saveToInternalStorageIfNotExist(context, imageAnalysisId)
            }
            imageAnalysisId
        }
}
