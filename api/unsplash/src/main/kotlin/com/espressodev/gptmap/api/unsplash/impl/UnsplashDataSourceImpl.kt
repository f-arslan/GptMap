package com.espressodev.gptmap.api.unsplash.impl

import com.espressodev.gptmap.api.unsplash.UnsplashApi
import com.espressodev.gptmap.api.unsplash.UnsplashService
import com.espressodev.gptmap.core.model.unsplash.LocationImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Even the data class fields are not null response can make your data class fields null
 * how is this allowed?
 */

class UnsplashServiceImpl(private val unsplashApi: UnsplashApi) : UnsplashService {
    override suspend fun getTwoPhotos(query: String): Result<List<LocationImage>> =
        withContext(Dispatchers.IO) {
            val response = unsplashApi.getTwoPhotos(query)
            response.isSuccessful.let { success ->
                when {
                    success -> {
                        response.body()?.let { images ->
                            val notNullImages = images.map {
                                it.copy(
                                    id = UUID.randomUUID().toString(),
                                    analysisId = ""
                                )
                            }
                            Result.success(notNullImages)
                        } ?: Result.failure(Throwable(UnsplashApiException()))
                    }

                    else -> {
                        Result.failure(Throwable(UnsplashApiException()))
                    }
                }
            }
        }

    companion object {
        class UnsplashApiException : Exception()
    }
}
