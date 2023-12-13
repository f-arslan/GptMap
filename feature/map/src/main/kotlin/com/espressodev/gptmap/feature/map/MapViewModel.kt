package com.espressodev.gptmap.feature.map

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.model.emptyImagePlaceholder
import com.espressodev.gptmap.core.palm.PalmService
import com.espressodev.gptmap.core.unsplash_api.UnsplashService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val palmService: PalmService,
    private val unsplashService: UnsplashService,
    logService: LogService,
) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()


    fun onEvent(event: MapUiEvent) {
        when (event) {
            is MapUiEvent.OnSearchValueChanged -> _uiState.update { it.copy(searchValue = event.text) }
            is MapUiEvent.OnSearchClick -> onSearchClick()
            is MapUiEvent.OnDismissBottomSheet -> _uiState.update { it.copy(bottomState = MapBottomState.SEARCH) }
            is MapUiEvent.OnImageDismiss -> _uiState.update {
                it.copy(imageGalleryState = Pair(0, false))
            }

            is MapUiEvent.OnImageClick -> _uiState.update {
                it.copy(imageGalleryState = Pair(event.pos, true))
            }

            is MapUiEvent.OnFavouriteClick -> onFavouriteClick()
            is MapUiEvent.OnStreetViewClick -> onStreetViewClick()
        }
    }

    private fun onSearchClick() = launchCatching {
        _uiState.update {
            it.copy(
                loadingState = LoadingState.Loading,
                searchButtonEnabledState = false,
                searchTextFieldEnabledState = false,
                location = it.location.copy(locationImages = emptyImagePlaceholder)
            )
        }

        palmService.getLocationInfo(uiState.value.searchValue)
            .onSuccess { location ->
                _uiState.update {
                    it.copy(
                        location = location,
                        loadingState = LoadingState.Idle,
                        searchButtonEnabledState = true,
                        searchTextFieldEnabledState = true,
                        bottomState = MapBottomState.DETAIL,
                        searchValue = ""
                    )
                }

                location.content.city.also { city ->
                    unsplashService.getTwoPhotos(city).onSuccess { locationImages ->
                        _uiState.update { it.copy(location = location.copy(locationImages = locationImages)) }
                    }
                }

            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        loadingState = LoadingState.Idle,
                        searchButtonEnabledState = true,
                        searchTextFieldEnabledState = true,
                    )
                }
                throw exception
            }
    }


    private fun onFavouriteClick() {

    }

    private fun onStreetViewClick() {

    }
}