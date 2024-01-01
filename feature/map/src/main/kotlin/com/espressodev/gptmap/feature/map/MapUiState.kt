package com.espressodev.gptmap.feature.map

import com.espressodev.gptmap.core.model.Location
import com.google.android.gms.maps.model.LatLng

enum class MapBottomSheetState {
    SMALL_INFORMATION_CARD, DETAIL_CARD, NOTHING
}

enum class ComponentLoadingState {
    STREET_VIEW, MAP, NOTHING
}

data class MapUiState(
    val searchValue: String = "",
    val location:Location = Location(),
    val componentLoadingState: ComponentLoadingState = ComponentLoadingState.NOTHING,
    val bottomSheetState: MapBottomSheetState = MapBottomSheetState.NOTHING,
    val searchButtonEnabledState: Boolean = true,
    val searchTextFieldEnabledState: Boolean = true,
    val bottomSearchState: Boolean = true,
    val isTopButtonsVisible: Boolean = true,
    val isFavouriteButtonPlaying: Boolean = false,
    val takeScreenshotState: Boolean = false,
    val imageGalleryState: Pair<Int, Boolean> = Pair(0, false)
)

sealed class MapUiEvent {
    data class OnSearchValueChanged(val text: String) : MapUiEvent()
    data object OnSearchClick : MapUiEvent()
    data object OnImageDismiss : MapUiEvent()
    data class OnImageClick(val pos: Int) : MapUiEvent()
    data object OnFavouriteClick : MapUiEvent()
    data object OnDetailSheetBackClick: MapUiEvent()
    data object OnBackClick: MapUiEvent()
    data object OnExploreWithAiClick: MapUiEvent()
    data object OnScreenshotProcessStarted: MapUiEvent()
    data object OnTakeScreenshotClick: MapUiEvent()
    data class OnStreetViewClick(val latLng: LatLng) : MapUiEvent()
}