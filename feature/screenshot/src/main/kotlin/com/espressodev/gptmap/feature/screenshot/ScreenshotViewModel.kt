package com.espressodev.gptmap.feature.screenshot

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.domain.SaveImageAnalysisToStorageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ScreenshotViewModel @Inject constructor(
    private val saveImageAnalysisToStorageUseCase: SaveImageAnalysisToStorageUseCase,
    logService: LogService
) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(ScreenshotUiState())
    val uiState = _uiState.asStateFlow()


    fun onEvent(event: ScreenshotUiEvent, navigateToImageAnalysis: () -> Unit = {}) {
        when (event) {
            is ScreenshotUiEvent.OnBitmapChanged ->
                _uiState.update { currentState ->
                    currentState.copy(
                        bitmap = event.bitmap,
                        screenState = ScreenState.AfterSelectingTheField
                    )
                }

            is ScreenshotUiEvent.OnCallbackChanged ->
                _uiState.update { currentState ->
                    currentState.copy(callback = event.callback)
                }

            ScreenshotUiEvent.OnCaptureClicked -> onCaptureClick()
            is ScreenshotUiEvent.OnImageResultChanged ->
                _uiState.update { currentState ->
                    currentState.copy(imageResult = event.imageResult)
                }

            ScreenshotUiEvent.OnSaveClicked -> onSaveClick(navigateToImageAnalysis)
        }
    }

    private fun onSaveClick(navigateToImageAnalysis: () -> Unit) = launchCatching {
        withContext(Dispatchers.IO) {
            uiState.value.imageResult.let { image ->
                if (image is ImageResult.Success) {
                    saveImageAnalysisToStorageUseCase(image.data).getOrThrow()
                    navigateToImageAnalysis()
                }
            }
        }
    }

    private fun onCaptureClick() = launchCatching {
        uiState.value.callback?.invoke()
    }
}