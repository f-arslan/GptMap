package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.realm.RealmImageAnalysis
import kotlinx.coroutines.flow.Flow

interface ImageAnalysisDataSource {
    suspend fun saveImageAnalysis(realmImageAnalysis: RealmImageAnalysis): Result<Unit>
    fun getImageAnalyses(): Flow<List<ImageAnalysis>>
    fun getImageAnalysis(id: String): Result<ImageAnalysis>
    suspend fun deleteImageAnalysis(imageAnalysisId: String): Result<Unit>
    suspend fun deleteImageAnalyses(imageAnalysesIds: Set<String>): Result<Unit>
    suspend fun updateImageAnalysisText(imageAnalysisId: String, text: String): Result<Unit>
}
