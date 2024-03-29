package com.espressodev.gptmap.feature.screenshot

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.data.repository.ImageAnalysisRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ScreenshotViewModel @Inject constructor(
    private val imageAnalysisRepository: ImageAnalysisRepository,
    logService: LogService
) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(ScreenshotUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState = MutableStateFlow<NavigationState>(NavigationState.None)
    val navigationState = _navigationState.asStateFlow()

    fun onEvent(event: ScreenshotUiEvent) {
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

            ScreenshotUiEvent.OnSaveClicked -> onSaveClick()
        }
    }

    private fun onSaveClick() = launchCatching {
        _uiState.update { it.copy(isSaveStateStarted = true) }

        uiState.value.imageResult.let { image ->
            if (image is ImageResult.Success) {
                imageAnalysisRepository.saveImageAnalysisToStorage(image.data, uiState.value.title)
                    .onSuccess {
                        _uiState.update { it.copy(isSaveStateStarted = false) }
                        _navigationState.update { NavigationState.PopUp }
                    }.onFailure { throwable ->
                        _uiState.update { it.copy(isSaveStateStarted = false) }
                        throw throwable
                    }
            }
        }
    }

    private fun onCaptureClick() = launchCatching {
        uiState.value.callback?.invoke()
    }
}
