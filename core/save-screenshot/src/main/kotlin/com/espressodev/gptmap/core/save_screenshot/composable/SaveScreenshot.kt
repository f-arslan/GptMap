package com.espressodev.gptmap.core.save_screenshot.composable

import android.app.Activity
import android.content.Context
import android.media.projection.MediaProjectionManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.GmDraggableButton
import com.espressodev.gptmap.core.save_screenshot.SaveScreenshotService

@Composable
fun SaveScreenshot(
    onSuccess: () -> Unit,
    onProcessStarted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScreenshotViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = uiState.screenState) {
        when (uiState.screenState) {
            ScreenState.Finished -> {
                onSuccess()
                viewModel.resetScreenState()
            }
            ScreenState.Started -> {
                onProcessStarted()
            }
            else -> {}
        }
    }

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
                        SaveScreenshotService.getStartIntent(
                            context,
                            result.resultCode,
                            intent
                        )
                    }
                )
            }
        }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isButtonVisible)
            GmDraggableButton(
                icon = GmIcons.CameraFilled,
                onClick = {
                    screenCaptureLauncher.launch(screenCaptureIntent)
                }
            )
    }
}
