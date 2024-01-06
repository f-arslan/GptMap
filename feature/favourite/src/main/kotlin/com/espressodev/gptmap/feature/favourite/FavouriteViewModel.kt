package com.espressodev.gptmap.feature.favourite

import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.model.Exceptions.RealmFailedToLoadFavouritesException
import com.espressodev.gptmap.core.model.Favourite
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    realmSyncService: RealmSyncService,
    logService: LogService,
    ioDispatcher: CoroutineDispatcher
) : GmViewModel(logService) {
    val favourites: StateFlow<Response<List<Favourite>>> =
        realmSyncService
            .getFavourites()
            .map<List<Favourite>, Response<List<Favourite>>> { favouritesList ->
                Response.Success(favouritesList)
            }
            .catch { exception ->
                logService.logNonFatalCrash(exception)
                emit(Response.Failure(RealmFailedToLoadFavouritesException()))
            }
            .flowOn(ioDispatcher)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                Response.Loading
            )
}
