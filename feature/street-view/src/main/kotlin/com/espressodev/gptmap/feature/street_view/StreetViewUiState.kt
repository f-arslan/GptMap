package com.espressodev.gptmap.feature.street_view

enum class ScreenshotState {
    IDLE, STARTED, FINISHED,
}

data class StreetViewUiState(
    val latitude: Double,
    val longitude: Double,
    val isScreenshotButtonVisible: Boolean = true,
    val screenshotState: ScreenshotState = ScreenshotState.IDLE,
)
