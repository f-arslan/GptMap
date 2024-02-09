package com.espressodev.gptmap.core.domain

import android.graphics.Bitmap
import com.espressodev.gptmap.core.model.Constants.STORAGE_IMAGE_HEIGHT
import com.espressodev.gptmap.core.model.Constants.STORAGE_IMAGE_WIDTH
import com.espressodev.gptmap.core.data.StorageService
import com.espressodev.gptmap.core.data.StorageService.Companion.ANALYSIS_IMAGE_REFERENCE
import com.espressodev.gptmap.core.model.ext.compressImage
import com.espressodev.gptmap.core.model.ext.resizeImage
import com.espressodev.gptmap.core.model.realm.RealmImageAnalysis
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class SaveImageAnalysisToStorageUseCase @Inject constructor(
    private val storageService: StorageService,
    private val realmSyncService: RealmSyncService,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(bitmap: Bitmap, title: String) = withContext(ioDispatcher) {
        runCatching {
            val byteArray =
                bitmap.resizeImage(STORAGE_IMAGE_WIDTH, STORAGE_IMAGE_HEIGHT).compressImage()
            val imageId = UUID.randomUUID().toString()
            val imageUrl = storageService.uploadImage(
                byteArray,
                imageId,
                ANALYSIS_IMAGE_REFERENCE
            ).getOrThrow()
            saveImageAnalysisToRealm(imageId, imageUrl, title)
            imageId
        }
    }

    private suspend fun saveImageAnalysisToRealm(imageId: String, imageUrl: String, title: String) {
        val realmImageAnalysis = RealmImageAnalysis().apply {
            this.imageId = imageId
            this.imageUrl = imageUrl
            this.title = title
        }
        realmSyncService.saveImageAnalysis(realmImageAnalysis).getOrThrow()
    }
}
