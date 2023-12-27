package com.espressodev.gptmap.feature.map

import android.util.Log
import com.espressodev.gptmap.api.gemini.GeminiService
import com.espressodev.gptmap.api.unsplash.UnsplashService
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.domain.AddDatabaseIfUserIsNewUseCase
import com.espressodev.gptmap.core.domain.SaveImageToFirebaseStorageUseCase
import com.espressodev.gptmap.core.domain.SaveImageToInternalStorageUseCase
import com.espressodev.gptmap.core.model.ext.classTag
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val geminiService: GeminiService,
    private val unsplashService: UnsplashService,
    private val saveImageToFirebaseStorageUseCase: SaveImageToFirebaseStorageUseCase,
    private val addDatabaseIfUserIsNewUseCase: AddDatabaseIfUserIsNewUseCase,
    private val realmSyncService: RealmSyncService,
    logService: LogService,
) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    init {
        launchCatching {
            addDatabaseIfUserIsNewUseCase()
        }
    }

    fun onEvent(event: MapUiEvent, navigateToStreetView: (LatLng) -> Unit = {}) {
        when (event) {
            is MapUiEvent.OnSearchValueChanged -> _uiState.update { it.copy(searchValue = event.text) }
            is MapUiEvent.OnSearchClick -> onSearchClick()
            is MapUiEvent.OnImageDismiss -> _uiState.update {
                it.copy(imageGalleryState = Pair(0, false))
            }

            is MapUiEvent.OnImageClick -> _uiState.update {
                it.copy(imageGalleryState = Pair(event.pos, true))
            }

            is MapUiEvent.OnFavouriteClick -> onFavouriteClick()
            is MapUiEvent.OnStreetViewClick -> {
                onStreetViewClick(event.latLng, navigateToStreetView)
            }

            is MapUiEvent.OnExploreWithAiClick -> _uiState.update { it.copy(bottomSheetState = MapBottomSheetState.DETAIL_CARD) }
            is MapUiEvent.OnDetailSheetBackClick -> _uiState.update { it.copy(bottomSheetState = MapBottomSheetState.SMALL_INFORMATION_CARD) }
            is MapUiEvent.OnBackClick -> _uiState.update {
                it.copy(
                    bottomSheetState = MapBottomSheetState.NOTHING,
                    bottomSearchState = true
                )
            }
        }
    }

    private fun onSearchClick() = launchCatching {
        _uiState.update {
            it.copy(
                componentLoadingState = ComponentLoadingState.MAP,
                searchButtonEnabledState = false,
                searchTextFieldEnabledState = false,
            )
        }

        geminiService.getLocationInfo(uiState.value.searchValue)
            .onSuccess { location ->
                _uiState.update {
                    it.copy(
                        location = location,
                        componentLoadingState = ComponentLoadingState.NOTHING,
                        searchButtonEnabledState = true,
                        searchTextFieldEnabledState = true,
                        bottomSheetState = MapBottomSheetState.SMALL_INFORMATION_CARD,
                        bottomSearchState = false,
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
                        componentLoadingState = ComponentLoadingState.NOTHING,
                        searchButtonEnabledState = true,
                        searchTextFieldEnabledState = true,
                        bottomSearchState = true
                    )
                }
                throw exception
            }
    }


    private fun onFavouriteClick() = launchCatching {
        uiState.value.location.also { location ->
            saveImageToFirebaseStorageUseCase(location).onSuccess {
                _uiState.update { state ->
                    state.copy(
                        location = state.location.copy(
                            addToFavouriteButtonState = false
                        ),
                        isFavouriteButtonPlaying = true
                    )
                }
            }.onFailure {
                throw it
            }
        }
    }

    private fun onStreetViewClick(latLng: LatLng, navigateToStreetView: (LatLng) -> Unit) =
        launchCatching {
            _uiState.update { it.copy(componentLoadingState = ComponentLoadingState.STREET_VIEW) }

            val isStreetAvailable = withContext(Dispatchers.IO) {
                MapUtils.fetchStreetViewData(latLng)
            }
            when (isStreetAvailable) {
                Status.OK -> {
                    navigateToStreetView(latLng)
                }

                else -> {
                    SnackbarManager.showMessage("Street View is not available for this location")
                }
            }

            _uiState.update { it.copy(componentLoadingState = ComponentLoadingState.NOTHING) }
        }

    fun loadLocationFromFavourite(favouriteId: String) = launchCatching {
        val location = withContext(Dispatchers.IO) {
            realmSyncService.getFavourite(favouriteId)
        }.toLocation()

        _uiState.update {
            it.copy(
                location = location,
                bottomSearchState = false,
                bottomSheetState = MapBottomSheetState.SMALL_INFORMATION_CARD,
            )
        }
    }
}