package com.espressodev.gptmap.core.model

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf

data class EditableItemUiState<out T>(
    val selectedItem: T,
    val isUiInEditMode: Boolean = false,
    val editDialogState: Boolean = false,
    val deleteDialogState: Boolean = false,
    val selectedItemsCount: Int = 0,
    val isSelectedItemAboveOne: Boolean = false,
    val topBarTitle: String = "",
    val isLoading: Boolean = false,
    val selectedItemsIds: PersistentSet<String> = persistentSetOf()
)

sealed class EditableItemUiEvent {
    data class OnLongClickToItem<T>(val item: T) : EditableItemUiEvent()
    data object OnCancelClick : EditableItemUiEvent()
    data object OnDeleteClick : EditableItemUiEvent()
    data object OnEditClick : EditableItemUiEvent()
    data object Reset : EditableItemUiEvent()
    data object OnEditDialogDismiss : EditableItemUiEvent()
    data class OnEditDialogConfirm(val text: String) : EditableItemUiEvent()
    data object OnDeleteDialogConfirm : EditableItemUiEvent()
    data object OnDeleteDialogDismiss : EditableItemUiEvent()
}

typealias ScreenshotGalleryUiState = EditableItemUiState<ImageSummary>
typealias FavouriteUiState = EditableItemUiState<Favourite>
