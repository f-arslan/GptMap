package com.espressodev.gptmap.core.domain

import com.espressodev.gptmap.core.data.StorageService
import com.espressodev.gptmap.core.data.StorageService.Companion.IMAGE_REFERENCE
import com.espressodev.gptmap.core.ext.runCatchingWithContext
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.mongodb.FavouriteService
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SaveImageToFirebaseStorageUseCase @Inject constructor(
    private val storageService: StorageService,
    private val favouriteService: FavouriteService,
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
        val realmLocation = location.toRealmFavourite().apply {
            placeholderImageUrl = imageUrl
        }
        favouriteService.saveFavourite(realmLocation).getOrThrow()
    }
}
