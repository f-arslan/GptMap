package com.espressodev.gptmap.core.model

import androidx.compose.runtime.Immutable
import java.time.LocalDateTime

@Immutable
data class ImageSummary(
    val id: String = "",
    val imageUrl: String = "",
    val title: String = "",
    val date: LocalDateTime
)