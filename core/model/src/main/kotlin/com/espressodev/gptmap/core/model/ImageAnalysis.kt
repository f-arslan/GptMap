package com.espressodev.gptmap.core.model

import java.time.LocalDateTime

data class ImageAnalysis(
    val id: String = "",
    val imageId: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val title: String = "",
    val messages: List<ImageMessage> = listOf(),
    val date: LocalDateTime = LocalDateTime.MIN
) {
    fun toImageAnalysisSummary() =
        ImageSummary(
            id = imageId,
            imageUrl = imageUrl,
            title = title,
            date = date
        )
}

fun ImageAnalysis.sortByDate() = copy(messages = messages.sortedByDescending { it.date })

data class ImageMessage(
    val id: String = "",
    val request: String = "",
    val response: String = "",
    val date: LocalDateTime = LocalDateTime.MIN
)
