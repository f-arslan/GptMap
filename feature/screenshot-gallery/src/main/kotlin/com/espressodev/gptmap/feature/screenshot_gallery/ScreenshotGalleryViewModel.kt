package com.espressodev.gptmap.feature.screenshot_gallery

import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.data.repository.FileRepository
import com.espressodev.gptmap.core.data.repository.ImageAnalysisRepository
import com.espressodev.gptmap.core.model.Constants.IMAGE_PHONE_CASH_SIZE
import com.espressodev.gptmap.core.model.EditableItemUiEvent
import com.espressodev.gptmap.core.model.Exceptions
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.ImageSummary
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.model.ScreenshotGalleryUiState
import com.espressodev.gptmap.core.mongodb.ImageAnalysisDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScreenshotGalleryViewModel @Inject constructor(
    private val imageAnalysisDataSource: ImageAnalysisDataSource,
    private val fileRepository: FileRepository,
    logService: LogService,
    private val ioDispatcher: CoroutineDispatcher,
    private val imageAnalysisRepository: ImageAnalysisRepository,
) : GmViewModel(logService) {
    val imageAnalyses = imageAnalysisDataSource
        .getImageAnalyses()
        .map<List<ImageAnalysis>, Response<List<ImageSummary>>> {
            Response.Success(
                it.map { imageAnalysis -> imageAnalysis.toImageAnalysisSummary() }
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

    fun onEvent(event: EditableItemUiEvent) {
        when (event) {
            EditableItemUiEvent.OnCancelClick -> reset()
            EditableItemUiEvent.OnDeleteClick -> _uiState.update { it.copy(deleteDialogState = true) }
            EditableItemUiEvent.OnDeleteDialogConfirm -> {
                onDeleteDialogConfirmClick()
            }

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
            fileRepository.saveImageToInternal(
                imageUrl = imageUrl,
                fileId = imageId,
                size = IMAGE_PHONE_CASH_SIZE
            ).getOrThrow()
            navigate(imageId)
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
        imageAnalysisRepository.deleteImageAnalyses(selectedItemsIds).getOrThrow()
        reset()
    }

    private fun onEditDialogConfirmClick(text: String) = launchCatching {
        withContext(ioDispatcher) {
            imageAnalysisDataSource.updateImageAnalysisText(imageSummaryId, text).getOrThrow()
        }
        reset()
    }

    private fun reset() {
        _uiState.update {
            ScreenshotGalleryUiState(
                selectedItem = ImageSummary(),
                deleteDialogState = false
            )
        }
    }
}
