package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.data.StorageService
import com.espressodev.gptmap.core.data.impl.StorageServiceImpl.Companion.IMAGE_REFERENCE
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveImageToFirebaseStorageUseCase @Inject constructor(
    private val storageService: StorageService,
    private val realmSyncService: RealmSyncService,
    private val downloadAndCompressImageUseCase: DownloadAndCompressImageUseCase
) {
    suspend operator fun invoke(location: Location) = withContext(Dispatchers.IO) {
        require(location.locationImages.isNotEmpty()) { "No images found for location" }
        val imageData = downloadAndCompressImageUseCase(location.locationImages[0].imageUrl).getOrThrow()
        val imageUrl = storageService.uploadImage(imageData, location.id, IMAGE_REFERENCE).getOrThrow()
        saveImageUrlToRealm(imageUrl, location)
    }

    private suspend fun saveImageUrlToRealm(imageUrl: String, location: Location) {
        val realmLocation = location.toRealmLocation().apply {
            placeholderImageUrl = imageUrl
        }
        realmSyncService.saveFavourite(realmLocation).getOrThrow()
    }
}