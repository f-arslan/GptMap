package com.espressodev.gptmap.core.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class GmViewModel(private val logService: LogService) : ViewModel() {
    fun launchCatching(snackbar: Boolean = true, block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                // It's only for debug purposes
                if (snackbar) {
                    // SnackbarManager.showMessage(throwable.toSnackbarMessage())
                }
                logService.logNonFatalCrash(throwable)
                Log.e("GmViewModel", "launchCatching: ", throwable)
            },
            block = block,
        )
}
