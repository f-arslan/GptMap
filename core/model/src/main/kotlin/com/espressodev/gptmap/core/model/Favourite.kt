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
)