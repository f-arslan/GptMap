package com.espressodev.gptmap.core.domain

import android.graphics.Bitmap
import com.espressodev.gptmap.core.data.StorageService
import com.espressodev.gptmap.core.data.impl.StorageServiceImpl.Companion.ANALYSIS_IMAGE_REFERENCE
import com.espressodev.gptmap.core.model.realm.RealmImageAnalysis
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

class SaveImageAnalysisToStorageUseCase @Inject constructor(
    private val storageService: StorageService,
    private val realmSyncService: RealmSyncService
) {
    suspend operator fun invoke(bitmap: Bitmap) = withContext(Dispatchers.IO) {
        try {
            val byteArray = bitmapToByteArray(bitmap)
            val imageId = UUID.randomUUID().toString()
            val uploadImageResult = storageService.uploadImage(byteArray, imageId, ANALYSIS_IMAGE_REFERENCE).getOrThrow()

            saveImageAnalysisToRealm(imageId, uploadImageResult)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveImageAnalysisToRealm(imageId: String, imageUrl: String) {
        val realmImageAnalysis = RealmImageAnalysis().apply {
            this.imageId = imageId
            this.imageUrl = imageUrl
        }
        realmSyncService.saveImageAnalysis(realmImageAnalysis).onFailure { throw it }
    }


    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}