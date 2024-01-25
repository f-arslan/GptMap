package com.espressodev.gptmap.feature.street_view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.TextType
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.core.designsystem.component.LottieAnimationPlaceholder
import com.espressodev.gptmap.core.save_screenshot.composable.SaveScreenshot
import com.google.android.gms.maps.StreetViewPanoramaOptions
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.streetview.StreetView
import com.google.maps.android.ktx.MapsExperimentalFeature
import kotlinx.coroutines.delay
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw
import com.espressodev.gptmap.core.designsystem.R.drawable as AppDrawable
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreetViewRoute(
    latitude: Double,
    longitude: Double,
    popUp: () -> Unit,
    navigateToScreenshot: () -> Unit,
    viewModel: StreetViewViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = latitude, key2 = longitude) {
        viewModel.getStreetView(latitude, longitude)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        initialValue = StreetViewUiState(LatLng(latitude, longitude))
    )
    Scaffold(
        topBar = {
            GmTopAppBar(
                text = TextType.Res(AppText.street_view),
                icon = IconType.Bitmap(AppDrawable.street_view),
                onBackClick = popUp
            )
        }
    ) {
        StreetViewScreen(modifier = Modifier.padding(it), uiState)
    }

    SaveScreenshot(
        onClick = viewModel::initializeScreenCaptureBroadcastReceiver,
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
fun StreetViewScreen(modifier: Modifier, uiState: StreetViewUiState) {
    var isStreetViewLoaded by remember { mutableStateOf(value = false) }

    Box(modifier = modifier.fillMaxSize()) {
        StreetView(
            streetViewPanoramaOptionsFactory = {
                StreetViewPanoramaOptions().position(uiState.latLng)
            },
        )
        LottieAnimationPlaceholder(
            rawRes = AppRaw.earth_orbit,
            visible = !isStreetViewLoaded,
            modifier = Modifier.zIndex(5f)
        )
        // StreetView composable doesn't have a callback for when the street view is loaded
        LaunchedEffect(key1 = uiState.latLng) {
            delay(2000L)
            isStreetViewLoaded = true
        }
    }
}
