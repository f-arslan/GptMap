package com.espressodev.gptmap.core.unsplash_api

import com.espressodev.gptmap.core.model.LocationImage

interface UnsplashService {
        suspend fun getTwoPhotos(query: String): Result<List<LocationImage>>
}