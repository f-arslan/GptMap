package com.espressodev.gptmap.core.unsplash_api

import com.espressodev.gptmap.core.model.unsplash.UnsplashResponse

interface UnsplashService {
        suspend fun getTwoPhotos(query: String): Result<UnsplashResponse>
}