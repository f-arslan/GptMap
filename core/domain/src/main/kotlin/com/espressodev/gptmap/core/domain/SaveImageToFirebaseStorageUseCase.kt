package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.data.StorageService
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
    suspend operator fun invoke(location: Location) =
        withContext(Dispatchers.IO) {
            try {
                downloadAndCompressImageUseCase(location.locationImages[0].imageUrl)
                    .onSuccess { imageData ->
                        storageService.uploadImage(imageData, location.id)
                        saveImageUrlToRealm(location.locationImages[0].imageUrl, location)
                    }.onFailure {
                        throw it
                    }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private suspend fun saveImageUrlToRealm(imageUrl: String, location: Location) {
        val realmLocation = location.toRealmLocation().apply {
            placeholderImageUrl = imageUrl
        }
        realmSyncService.saveLocation(realmLocation).onFailure { throw it }
    }
}