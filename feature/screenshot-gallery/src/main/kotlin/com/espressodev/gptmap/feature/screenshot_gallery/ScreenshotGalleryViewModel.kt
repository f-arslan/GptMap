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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ScreenshotGalleryViewModel @Inject constructor(
    realmSyncService: RealmSyncService,
    logService: LogService
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

    fun onLongClickToImage(imageSummary: ImageSummary) = launchCatching {
        _uiState.update { it.copy(selectedImageSummary = imageSummary, uiIsInEditMode = true) }
    }

    fun onCancelClick() {
        _uiState.update { it.copy(uiIsInEditMode = false) }
    }

    fun onEditClick() {

    }

    fun onDeleteClick() {

    }
}
