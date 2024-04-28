package com.espressodev.gptmap.feature.map

import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.data.repository.MyLocationRepository
import com.espressodev.gptmap.core.designsystem.Constants
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapMyLocationState(
    val isMyLocationButtonVisible: Boolean = true,
    val componentLoadingState: ComponentLoadingState = ComponentLoadingState.NOTHING,
    val isMyLocationFetched: Boolean = false,
    val isFirstTimeFetched: Boolean = false,
    val myLocationCoordinates: Pair<Double, Double> = 0.0 to 0.0
) {
    val myCoordinatesLatLng: LatLng
        get() = myLocationCoordinates.run { LatLng(first, second) }
}

sealed interface MapLocationEvent {
    data object OnMyCurrentLocationClick : MapLocationEvent
    data object OnWhenNavigateToMyLocation : MapLocationEvent
}

@HiltViewModel
class MyLocationViewModel @Inject constructor(
    logService: LogService,
    private val myLocationRepository: MyLocationRepository
) : GmViewModel(logService) {

    private val _mapMyLocationState = MutableStateFlow(MapMyLocationState())
    val mapLocationState = _mapMyLocationState.asStateFlow()

    fun onEvent(event: MapLocationEvent) {
        when (event) {
            MapLocationEvent.OnMyCurrentLocationClick -> getMyCurrentLocation()
            MapLocationEvent.OnWhenNavigateToMyLocation -> onNavigateToMyLocationFirstTime()
        }
    }

    private val myCoordinatesLatLng
        get() = mapLocationState.value.myCoordinatesLatLng

    private var myLocationJob: Job? = null
    private fun getMyCurrentLocation() {
        myLocationJob?.cancel()
        myLocationJob = viewModelScope.launch {
            if (myCoordinatesLatLng.latitude != 0.0) {
                return@launch
            }

            _mapMyLocationState.update {
                it.copy(
                    isMyLocationButtonVisible = false,
                    componentLoadingState = ComponentLoadingState.MY_LOCATION
                )
            }

            myLocationRepository.getCurrentLocation().collect { locationResult ->
                locationResult
                    .onSuccess { coordinates ->
                        _mapMyLocationState.update {
                            it.copy(isMyLocationFetched = true, myLocationCoordinates = coordinates)
                        }
                    }
                    .onFailure { throwable ->
                        val message = throwable.message ?: Constants.GENERIC_ERROR_MSG
                        SnackbarManager.showMessage(message)
                    }

                _mapMyLocationState.update {
                    it.copy(
                        isMyLocationButtonVisible = true,
                        componentLoadingState = ComponentLoadingState.NOTHING
                    )
                }
            }
        }
    }

    private fun onNavigateToMyLocationFirstTime() {
        _mapMyLocationState.update { it.copy(isFirstTimeFetched = true) }
    }
}
