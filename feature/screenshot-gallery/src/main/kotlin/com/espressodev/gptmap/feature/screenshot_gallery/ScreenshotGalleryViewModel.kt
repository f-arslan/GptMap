package com.espressodev.gptmap.feature.screenshot_gallery

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.data.StorageService
import com.espressodev.gptmap.core.data.StorageService.Companion.ANALYSIS_IMAGE_REFERENCE
import com.espressodev.gptmap.core.domain.SaveImageToInternalStorageUseCase
import com.espressodev.gptmap.core.model.EditableItemUiEvent
import com.espressodev.gptmap.core.model.Exceptions
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.ImageSummary
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.model.ScreenshotGalleryUiState
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScreenshotGalleryViewModel @Inject constructor(
    private val realmSyncService: RealmSyncService,
    private val storageService: StorageService,
    private val saveImageToInternalStorageUseCase: SaveImageToInternalStorageUseCase,
    logService: LogService,
    private val ioDispatcher: CoroutineDispatcher,
) : GmViewModel(logService) {
    val imageAnalyses = realmSyncService
        .getImageAnalyses()
        .map<List<ImageAnalysis>, Response<ImmutableList<ImageSummary>>> {
            Response.Success(
                it.map { imageAnalysis -> imageAnalysis.toImageAnalysisSummary() }
                    .toImmutableList()
            )
        }
        .catch {
            Response.Failure(Exceptions.RealmFailedToLoadImageAnalysesException())
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            Response.Loading
        )


    private val _uiState =
        MutableStateFlow(ScreenshotGalleryUiState(selectedItem = ImageSummary()))
    val uiState = _uiState.asStateFlow()

    private val imageSummaryId
        get() = uiState.value.selectedItemsIds.first()

    private val selectedItemsIds
        get() = uiState.value.selectedItemsIds

    private val selectedItemCount
        get() = uiState.value.selectedItemsCount

    fun onEvent(event: EditableItemUiEvent) {
        when (event) {
            EditableItemUiEvent.OnCancelClick -> reset()
            EditableItemUiEvent.OnDeleteClick -> _uiState.update { it.copy(deleteDialogState = true) }
            EditableItemUiEvent.OnDeleteDialogConfirm -> onDeleteDialogConfirmClick()
            EditableItemUiEvent.OnDeleteDialogDismiss -> _uiState.update { it.copy(deleteDialogState = false) }
            EditableItemUiEvent.OnEditClick -> _uiState.update { it.copy(editDialogState = true) }
            is EditableItemUiEvent.OnEditDialogConfirm -> onEditDialogConfirmClick(event.text)
            EditableItemUiEvent.OnEditDialogDismiss -> _uiState.update { it.copy(editDialogState = false) }
            is EditableItemUiEvent.OnLongClickToItem<*> -> itemOnLongClick(event.item as ImageSummary)

            EditableItemUiEvent.Reset -> reset()
        }
    }

    fun navigateToSnapToScript(imageId: String, imageUrl: String, navigate: (String) -> Unit) =
        launchCatching {
            saveImageToInternalStorageUseCase(imageUrl = imageUrl, fileId = imageId)
                .onSuccess {
                    navigate(imageId)
                }
                .onFailure {
                    Log.e("ScreenshotGalleryViewModel", "Failed to save image to internal storage")
                }
        }

    private fun itemOnLongClick(imageSummary: ImageSummary) {
        val id = imageSummary.id
        _uiState.update { currentState ->
            val newSelectedItems =
                if (id in currentState.selectedItemsIds) {
                    currentState.selectedItemsIds.remove(id)
                } else {
                    currentState.selectedItemsIds.add(id)
                }

            val newSelectedItemCount = newSelectedItems.size
            val isMultipleSelection = newSelectedItemCount > 1
            val newTopBarTitle = when (newSelectedItemCount) {
                0 -> "You can select"
                1 -> "1 item selected"
                else -> "$newSelectedItemCount items selected"
            }

            currentState.copy(
                isUiInEditMode = true,
                selectedItemsIds = newSelectedItems,
                selectedItemsCount = newSelectedItemCount,
                selectedItem = imageSummary,
                isSelectedItemAboveOne = isMultipleSelection,
                topBarTitle = newTopBarTitle
            )
        }
    }


    private fun onDeleteDialogConfirmClick() = launchCatching {
        withContext(ioDispatcher) {
            launch {
                if (selectedItemCount == 1) {
                    realmSyncService.deleteImageAnalysis(imageSummaryId).getOrThrow()
                } else {
                    realmSyncService.deleteImageAnalyses(imageAnalysesIds = selectedItemsIds)
                        .getOrThrow()
                }
                reset()
            }
            launch {
                for (id in selectedItemsIds) {
                    storageService.deleteImage(id, ANALYSIS_IMAGE_REFERENCE).getOrThrow()
                }
            }
        }
    }

    private fun onEditDialogConfirmClick(text: String) = launchCatching {
        withContext(ioDispatcher) {
            realmSyncService.updateImageAnalysisText(imageSummaryId, text).getOrThrow()
        }
        reset()
    }

    private fun reset() {
        _uiState.update { ScreenshotGalleryUiState(selectedItem = ImageSummary()) }
    }
}
