package espressodev.gptmap.api.unsplash

import espressodev.gptmap.model.LocationImage

interface UnsplashService {
    suspend fun getTwoPhotos(query: String): Result<List<LocationImage>>
}