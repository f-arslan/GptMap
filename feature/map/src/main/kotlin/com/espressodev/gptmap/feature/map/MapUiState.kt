package com.espressodev.gptmap.feature.map

import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.feature.screenshot.ScreenshotState
import com.google.android.gms.maps.model.LatLng

enum class MapBottomSheetState {
    SMALL_INFORMATION_CARD, DETAIL_CARD, BOTTOM_SHEET_HIDDEN
}

enum class ComponentLoadingState {
    MY_LOCATION, MAP, NOTHING
}

val istanbul = Pair(41.0082, 28.9784)

data class MapUiState(
    val searchValue: String = "",
    val location: Location = Location(),
    val userFirstChar: Char = 'H',
    val componentLoadingState: ComponentLoadingState = ComponentLoadingState.NOTHING,
    val bottomSheetState: MapBottomSheetState = MapBottomSheetState.BOTTOM_SHEET_HIDDEN,
    val searchButtonEnabledState: Boolean = true,
    val searchTextFieldEnabledState: Boolean = true,
    val searchBarState: Boolean = true,
    val isFavouriteButtonPlaying: Boolean = false,
    val isMapButtonsVisible: Boolean = true,
    val myCurrentLocationState: Pair<Boolean, Pair<Double, Double>> = Pair(false, Pair(0.0, 0.0)),
    val screenshotState: ScreenshotState = ScreenshotState.IDLE,
    val imageGalleryState: Pair<Int, Boolean> = Pair(0, false),
    val isMyLocationButtonVisible: Boolean = true,
    val isLoading: Boolean = false,
) {
    val coordinatesLatLng: LatLng
        get() = location.content.coordinates.run { LatLng(latitude, longitude) }

    val myCoordinatesLatLng: LatLng
        get() = myCurrentLocationState.second.run { LatLng(first, second) }
}

sealed class MapUiEvent {
    data class OnSearchValueChanged(val text: String) : MapUiEvent()
    data object OnSearchClick : MapUiEvent()
    data object OnImageDismiss : MapUiEvent()
    data class OnImageClick(val pos: Int) : MapUiEvent()
    data object OnFavouriteClick : MapUiEvent()
    data object OnDetailSheetBackClick : MapUiEvent()
    data object OnBackClick : MapUiEvent()
    data object OnExploreWithAiClick : MapUiEvent()
    data object OnChatAiClick : MapUiEvent()
    data object OnProfileClick : MapUiEvent()
    data class OnExploreWithAiClickFromImage(val index: Int) : MapUiEvent()
    data object OnScreenshotProcessStarted : MapUiEvent()
    data object OnScreenshotProcessCancelled : MapUiEvent()
    data class OnStreetViewClick(val latLng: Pair<Double, Double>) : MapUiEvent()
    data object OnMyCurrentLocationClick : MapUiEvent()

    data object OnUnsetMyCurrentLocationState : MapUiEvent()
}

sealed interface NavigationState {
    data object None : NavigationState
    data class NavigateToStreetView(val latLng: Pair<Float, Float>) : NavigationState
    data object NavigateToScreenshot : NavigationState
    data object NavigateToProfile : NavigationState
    data class NavigateToSnapToScript(val imageId: String) : NavigationState
    data object NavigateToGallery : NavigationState
}
