package com.espressodev.gptmap.core.common.snackbar

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object SnackbarManager {
    private val messages = MutableStateFlow<Pair<SnackbarMessage, SnackbarDuration?>?>(null)
    val snackbarMessages get() = messages.asStateFlow()

    fun showMessage(@StringRes message: Int, duration: SnackbarDuration? = null) {
        messages.update { Pair(SnackbarMessage.ResourceSnackbar(message), duration) }
    }

    fun showMessage(message: String, duration: SnackbarDuration? = null) {
        messages.update { Pair(SnackbarMessage.StringSnackbar(message), duration) }
    }

    fun clean() {
        messages.update { null }
    }
}
