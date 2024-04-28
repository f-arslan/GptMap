package com.espressodev.gptmap.core.unsplash

import com.espressodev.gptmap.core.model.unsplash.LocationImage

interface UnsplashRepository {
    suspend fun getTwoPhotos(query: String): Result<List<LocationImage>>
}
