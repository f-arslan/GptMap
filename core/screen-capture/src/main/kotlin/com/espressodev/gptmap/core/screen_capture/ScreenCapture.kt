package com.espressodev.gptmap.core.screen_capture

import android.app.Activity
import android.content.Context
import android.media.projection.MediaProjectionManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.GmDraggableButton

@Composable
fun BoxScope.ScreenCapture(viewModel: ScreenCaptureViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Log.i("ScreenCapture", "uiState: $uiState")

    ScreenCaptureScreen(uiState)
}

@Composable
private fun BoxScope.ScreenCaptureScreen(uiState: ScreenCaptureUiState) {
    val context = LocalContext.current
    val mediaProjectionManager =
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    val screenCaptureIntent = remember { mediaProjectionManager.createScreenCaptureIntent() }
    val screenCaptureLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                context.startService(
                    data?.let { intent ->
                        ScreenCaptureService.getStartIntent(
                            context,
                            result.resultCode,
                            intent
                        )
                    }
                )
            }
        }

    if (uiState.isCameraButtonVisible)
        GmDraggableButton(
            icon = GmIcons.CameraFilled,
            onClick = {
                screenCaptureLauncher.launch(screenCaptureIntent)
            }
        )
}