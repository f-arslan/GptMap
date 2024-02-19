package com.espressodev.gptmap.core.data.repository

import android.graphics.Bitmap
import com.espressodev.gptmap.core.model.Constants.STORAGE_IMAGE_HEIGHT
import com.espressodev.gptmap.core.model.Constants.STORAGE_IMAGE_WIDTH
import com.espressodev.gptmap.core.model.ImageType

interface ImageAnalysisRepository {
    suspend fun deleteImageAnalyses(imageIds: Set<String>): Result<Unit>
    suspend fun turnImageToImageAnalysis(imageUrl: String): Result<String>
    suspend fun saveImageAnalysisToStorage(
        bitmap: Bitmap,
        title: String,
        imageWidth: Int = STORAGE_IMAGE_WIDTH,
        imageHeight: Int = STORAGE_IMAGE_HEIGHT,
        imageType: ImageType = ImageType.Screenshot
    ): Result<String>
}
