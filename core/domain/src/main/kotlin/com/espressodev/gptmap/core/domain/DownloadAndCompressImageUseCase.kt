package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.ext.runCatchingWithContext
import com.espressodev.gptmap.core.model.Constants.DOWNLOAD_IMAGE_HEIGHT
import com.espressodev.gptmap.core.model.Constants.DOWNLOAD_IMAGE_WIDTH
import com.espressodev.gptmap.core.model.ext.compressImage
import com.espressodev.gptmap.core.model.ext.downloadImage
import com.espressodev.gptmap.core.model.ext.resizeImage
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DownloadAndCompressImageUseCase @Inject constructor(private val ioDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(
        imageUrl: String,
        width: Int = DOWNLOAD_IMAGE_WIDTH,
        height: Int = DOWNLOAD_IMAGE_HEIGHT
    ) = runCatchingWithContext(ioDispatcher) {
        imageUrl.downloadImage().resizeImage(width, height).compressImage()
    }
}
