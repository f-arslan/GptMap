package com.espressodev.gptmap.feature.screenshot_gallery

import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.model.Exceptions
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.model.ImageSummary
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
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
    private val realmSyncService: RealmSyncService,
    logService: LogService,
    private val ioDispatcher: CoroutineDispatcher,
) : GmViewModel(logService) {
    val imageAnalyses = realmSyncService
        .getImageAnalyses()
        .map<List<ImageAnalysis>, Response<PersistentList<ImageSummary>>> {
            Response.Success(
                it.map { imageAnalysis -> imageAnalysis.toImageAnalysisSummary() }
                    .toPersistentList()
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

    private val _uiState = MutableStateFlow(ScreenshotGalleryUiState())
    val uiState = _uiState.asStateFlow()

    private val imageSummaryId
        get() = uiState.value.selectedImageSummary.id


    fun onEvent(event: ScreenshotGalleryUiEvent) {
        when (event) {
            ScreenshotGalleryUiEvent.OnCancelClick -> reset()
            ScreenshotGalleryUiEvent.OnDeleteClick -> onDeleteClick()
            ScreenshotGalleryUiEvent.OnEditClick -> _uiState.update { it.copy(uiIsInEditMode = true) }
            ScreenshotGalleryUiEvent.OnEditDialogDismiss -> _uiState.update {
                it.copy(editDialogState = false)
            }

            is ScreenshotGalleryUiEvent.OnLongClickToImage -> _uiState.update {
                it.copy(selectedImageSummary = event.imageSummary, uiIsInEditMode = true)
            }

            ScreenshotGalleryUiEvent.Reset -> reset()
            is ScreenshotGalleryUiEvent.OnEditDialogConfirm -> onEditDialogConfirmClick(event.text)
        }
    }

    private fun onEditDialogConfirmClick(text: String) = launchCatching {
        withContext(ioDispatcher) {
            realmSyncService.updateImageAnalysisText(imageSummaryId, text).getOrThrow()
        }
        reset()
    }

    private fun reset() {
        _uiState.update { ScreenshotGalleryUiState() }
    }
    private fun onDeleteClick() = launchCatching {
        withContext(ioDispatcher) {
            realmSyncService.deleteImageAnalysis(imageSummaryId).getOrThrow()
        }
        reset()
    }
}
