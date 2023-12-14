package com.espressodev.gptmap.feature.street_view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
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
    popUp: () -> Unit,
    viewModel: StreetViewViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = latitude, key2 = longitude) {
        viewModel.getStreetView(latitude, longitude)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        initialValue = StreetViewUiState(LatLng(latitude, longitude))
    )
    BackHandler {
        popUp()
    }
    StreetViewScreen(uiState)
}


@OptIn(MapsExperimentalFeature::class)
@Composable
fun StreetViewScreen(uiState: StreetViewUiState) {
    var isStreetViewLoaded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        StreetView(
            streetViewPanoramaOptionsFactory = {
                StreetViewPanoramaOptions().position(uiState.latLng)
            },
        )
        if (!isStreetViewLoaded) {
            LoadingAnimation()
        }

        // StreetView composable doesn't have a callback for when the street view is loaded
        LaunchedEffect(key1 = uiState.latLng) {
            delay(1500L)
            isStreetViewLoaded = true
        }
    }
}

@Composable
fun LoadingAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(AppRaw.transistor_traveling))
    val progress by animateLottieCompositionAsState(composition)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .zIndex(2f), contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize(0.5f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StreetViewPreview() {

}