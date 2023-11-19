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
    val location: Response<Location>,
    val loadingState: LoadingState = LoadingState.Idle,
    val searchButtonEnabledState: Boolean = true,
    val searchTextFieldEnabledState: Boolean = true,
    val bottomState: MapBottomState = MapBottomState.SEARCH
) {
    // TODO: THIS LOGIC WILL BE MOVE TO MONGODB
    constructor() : this(
        location = Response.Success(
            Location(
                id = "default",
                content = Content(
                    coordinates = Coordinates(
                        latitude = 41.0082,
                        longitude = 28.9784
                    ),
                    city = "Istanbul",
                    district = "Eminonu",
                    country = "Turkey",
                    poeticDescription = "Spires touch the sky, Bosphorus whispers tales—a city's heartbeat, where continents embrace, Istanbul's poetic dance.",
                    normalDescription = "Enchanting Istanbul, where East meets West in a vibrant blend of culture and history."
                )
            )
        )
    )
}