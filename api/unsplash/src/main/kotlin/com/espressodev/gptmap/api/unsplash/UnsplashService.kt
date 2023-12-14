package com.espressodev.gptmap.api.unsplash

import com.espressodev.gptmap.core.model.LocationImage

interface UnsplashService {
        suspend fun getTwoPhotos(query: String): Result<List<LocationImage>>
}