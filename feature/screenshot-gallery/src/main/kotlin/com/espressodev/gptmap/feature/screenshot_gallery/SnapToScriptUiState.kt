package com.espressodev.gptmap.feature.screenshot_gallery

import com.espressodev.gptmap.core.model.AiResponseStatus

enum class InputSelector {
    None, Keyboard, MicBox
}

data class SnapToScriptUiState(
    val value: String = "",
    val isTextFieldEnabled: Boolean = true,
    val inputSelector: InputSelector = InputSelector.None,
    val aiResponseStatus: AiResponseStatus = AiResponseStatus.Idle,
    val userFirstChar: Char = 'U',
    val imageUrl: String = "",
    val isPinned: Boolean = true
)

sealed class SnapToScriptUiEvent {
    data class OnValueChanged(val value: String) : SnapToScriptUiEvent()
    data class OnTextFieldEnabledStateChanged(val value: Boolean) : SnapToScriptUiEvent()
    data object OnMicClick : SnapToScriptUiEvent()
    data object OnMicOffClick : SnapToScriptUiEvent()
    data object OnReset : SnapToScriptUiEvent()
    data object OnSendClick : SnapToScriptUiEvent()
    data object OnTypingEnd : SnapToScriptUiEvent()
    data object OnKeyboardClick : SnapToScriptUiEvent()
    data object OnPinClick : SnapToScriptUiEvent()
}
