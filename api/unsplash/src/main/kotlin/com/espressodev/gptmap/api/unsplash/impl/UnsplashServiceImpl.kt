package com.espressodev.gptmap.api.unsplash.impl

import android.util.Log
import com.espressodev.gptmap.api.unsplash.UnsplashApi
import com.espressodev.gptmap.api.unsplash.UnsplashService
import com.espressodev.gptmap.core.model.unsplash.LocationImage
import com.espressodev.gptmap.core.model.ext.classTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UnsplashServiceImpl(private val unsplashApi: UnsplashApi) : UnsplashService {
    override suspend fun getTwoPhotos(query: String): Result<List<LocationImage>> =
        withContext(Dispatchers.IO) {
            val response = unsplashApi.getTwoPhotos(query)
            response.isSuccessful.let { success ->
                when {
                    success -> {
                        response.body()?.let {
                            Result.success(it)
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