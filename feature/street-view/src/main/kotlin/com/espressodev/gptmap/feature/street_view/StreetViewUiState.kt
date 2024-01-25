package com.espressodev.gptmap.feature.street_view

import com.google.android.gms.maps.model.LatLng

enum class ScreenshotState {
    IDLE, STARTED, FINISHED,
}

data class StreetViewUiState(
    val latLng: LatLng = LatLng(0.0, 0.0),
    val isScreenshotButtonVisible: Boolean = true,
    val screenshotState: ScreenshotState = ScreenshotState.IDLE,
)