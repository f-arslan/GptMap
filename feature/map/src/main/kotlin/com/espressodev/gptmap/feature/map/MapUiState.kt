package com.espressodev.gptmap.feature.map

import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.locationDefault
import com.espressodev.gptmap.feature.screenshot.ScreenshotState
import com.google.android.gms.maps.model.LatLng

enum class MapBottomSheetState {
    SMALL_INFORMATION_CARD, DETAIL_CARD, BOTTOM_SHEET_HIDDEN
}

enum class ComponentLoadingState {
    MY_LOCATION, MAP, NOTHING
}

fun Pair<Double, Double>.toLatLng() = LatLng(first, second)

data class ImageGalleryState(
    val currentIndex: Int = 0,
    val shouldShownGallery: Boolean = false
)

data class MapUiState(
    val searchValue: String = "",
    val location: Location = locationDefault,
    val userFirstChar: Char = 'H',
    val componentLoadingState: ComponentLoadingState = ComponentLoadingState.NOTHING,
    val bottomSheetState: MapBottomSheetState = MapBottomSheetState.BOTTOM_SHEET_HIDDEN,
    val screenshotState: ScreenshotState = ScreenshotState.IDLE,
    val imageGalleryState: ImageGalleryState = ImageGalleryState(),
    val isLoading: Boolean = false,
    val isComponentVisible: Boolean = true,
    val isSearchBarVisible: Boolean = true
) {
    val coordinatesLatLng: LatLng
        get() = location.content.coordinates.run { LatLng(latitude, longitude) }
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
}

sealed interface NavigationState {
    data object None : NavigationState
    data class NavigateToStreetView(val latLng: Pair<Float, Float>) : NavigationState
    data object NavigateToScreenshot : NavigationState
    data object NavigateToProfile : NavigationState
    data class NavigateToSnapToScript(val imageId: String) : NavigationState
    data object NavigateToGallery : NavigationState
}
