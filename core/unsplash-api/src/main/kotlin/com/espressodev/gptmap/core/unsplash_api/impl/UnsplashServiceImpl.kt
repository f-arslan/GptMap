package com.espressodev.gptmap.core.unsplash_api.impl

import android.util.Log
import com.espressodev.gptmap.core.model.ext.classTag
import com.espressodev.gptmap.core.model.unsplash.UnsplashResponse
import com.espressodev.gptmap.core.unsplash_api.UnsplashApi
import com.espressodev.gptmap.core.unsplash_api.UnsplashService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UnsplashServiceImpl(private val unsplashApi: UnsplashApi) : UnsplashService {
    override suspend fun getTwoPhotos(query: String): Result<UnsplashResponse> =
        withContext(Dispatchers.IO) {
            unsplashApi.getTwoPhotos(query).isSuccessful.let { success ->
                if (success) {
                    Log.d(classTag(), "getTwoPhotos: ${unsplashApi.getTwoPhotos(query).body()}")
                    Result.success(unsplashApi.getTwoPhotos(query).body()!!)
                } else {
                    Log.d(classTag(), "getTwoPhotos: failure")
                    Result.failure(Throwable("getTwoPhotos: failure"))
                }
            }
        }
}