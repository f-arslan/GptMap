package com.espressodev.gptmap.feature.screenshot_gallery

enum class InputSelector {
    None, Keyboard, MicBox
}

data class SnapToScriptUiState(
    val value: String = "",
    val isTextFieldEnabled: Boolean = true,
    val isSendButtonEnabled: Boolean = true,
    val rmsValue : Int = 0,
    val inputSelector: InputSelector = InputSelector.None
)

sealed class SnapToScriptUiEvent {
    data class OnValueChanged(val value: String): SnapToScriptUiEvent()
    data class OnTextFieldEnabledStateChanged(val value: Boolean): SnapToScriptUiEvent()
    data object OnSendClick: SnapToScriptUiEvent()
    data object OnMicClick: SnapToScriptUiEvent()
    data object OnMicOffClick: SnapToScriptUiEvent()
    data object OnReset: SnapToScriptUiEvent()
    data object OnKeyboardClick: SnapToScriptUiEvent()
}
