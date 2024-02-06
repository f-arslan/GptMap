package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.model.ext.compressImage
import com.espressodev.gptmap.core.model.ext.downloadImage
import com.espressodev.gptmap.core.model.ext.resizeImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DownloadAndCompressImageUseCase @Inject constructor(private val ioDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(imageUrl: String, width: Int = 320, height: Int = 180) =
        withContext(ioDispatcher) {
            runCatching {
                imageUrl.downloadImage().resizeImage(width, height).compressImage()
            }
        }
}
