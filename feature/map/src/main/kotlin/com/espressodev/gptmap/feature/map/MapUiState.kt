package com.espressodev.gptmap.feature.map

import com.espressodev.gptmap.core.model.Location
import com.espressodev.gptmap.core.model.Response


data class MapUiState(
    val searchValue: String = "",
    val location: Response<Location> = Response.Idle
)