package com.espressodev.gptmap.core.screen_capture.composable

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
import com.espressodev.gptmap.core.screen_capture.ScreenCaptureService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

enum class TakingScreenshotProgress {
    Idle, OnProgress
}

enum class ScreenState {
    Initial, AfterTakingScreenshot, AfterCapturingTheImage
}

data class ScreenCaptureUiState(
    val isCameraButtonVisible: Boolean = true,
    val takingScreenshotProgress: TakingScreenshotProgress = TakingScreenshotProgress.Idle,
    val bitmap: Bitmap? = null,
    val screenState: ScreenState = ScreenState.Initial
)

@HiltViewModel
class ScreenCaptureViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    logService: LogService
) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(ScreenCaptureUiState())
    val uiState = _uiState.asStateFlow()

    private val serviceStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ScreenCaptureService.ACTION_SERVICE_STARTED -> {
                    _uiState.update {
                        it.copy(
                            takingScreenshotProgress = TakingScreenshotProgress.OnProgress,
                            isCameraButtonVisible = false
                        )
                    }
                }

                ScreenCaptureService.ACTION_SERVICE_STOPPED -> {
                    _uiState.update {
                        it.copy(
                            takingScreenshotProgress = TakingScreenshotProgress.Idle,
                            bitmap = loadBitmapFromFile(),
                            screenState = ScreenState.AfterTakingScreenshot
                        )
                    }
                }
            }
        }

    }

    init {
        initializeScreenCaptureBroadcastReceiver()
    }


    fun onImageCaptured(bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                bitmap = bitmap,
                screenState = ScreenState.AfterCapturingTheImage
            )
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun initializeScreenCaptureBroadcastReceiver() = launchCatching {
        val filter = IntentFilter().apply {
            addAction(ScreenCaptureService.ACTION_SERVICE_STARTED)
            addAction(ScreenCaptureService.ACTION_SERVICE_STOPPED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            applicationContext.registerReceiver(
                serviceStateReceiver,
                filter,
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            applicationContext.registerReceiver(serviceStateReceiver, filter)
        }
    }

    fun loadBitmapFromFile(): Bitmap =
        applicationContext.getExternalFilesDir(null)?.let { dir ->
            val filePath = "${dir.absolutePath}/screenshots/screenshot.png"
            BitmapFactory.decodeFile(filePath)
        } ?: throw Exception("Failed to load bitmap from file")


    override fun onCleared() {
        applicationContext.unregisterReceiver(serviceStateReceiver)
        super.onCleared()
    }
}