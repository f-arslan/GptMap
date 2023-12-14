package com.espressodev.gptmap.feature.street_view

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
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
    Log.d("StreetViewRoute", "uiState: $uiState")
    BackHandler {
        popUp()
    }
    StreetViewScreen(uiState)
}
val streetViewPanoramaOptions = StreetViewPanoramaOptions()
@OptIn(MapsExperimentalFeature::class)
@Composable
fun StreetViewScreen(uiState: StreetViewUiState) {
    StreetView(
        streetViewPanoramaOptionsFactory = {
            streetViewPanoramaOptions.position(uiState.latLng)
        },
        isPanningGesturesEnabled = true,
        isStreetNamesEnabled = true,
        isUserNavigationEnabled = true,
        isZoomGesturesEnabled = true
    )
}


@Preview(showBackground = true)
@Composable
fun StreetViewPreview() {

}