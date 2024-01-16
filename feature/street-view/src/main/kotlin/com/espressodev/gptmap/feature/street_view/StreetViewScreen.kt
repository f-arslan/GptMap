package com.espressodev.gptmap.feature.street_view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.component.GmDraggableButton
import com.espressodev.gptmap.core.designsystem.component.LottieAnimationView
import com.google.android.gms.maps.StreetViewPanoramaOptions
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.streetview.StreetView
import com.google.maps.android.ktx.MapsExperimentalFeature
import kotlinx.coroutines.delay
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw
@Composable
fun StreetViewRoute(
    latitude: Double,
    longitude: Double,
    viewModel: StreetViewViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = latitude, key2 = longitude) {
        viewModel.getStreetView(latitude, longitude)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        initialValue = StreetViewUiState(LatLng(latitude, longitude))
    )
    StreetViewScreen(uiState, onCameraButtonClick = viewModel::onCameraButtonClick)
}


@OptIn(MapsExperimentalFeature::class)
@Composable
fun StreetViewScreen(uiState: StreetViewUiState, onCameraButtonClick: (Boolean) -> Unit) {
    var isStreetViewLoaded by remember { mutableStateOf(value = false) }

    Box(modifier = Modifier.fillMaxSize()) {

        AnimatedVisibility(visible = uiState.cameraButtonState, modifier = Modifier.zIndex(4f)) {
            GmDraggableButton(icon = GmIcons.CameraFilled, onClick = { onCameraButtonClick(true) })
        }

        StreetView(
            streetViewPanoramaOptionsFactory = {
                StreetViewPanoramaOptions().position(uiState.latLng)
            },
        )
        if (!isStreetViewLoaded) {
            LottieAnimationView(AppRaw.earth_orbit)
        }

        // StreetView composable doesn't have a callback for when the street view is loaded
        LaunchedEffect(key1 = uiState.latLng) {
            delay(2000L)
            isStreetViewLoaded = true
        }
    }
}