package com.espressodev.gptmap.core.common.snackbar

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SnackbarManager {
    private val messages: MutableStateFlow<SnackbarMessage?> = MutableStateFlow(null)
    val snackbarMessages: StateFlow<SnackbarMessage?>
        get() = messages.asStateFlow()

    fun showMessage(@StringRes message: Int) {
        messages.value = SnackbarMessage.ResourceSnackbar(message)
    }

    fun showMessage(message: String) {
        messages.value = SnackbarMessage.StringSnackbar(message)
    }

    fun showMessage(message: SnackbarMessage) {
        messages.value = message
    }

    fun clean() {
        messages.value = null
    }
}