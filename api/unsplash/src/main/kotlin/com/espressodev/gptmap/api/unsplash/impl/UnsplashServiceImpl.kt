package com.espressodev.gptmap.api.unsplash.impl

import android.util.Log
import com.espressodev.gptmap.api.unsplash.UnsplashApi
import com.espressodev.gptmap.api.unsplash.UnsplashService
import com.espressodev.gptmap.core.model.LocationImage
import com.espressodev.gptmap.core.model.ext.classTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UnsplashServiceImpl(private val unsplashApi: UnsplashApi) : UnsplashService {
    override suspend fun getTwoPhotos(query: String): Result<List<LocationImage>> =
        withContext(Dispatchers.IO) {
            unsplashApi.getTwoPhotos(query).isSuccessful.let { success ->
                when {
                    success -> {
                        Log.d(classTag(), unsplashApi.getTwoPhotos(query).body().toString())
                        unsplashApi.getTwoPhotos(query).body()?.let {
                            Result.success(it.toLocationImageList())
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