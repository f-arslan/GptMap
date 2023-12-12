package com.espressodev.gptmap.core.model.unsplash

import com.espressodev.gptmap.core.model.LocationImage

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