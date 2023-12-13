package com.espressodev.gptmap.core.model

import com.espressodev.gptmap.core.model.chatgpt.Content

// Added due to showing placeholder image while loading
val emptyImagePlaceholder = List(2) { LocationImage("", "") }

data class Location(
    val id: String = "default",
    val content: Content = Content(),
    val locationImages: List<LocationImage> = emptyImagePlaceholder
)
