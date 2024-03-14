package com.espressodev.gptmap.feature.sub

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SubViewModel @Inject constructor(logService: LogService) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(SubUiState())
    val uiState = _uiState.asStateFlow()

    fun onMonthlyClick() {
        _uiState.update { it.copy(selectedCard = CardType.Monthly) }
    }

    fun onAnnualClick() {
        _uiState.update { it.copy(selectedCard = CardType.Annual) }
    }

    fun onSubClick() {

    }
}
