package com.espressodev.gptmap.feature.snapTo_script

import android.util.Log
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.common.SpeechToText
import com.espressodev.gptmap.core.domain.AddImageMessageUseCase
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.espressodev.gptmap.feature.screenshot_gallery.AiResponseStatus
import com.espressodev.gptmap.feature.screenshot_gallery.InputSelector
import com.espressodev.gptmap.feature.screenshot_gallery.SnapToScriptUiEvent
import com.espressodev.gptmap.feature.screenshot_gallery.SnapToScriptUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class SnapToScriptViewModel @Inject constructor(
    private val speechToText: SpeechToText,
    private val realmSyncService: RealmSyncService,
    private val addImageMessageUseCase: AddImageMessageUseCase,
    logService: LogService
) : GmViewModel(logService) {
    private val _snapToScriptUiState = MutableStateFlow(SnapToScriptUiState())
    val snapToScriptUiState = _snapToScriptUiState.asStateFlow()

    private val _imageAnalysis = MutableStateFlow(ImageAnalysis())
    val imageAnalysis = _imageAnalysis.asStateFlow()

    private val value
        get() = snapToScriptUiState.value.value

    fun initializeImageAnalysis(imageId: String) = launchCatching {
        realmSyncService.getImageAnalysis(imageId)
            .onSuccess { imageAnalysis ->
                _imageAnalysis.update { imageAnalysis }
            }
            .onFailure { throwable ->
                throw Exception(throwable.localizedMessage ?: "Failed to load image analysis")
            }
    }


    fun onEvent(event: SnapToScriptUiEvent) {
        when (event) {
            is SnapToScriptUiEvent.OnTextFieldEnabledStateChanged -> {
                _snapToScriptUiState.update { it.copy(isTextFieldEnabled = event.value) }
            }

            is SnapToScriptUiEvent.OnValueChanged -> {
                _snapToScriptUiState.update { it.copy(value = event.value) }
            }

            SnapToScriptUiEvent.OnMicClick -> onMicClick()
            SnapToScriptUiEvent.OnMicOffClick -> onMicOffClick()

            SnapToScriptUiEvent.OnReset -> resetSnapToScriptUiState()
            SnapToScriptUiEvent.OnKeyboardClick -> {
                _snapToScriptUiState.update { it.copy(inputSelector = InputSelector.Keyboard) }
            }
        }
    }

    fun onSendClick() = launchCatching {
        _snapToScriptUiState.update { it.copy(aiResponseStatus = AiResponseStatus.Loading) }

        addImageMessageUseCase(imageId = imageAnalysis.value.imageId, text = value)
            .onSuccess {
                _snapToScriptUiState.update { it.copy(aiResponseStatus = AiResponseStatus.Idle) }
            }
            .onFailure { throwable ->
                _snapToScriptUiState.update { it.copy(aiResponseStatus = AiResponseStatus.Error) }
                Log.e("SnapToScriptViewModel", "onSendClick: failure: $throwable")
            }
    }

    private fun onMicOffClick() = launchCatching {
        _snapToScriptUiState.update { it.copy(inputSelector = InputSelector.Keyboard) }
        speechToText.stopListening()
    }

    private fun onMicClick() = launchCatching {
        _snapToScriptUiState.update { it.copy(inputSelector = InputSelector.MicBox) }
        speechToText.startListening().collect { (value, rms, isFinished) ->
            if (value.isNotEmpty()) {
                val joinedString = value.joinToString(" ")
                val totalValue = snapToScriptUiState.value.value + joinedString
                _snapToScriptUiState.update { it.copy(value = totalValue) }
            }
            if (rms > 0) {
                _snapToScriptUiState.update { it.copy(rmsValue = rms) }
            }
            if (isFinished) {
                _snapToScriptUiState.update { it.copy(inputSelector = InputSelector.Keyboard) }
            }
        }
    }

    private fun resetSnapToScriptUiState() {
        _snapToScriptUiState.update { SnapToScriptUiState() }
    }

}
