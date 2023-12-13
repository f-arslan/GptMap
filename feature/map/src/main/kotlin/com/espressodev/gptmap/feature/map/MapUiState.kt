package com.espressodev.gptmap.feature.map

import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.model.chatgpt.Content
import com.espressodev.gptmap.core.model.chatgpt.Coordinates

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
    data object OnDismissBottomSheet : MapUiEvent()
    data object OnImageDismiss : MapUiEvent()
    data class OnImageClick(val pos: Int) : MapUiEvent()
    data object OnFavouriteClick : MapUiEvent()
    data object OnStreetViewClick : MapUiEvent()
}