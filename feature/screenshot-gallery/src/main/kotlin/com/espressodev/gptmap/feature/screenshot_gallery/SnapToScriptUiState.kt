package com.espressodev.gptmap.feature.screenshot_gallery

import com.espressodev.gptmap.core.model.AiResponseStatus


enum class InputSelector {
    None, Keyboard, MicBox
}


data class SnapToScriptUiState(
    val value: String = "",
    val isTextFieldEnabled: Boolean = true,
    val isSendButtonEnabled: Boolean = true,
    val rmsValue : Int = 0,
    val inputSelector: InputSelector = InputSelector.None,
    val aiResponseStatus: AiResponseStatus = AiResponseStatus.Idle,
    val userFirstChar: Char = 'U',
    val imageUrl: String = ""
)

sealed class SnapToScriptUiEvent {
    data class OnValueChanged(val value: String): SnapToScriptUiEvent()
    data class OnTextFieldEnabledStateChanged(val value: Boolean): SnapToScriptUiEvent()
    data object OnMicClick: SnapToScriptUiEvent()
    data object OnMicOffClick: SnapToScriptUiEvent()
    data object OnReset: SnapToScriptUiEvent()
    data object OnKeyboardClick: SnapToScriptUiEvent()
    data object OnSendClick: SnapToScriptUiEvent()
}
