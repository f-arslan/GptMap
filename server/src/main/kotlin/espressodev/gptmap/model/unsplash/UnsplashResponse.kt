package espressodev.gptmap.model.unsplash

import espressodev.gptmap.model.LocationImage


data class UnsplashResponse(
    val results: List<Result>,
    val total: Int,
    val total_pages: Int
) {
    fun toLocationImageList(): List<LocationImage> =
        results.map {
            LocationImage(imageUrl = it.urls.regular, imageAuthor = it.user.name)
        }
}