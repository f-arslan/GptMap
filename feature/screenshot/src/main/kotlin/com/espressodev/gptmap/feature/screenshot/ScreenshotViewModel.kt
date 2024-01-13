package com.espressodev.gptmap.feature.screenshot

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.domain.SaveImageAnalysisToStorageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ScreenshotViewModel @Inject constructor(
    private val saveImageAnalysisToStorageUseCase: SaveImageAnalysisToStorageUseCase,
    logService: LogService
) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(ScreenshotUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: ScreenshotUiEvent, navigateToMap: () -> Unit = {}) {
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

            is ScreenshotUiEvent.OnTitleChanged -> _uiState.update { it.copy(title = event.value) }

            ScreenshotUiEvent.OnSaveClicked -> onSaveClick(navigateToMap)
        }
    }

    private fun onSaveClick(navigateToMap: () -> Unit) = launchCatching {
        _uiState.update { it.copy(isSaveStateStarted = true) }

        uiState.value.imageResult.let { image ->
            if (image is ImageResult.Success) {
                saveImageAnalysisToStorageUseCase(image.data, uiState.value.title).getOrThrow()
                navigateToMap()
            }
        }

        _uiState.update { it.copy(isSaveStateStarted = false) }
    }

    private fun onCaptureClick() = launchCatching {
        uiState.value.callback?.invoke()
    }
}
