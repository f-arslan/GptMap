package com.espressodev.gptmap.core.model

data class ImageAnalysis(
    val id: String,
    val imageId: String,
    val userId: String,
    val imageUrl: String,
    val title: String,
    val messages: List<ImageMessage>
)

data class ImageMessage(
    val id: String,
    val message: String,
    val score: Double
)


