package com.espressodev.gptmap.feature.map

import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.model.Location
import com.google.android.gms.maps.model.LatLng

enum class MapBottomState {
    SEARCH, DETAIL
}


data class MapUiState(
    val searchValue: String = "",
    val location:Location = Location(),
    val loadingState: LoadingState = LoadingState.Idle,
    val searchButtonEnabledState: Boolean = true,
    val searchTextFieldEnabledState: Boolean = true,
    val bottomState: MapBottomState = MapBottomState.SEARCH,
    val imageGalleryState: Pair<Int, Boolean> = Pair(0, false)
)

sealed class MapUiEvent {
    data class OnSearchValueChanged(val text: String) : MapUiEvent()
    data object OnSearchClick : MapUiEvent()
    data object OnImageDismiss : MapUiEvent()
    data class OnImageClick(val pos: Int) : MapUiEvent()
    data object OnFavouriteClick : MapUiEvent()

    data class OnStreetViewClick(val latLng: LatLng) : MapUiEvent()

}