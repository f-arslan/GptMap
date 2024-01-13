package com.espressodev.gptmap.core.domain

import android.graphics.Bitmap
import com.espressodev.gptmap.core.data.StorageService
import com.espressodev.gptmap.core.data.impl.StorageServiceImpl.Companion.ANALYSIS_IMAGE_REFERENCE
import com.espressodev.gptmap.core.model.ext.compressImage
import com.espressodev.gptmap.core.model.ext.resizeImage
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
    suspend operator fun invoke(bitmap: Bitmap, title: String) = withContext(Dispatchers.IO) {
        runCatching {
            val byteArray = bitmap.resizeImage(320, 320).compressImage()
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
