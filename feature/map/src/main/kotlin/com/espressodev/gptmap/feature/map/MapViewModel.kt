package com.espressodev.gptmap.feature.map

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.chatgpt.ChatgptService
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.model.chatgpt.ChatgptRequest
import com.espressodev.gptmap.core.model.chatgpt.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val chatgptService: ChatgptService,
    logService: LogService
) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    fun onSearchValueChange(text: String) = _uiState.update { it.copy(searchValue = text) }

    fun onSearchClick() = launchCatching {
        val requestBody = ChatgptRequest(
            model = "gpt-3.5-turbo",
            messages = listOf(Message(uiState.value.searchValue, "user")),
            temperature = 0.7
        )
        val response = chatgptService.getPrompt(requestBody)
        Log.d(TAG, response.toString())
    }

    private companion object {
        const val TAG = "MapViewModel"
    }
}