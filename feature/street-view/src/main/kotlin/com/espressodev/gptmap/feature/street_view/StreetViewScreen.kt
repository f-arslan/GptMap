package com.espressodev.gptmap.feature.street_view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.component.BackButton
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.save_screenshot.composable.SaveScreenshot
import com.google.android.gms.maps.StreetViewPanoramaOptions
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.streetview.StreetView
import com.google.maps.android.ktx.MapsExperimentalFeature
import kotlinx.coroutines.delay

@Composable
internal fun StreetViewRoute(
    popUp: () -> Unit,
    navigateToScreenshot: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StreetViewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    StreetViewScreen(
        latitude = uiState.latitude,
        longitude = uiState.longitude,
        popUp = popUp,
        modifier = modifier
    )

    SaveScreenshot(
        onClick = viewModel::initializeScreenCaptureBroadcastReceiver,
        onCancelClick = viewModel::reset,
        isButtonVisible = uiState.isScreenshotButtonVisible
    )

    LaunchedEffect(key1 = uiState.screenshotState) {
        if (uiState.screenshotState == ScreenshotState.FINISHED) {
            viewModel.reset()
            navigateToScreenshot()
        }
    }
}

@OptIn(MapsExperimentalFeature::class)
@Composable
private fun StreetViewScreen(
    latitude: Double,
    longitude: Double,
    popUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (isStreetViewLoaded, setStreetViewLoaded) = remember { mutableStateOf(value = false) }

    LaunchedEffect(key1 = latitude) {
        delay(1250L)
        setStreetViewLoaded(true)
    }

    Box(modifier = modifier.fillMaxSize()) {
        StreetView(
            modifier = Modifier.matchParentSize(),
            streetViewPanoramaOptionsFactory = {
                StreetViewPanoramaOptions().position(
                    LatLng(
                        latitude,
                        longitude
                    )
                )
            }
        )
        AnimatedVisibility(!isStreetViewLoaded, modifier = Modifier.align(Alignment.Center)) {
            CircularProgressIndicator()
        }

        if (isStreetViewLoaded) {
            BackButton { popUp() }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StreetViewPreview() {
    GptmapTheme {
        StreetViewScreen(latitude = 10.0, longitude = 20.0, {})
    }
}
