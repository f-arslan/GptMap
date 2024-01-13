package com.espressodev.gptmap.core.model

import java.time.LocalDateTime

data class ImageAnalysisSummary(
    val id: String = "",
    val imageUrl: String = "",
    val title: String = "",
    val date: LocalDateTime
)