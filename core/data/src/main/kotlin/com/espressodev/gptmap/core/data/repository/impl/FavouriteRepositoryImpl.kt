package com.espressodev.gptmap.core.data.repository.impl

import com.espressodev.gptmap.core.data.di.Dispatcher
import com.espressodev.gptmap.core.data.di.GmDispatchers.IO
import com.espressodev.gptmap.core.data.repository.FavouriteRepository
import com.espressodev.gptmap.core.data.util.runCatchingWithContext
import com.espressodev.gptmap.core.firebase.StorageDataStore
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.ext.downloadResizeAndCompress
import com.espressodev.gptmap.core.mongodb.FavouriteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FavouriteRepositoryImpl @Inject constructor(
    private val storageDataStore: StorageDataStore,
    private val favouriteDataSource: FavouriteDataSource,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) : FavouriteRepository {
    override suspend fun saveImageForLocation(location: Location) =
        runCatchingWithContext(ioDispatcher) {
            require(location.locationImages.isNotEmpty()) { "No images found for location" }
            val imageData =
                location.locationImages[0].imageUrl.downloadResizeAndCompress()

            val imageUrl =
                storageDataStore.uploadImage(
                    imageData,
                    location.id,
                    StorageDataStore.IMAGE_REFERENCE
                ).getOrThrow()

            saveImageUrlToRealm(imageUrl, location)
        }

    private suspend fun saveImageUrlToRealm(imageUrl: String, location: Location) {
        val realmLocation = location.toRealmFavourite().apply {
            placeholderImageUrl = imageUrl
        }
        favouriteDataSource.saveFavourite(realmLocation).getOrThrow()
    }
}
