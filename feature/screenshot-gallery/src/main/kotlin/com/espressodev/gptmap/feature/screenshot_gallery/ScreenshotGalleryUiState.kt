package com.espressodev.gptmap.feature.screenshot_gallery

import com.espressodev.gptmap.core.model.ImageSummary
import java.time.LocalDateTime

data class ScreenshotGalleryUiState(
    val selectedImageSummary: ImageSummary = ImageSummary(date = LocalDateTime.now()),
    val uiIsInEditMode: Boolean = false,
)