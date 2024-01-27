package com.espressodev.gptmap.core.common.snackbar

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object SnackbarManager {
    private val messages = MutableStateFlow<SnackbarMessage?>(null)
    val snackbarMessages get() = messages.asStateFlow()

    fun showMessage(@StringRes message: Int) {
        messages.update { SnackbarMessage.ResourceSnackbar(message) }
    }

    fun showMessage(message: String) {
        messages.update { SnackbarMessage.StringSnackbar(message) }
    }

    fun clean() {
        messages.update { null }
    }
}
