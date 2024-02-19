package com.espressodev.gptmap.api.unsplash

import com.espressodev.gptmap.core.model.unsplash.LocationImage

interface UnsplashDataSource {
    suspend fun getTwoPhotos(query: String): Result<List<LocationImage>>
}
