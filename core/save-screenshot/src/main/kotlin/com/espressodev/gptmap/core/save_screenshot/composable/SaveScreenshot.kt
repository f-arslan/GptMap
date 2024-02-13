package com.espressodev.gptmap.core.save_screenshot.composable

import android.app.Activity
import android.content.Context
import android.media.projection.MediaProjectionManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.component.GmDraggableButton
import com.espressodev.gptmap.core.save_screenshot.SaveScreenshotService

@Composable
fun SaveScreenshot(
    onClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
    isButtonVisible: Boolean = true,
) {
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
            } else {
                onCancelClick()
            }
        }

    if (isButtonVisible)
        GmDraggableButton(
            icon = IconType.Vector(GmIcons.CameraFilled),
            onClick = {
                onClick()
                screenCaptureLauncher.launch(screenCaptureIntent)
            },
            modifier = modifier
        )
}
