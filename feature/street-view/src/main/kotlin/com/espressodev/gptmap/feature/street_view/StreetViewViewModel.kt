package com.espressodev.gptmap.feature.street_view

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class StreetViewViewModel @Inject constructor(logService: LogService): GmViewModel(logService) {

    private val _uiState = MutableStateFlow(StreetViewUiState())
    val uiState = _uiState.asStateFlow()

    fun getStreetView(latitude: Double, longitude: Double) {
        _uiState.update { it.copy(latLng = LatLng(latitude, longitude)) }
    }
}
