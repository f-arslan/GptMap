package com.espressodev.gptmap.core.domain

import android.util.Log
import com.espressodev.gptmap.core.data.StorageService
import com.espressodev.gptmap.core.data.StorageService.Companion.IMAGE_REFERENCE
import com.espressodev.gptmap.core.ext.runCatchingWithContext
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SaveImageToFirebaseStorageUseCase @Inject constructor(
    private val storageService: StorageService,
    private val realmSyncService: RealmSyncService,
    private val ioDispatcher: CoroutineDispatcher,
    private val downloadAndCompressImageUseCase: DownloadAndCompressImageUseCase
) {
    suspend operator fun invoke(location: Location) = runCatchingWithContext(ioDispatcher) {
        require(location.locationImages.isNotEmpty()) { "No images found for location" }
        val imageData =
            downloadAndCompressImageUseCase(location.locationImages[0].imageUrl).getOrThrow()

        val imageUrl =
            storageService.uploadImage(imageData, location.id, IMAGE_REFERENCE).getOrThrow()
        saveImageUrlToRealm(imageUrl, location)
    }

    private suspend fun saveImageUrlToRealm(imageUrl: String, location: Location) {
        Log.d("SaveImageToFirebaseStorageUseCase", "saveImageUrlToRealm: $location")
        val realmLocation = location.toRealmFavourite().apply {
            placeholderImageUrl = imageUrl
        }
        realmSyncService.saveFavourite(realmLocation).getOrThrow()
    }
}
