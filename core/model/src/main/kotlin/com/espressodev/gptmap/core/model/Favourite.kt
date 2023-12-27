package com.espressodev.gptmap.core.model

import com.espressodev.gptmap.core.model.unsplash.LocationImage
import java.time.LocalDateTime

data class Favourite(
    val id: String,
    val userId: String,
    val favouriteId: String,
    val title: String,
    val placeholderImageUrl: String,
    val locationImages: List<LocationImage>,
    val content: Content,
    val date: LocalDateTime
) {
    val placeholderTitle = "${content.city}, ${content.country}"
    val placeholderCoordinates = "${"%.2f".format(content.latitude)}°, ${"%.2f".format(content.longitude)}°"

    fun toLocation(): Location = Location(
        id = id,
        content = content.toChatgptContent(),
        locationImages = locationImages,
        addToFavouriteButtonState = false
    )
}