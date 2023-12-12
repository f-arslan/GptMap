package com.espressodev.gptmap.core.model

import com.espressodev.gptmap.core.model.chatgpt.Content

data class Location(
    val id: String,
    val content: Content,
    val locationImages: List<LocationImage> = emptyList()
)
