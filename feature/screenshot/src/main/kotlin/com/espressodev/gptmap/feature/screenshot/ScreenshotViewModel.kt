package com.espressodev.gptmap.feature.screenshot

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.save_screenshot.SaveScreenshotService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class ScreenshotViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    logService: LogService
) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(ScreenCaptureUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBitmapFromFile()
    }

    fun onImageCaptured(bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                bitmap = bitmap,
                screenState = ScreenState.AfterSelectingTheField
            )
        }
    }

    private fun loadBitmapFromFile() = launchCatching {
        applicationContext.getExternalFilesDir(null)?.also { dir ->
            val filePath = "${dir.absolutePath}/screenshots/screenshot.png"
            BitmapFactory.decodeFile(filePath)?.also { bitmap ->
                _uiState.update {
                    it.copy(
                        bitmap = bitmap,
                        screenState = ScreenState.AfterSelectingTheField
                    )
                }
            }
        }
    }
}