package com.espressodev.gptmap.feature.snapTo_script

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.common.DataStoreService
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.common.SpeechToText
import com.espressodev.gptmap.core.domain.AddImageMessageUseCase
import com.espressodev.gptmap.core.model.AiResponseStatus
import com.espressodev.gptmap.core.model.ImageMessage
import com.espressodev.gptmap.core.model.ImageType
import com.espressodev.gptmap.core.model.ext.toImageType
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.espressodev.gptmap.feature.screenshot_gallery.InputSelector
import com.espressodev.gptmap.feature.screenshot_gallery.SnapToScriptUiEvent
import com.espressodev.gptmap.feature.screenshot_gallery.SnapToScriptUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SnapToScriptViewModel @Inject constructor(
    private val speechToText: SpeechToText,
    private val addImageMessageUseCase: AddImageMessageUseCase,
    private val dataStoreService: DataStoreService,
    realmSyncService: RealmSyncService,
    ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle,
    logService: LogService
) : GmViewModel(logService) {
    private val imageId: String = checkNotNull(savedStateHandle[IMAGE_ID])

    val messages: StateFlow<List<ImageMessage>> = realmSyncService
        .getImageAnalysisMessages(imageId)
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf()
        )

    val imageType: ImageType = realmSyncService.getImageType(imageId).toImageType()

    private val _snapToScriptUiState = MutableStateFlow(SnapToScriptUiState())
    val snapToScriptUiState = _snapToScriptUiState.asStateFlow()

    private val _rmsFlow = MutableSharedFlow<Int>(replay = 1)
    val rmsFlow = _rmsFlow.asSharedFlow()

    private val value
        get() = snapToScriptUiState.value.value

    private val isPinned
        get() = snapToScriptUiState.value.isPinned

    init {
        getNameImageAndCacheIdWithDataStore()
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
            SnapToScriptUiEvent.OnSendClick -> onSendClick()
            SnapToScriptUiEvent.OnTypingEnd -> {
                _snapToScriptUiState.update { it.copy(aiResponseStatus = AiResponseStatus.Idle) }
            }

            SnapToScriptUiEvent.OnKeyboardClick -> {
                _snapToScriptUiState.update { it.copy(inputSelector = InputSelector.Keyboard) }
            }

            SnapToScriptUiEvent.OnPinClick -> {
                _snapToScriptUiState.update { it.copy(isPinned = !isPinned) }
            }
        }
    }

    private fun onSendClick() = launchCatching {
        val value = value.trim()

        _snapToScriptUiState.update {
            it.copy(
                aiResponseStatus = AiResponseStatus.Loading,
                inputSelector = InputSelector.None,
                isTextFieldEnabled = false,
                value = ""
            )
        }

        val addMessageResult = addImageMessageUseCase(imageId = imageId, text = value)

        addMessageResult.getOrElse {
            Log.e("SnapToScriptViewModel", "onSendClick: ", it)
            _snapToScriptUiState.update { it.copy(aiResponseStatus = AiResponseStatus.Idle) }
        }
        _snapToScriptUiState.update {
            it.copy(
                aiResponseStatus = AiResponseStatus.Success,
                isTextFieldEnabled = true
            )
        }
    }

    private fun onMicOffClick() = launchCatching {
        _snapToScriptUiState.update { it.copy(inputSelector = InputSelector.None) }
        speechToText.stopListening()
    }

    private fun onMicClick() {
        launchCatching {
            _snapToScriptUiState.update { it.copy(inputSelector = InputSelector.MicBox) }

            speechToText.startListening().collect { (value, rms, isFinished) ->
                if (value.isNotEmpty()) {
                    val joinedString = value.joinToString(" ")
                    val totalValue = snapToScriptUiState.value.value + joinedString
                    _snapToScriptUiState.update { it.copy(value = totalValue) }
                }
                if (isFinished) {
                    _snapToScriptUiState.update { it.copy(inputSelector = InputSelector.None) }
                }
                _rmsFlow.emit(rms)
            }
        }
    }

    private fun getNameImageAndCacheIdWithDataStore() = launchCatching {
        launch {
            val fullName = dataStoreService.getUserFullName()
            _snapToScriptUiState.update { it.copy(userFirstChar = fullName.first()) }
        }
        launch {
            val imageUrl = dataStoreService.getImageUrl()
            _snapToScriptUiState.update { it.copy(imageUrl = imageUrl) }
        }
        launch {
            val latestImageIdForChat = dataStoreService.getLatestImageIdForChat()
            if (latestImageIdForChat != imageId) {
                dataStoreService.setLatestImageIdForChat(imageId)
            }
        }
    }

    private fun resetSnapToScriptUiState() {
        _snapToScriptUiState.update { SnapToScriptUiState() }
    }
}
