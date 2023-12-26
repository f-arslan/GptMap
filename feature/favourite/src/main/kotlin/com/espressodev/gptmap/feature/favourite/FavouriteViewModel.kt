package com.espressodev.gptmap.feature.favourite

import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val realmSyncService: RealmSyncService,
    logService: LogService
) : GmViewModel(logService) {
    val favourites = realmSyncService.getFavourites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}