package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.ImageMessage
import com.espressodev.gptmap.core.model.realm.RealmImageMessage
import kotlinx.coroutines.flow.Flow

interface ImageMessageService {
    fun getImageAnalysisMessages(imageAnalysisId: String): Flow<List<ImageMessage>>
    suspend fun addImageMessageToImageAnalysis(
        imageAnalysisId: String,
        message: RealmImageMessage
    ): Result<Unit>

    suspend fun updateImageMessageInImageAnalysis(
        imageAnalysisId: String,
        messageId: String,
        text: String
    ): Result<Unit>

    fun getImageType(imageAnalysisId: String): String
}
