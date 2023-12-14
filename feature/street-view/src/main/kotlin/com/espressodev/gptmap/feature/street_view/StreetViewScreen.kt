package com.espressodev.gptmap.feature.street_view

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.LatLng


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
}

@Composable
fun StreetViewScreen() {

}


@Preview(showBackground = true)
@Composable
fun StreetViewPreview() {
    StreetViewScreen()
}