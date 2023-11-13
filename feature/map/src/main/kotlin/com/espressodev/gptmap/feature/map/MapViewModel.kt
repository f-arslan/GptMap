package com.espressodev.gptmap.feature.map

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(logService: LogService) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    fun onSearchValueChange(text: String) = _uiState.update { it.copy(searchValue = text) }

    fun onSearchClick() = launchCatching {

    }
}