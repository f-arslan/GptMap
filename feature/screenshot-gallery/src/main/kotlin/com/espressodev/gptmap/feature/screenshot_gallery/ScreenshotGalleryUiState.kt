package com.espressodev.gptmap.feature.screenshot_gallery

import com.espressodev.gptmap.core.model.ImageSummary
import java.time.LocalDateTime

data class ScreenshotGalleryUiState(
    val selectedImageSummary: ImageSummary = ImageSummary(date = LocalDateTime.now()),
    val uiIsInEditMode: Boolean = false,
    val editDialogState: Boolean = false,
)

sealed class ScreenshotGalleryUiEvent {
    data class OnLongClickToImage(val imageSummary: ImageSummary) : ScreenshotGalleryUiEvent()
    data object OnCancelClick : ScreenshotGalleryUiEvent()
    data object OnDeleteClick : ScreenshotGalleryUiEvent()
    data object OnEditClick : ScreenshotGalleryUiEvent()
    data object OnEditDialogDismiss : ScreenshotGalleryUiEvent()
    data object Reset: ScreenshotGalleryUiEvent()
    data class OnEditDialogConfirm(val text: String) : ScreenshotGalleryUiEvent()
}