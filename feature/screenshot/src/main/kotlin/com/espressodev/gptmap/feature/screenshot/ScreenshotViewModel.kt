package com.espressodev.gptmap.feature.screenshot

import android.content.Context
import android.graphics.BitmapFactory
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ScreenshotViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    logService: LogService
) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(ScreenshotUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBitmapFromFile()
    }

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

            ScreenshotUiEvent.OnSaveClicked -> onSaveClick()
        }
    }

    private fun onSaveClick() {

    }

    private fun onCaptureClick() = launchCatching {
        uiState.value.callback?.invoke()
    }

    private fun loadBitmapFromFile() = launchCatching {
        withContext(Dispatchers.IO) {
            val dir = applicationContext.getExternalFilesDir(null) ?: return@withContext
            val filePath = "${dir.absolutePath}/screenshots/screenshot.png"

            // Decode the full-size bitmap
            val bitmap = BitmapFactory.decodeFile(filePath)

            // Update state on the main thread
            withContext(Dispatchers.Main) {
                _uiState.update { currentState ->
                    currentState.copy(bitmap = bitmap)
                }
            }
        }
    }
}