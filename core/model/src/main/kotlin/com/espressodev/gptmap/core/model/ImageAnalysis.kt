package com.espressodev.gptmap.core.model

import java.time.LocalDateTime

data class ImageAnalysis(
    val id: String,
    val imageId: String,
    val userId: String,
    val imageUrl: String,
    val title: String,
    val messages: List<ImageMessage>,
    val date: LocalDateTime
)

data class ImageMessage(
    val request: String,
    val response: String,
    val date: LocalDateTime
)
