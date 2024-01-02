package com.espressodev.gptmap.feature.image_analyses

import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.model.ImageAnalysis
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class ImageAnalysesViewModel @Inject constructor(
    private val realmSyncService: RealmSyncService,
    logService: LogService
) : GmViewModel(logService) {
    val imageAnalyses = realmSyncService
        .getImageAnalyses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

}