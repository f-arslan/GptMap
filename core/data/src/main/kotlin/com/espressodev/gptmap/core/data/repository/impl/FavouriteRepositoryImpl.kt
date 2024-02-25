package com.espressodev.gptmap.core.data.repository.impl

import com.espressodev.gptmap.core.data.repository.FavouriteRepository
import com.espressodev.gptmap.core.data.util.runCatchingWithContext
import com.espressodev.gptmap.core.firebase.StorageRepository
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.di.Dispatcher
import com.espressodev.gptmap.core.model.di.GmDispatchers.IO
import com.espressodev.gptmap.core.model.ext.downloadResizeAndCompress
import com.espressodev.gptmap.core.mongodb.FavouriteRealmRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
class FavouriteRepositoryImpl @Inject constructor(
    private val storageRepository: StorageRepository,
    private val favouriteRealmRepository: FavouriteRealmRepository,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) : FavouriteRepository {
    override suspend fun saveImageForLocation(location: Location) =
        runCatchingWithContext(ioDispatcher) {
            require(location.locationImages.isNotEmpty()) { "No images found for location" }
            val imageData =
                location.locationImages[0].imageUrl.downloadResizeAndCompress()

            val imageUrl =
                storageRepository.uploadImage(
                    imageData,
                    location.id,
                    StorageRepository.IMAGE_REFERENCE
                ).getOrThrow()

            saveImageUrlToRealm(imageUrl, location)
        }

    private suspend fun saveImageUrlToRealm(imageUrl: String, location: Location) {
        val realmLocation = location.toRealmFavourite().apply {
            placeholderImageUrl = imageUrl
        }
        favouriteRealmRepository.saveFavourite(realmLocation).getOrThrow()
    }
}
