package com.espressodev.gptmap.core.domain

import android.graphics.Bitmap
import com.espressodev.gptmap.core.data.StorageService
import com.espressodev.gptmap.core.data.StorageService.Companion.ANALYSIS_IMAGE_REFERENCE
import com.espressodev.gptmap.core.ext.runCatchingWithContext
import com.espressodev.gptmap.core.model.Constants.STORAGE_IMAGE_HEIGHT
import com.espressodev.gptmap.core.model.Constants.STORAGE_IMAGE_WIDTH
import com.espressodev.gptmap.core.model.ImageType
import com.espressodev.gptmap.core.model.ext.compressImage
import com.espressodev.gptmap.core.model.ext.resizeImage
import com.espressodev.gptmap.core.model.realm.RealmImageAnalysis
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import kotlinx.coroutines.CoroutineDispatcher
import java.util.UUID
import javax.inject.Inject

class SaveImageAnalysisToStorageUseCase @Inject constructor(
    private val storageService: StorageService,
    private val realmSyncService: RealmSyncService,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        bitmap: Bitmap,
        title: String,
        imageWidth: Int = STORAGE_IMAGE_WIDTH,
        imageHeight: Int = STORAGE_IMAGE_HEIGHT,
        imageType: ImageType = ImageType.Screenshot
    ) =
        runCatchingWithContext(ioDispatcher) {
            val byteArray =
                bitmap.resizeImage(imageWidth, imageHeight).compressImage()
            val imageId = UUID.randomUUID().toString()
            val imageUrl = storageService.uploadImage(
                byteArray,
                imageId,
                ANALYSIS_IMAGE_REFERENCE
            ).getOrThrow()
            saveImageAnalysisToRealm(imageId, imageUrl, title, imageType)
            imageId
        }


    private suspend fun saveImageAnalysisToRealm(
        imageId: String,
        imageUrl: String,
        title: String,
        imageType: ImageType
    ) {
        val realmImageAnalysis = RealmImageAnalysis().apply {
            this.imageId = imageId
            this.imageUrl = imageUrl
            this.title = title
            this.imageType = imageType.name
        }
        realmSyncService.saveImageAnalysis(realmImageAnalysis).getOrThrow()
    }
}
