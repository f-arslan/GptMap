package com.espressodev.gptmap.core.model

data class EditableItemUiState<out T>(
    val selectedItem: T,
    val uiIsInEditMode: Boolean = false,
    val editDialogState: Boolean = false,
    val deleteDialogState: Boolean = false,
)

sealed class EditableItemUiEvent {
    data class OnLongClickToItem<T>(val item: T) : EditableItemUiEvent()
    data object OnCancelClick : EditableItemUiEvent()
    data object OnDeleteClick : EditableItemUiEvent()
    data object OnEditClick : EditableItemUiEvent()
    data object Reset: EditableItemUiEvent()
    data object OnEditDialogDismiss : EditableItemUiEvent()
    data class OnEditDialogConfirm(val text: String) : EditableItemUiEvent()
    data object OnDeleteDialogConfirm : EditableItemUiEvent()
    data object OnDeleteDialogDismiss : EditableItemUiEvent()
}

typealias ScreenshotGalleryUiState = EditableItemUiState<ImageSummary>
typealias FavouriteUiState = EditableItemUiState<Favourite>