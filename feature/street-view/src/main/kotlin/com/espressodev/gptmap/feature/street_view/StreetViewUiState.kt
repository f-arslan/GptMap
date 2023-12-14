package com.espressodev.gptmap.feature.street_view

import com.espressodev.gptmap.core.model.chatgpt.Coordinates
import com.google.android.gms.maps.model.LatLng


data class StreetViewUiState(
    val latLng: LatLng = LatLng(0.0, 0.0)
)