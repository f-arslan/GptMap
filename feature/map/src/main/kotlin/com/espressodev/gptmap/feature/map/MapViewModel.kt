package com.espressodev.gptmap.feature.map

import com.espressodev.gptmap.core.chatgpt.ChatgptService
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val chatgptService: ChatgptService,
    logService: LogService,
) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()
    fun onSearchValueChange(text: String) = _uiState.update { it.copy(searchValue = text) }

    fun onSearchClick() = launchCatching {
        _uiState.update {
            it.copy(
                loadingState = LoadingState.Loading,
                searchButtonEnabledState = false,
                searchTextFieldEnabledState = false,
            )
        }

        chatgptService.getPrompt(uiState.value.searchValue).onSuccess { location ->
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


    fun onDismissBottomSheet() {
        _uiState.update { it.copy(bottomState = MapBottomState.SEARCH) }
    }

    fun onFavouriteClick() {

    }

    private companion object {
        const val TAG = "MapViewModel"
    }
}