package com.espressodev.gptmap.feature.favourite

import com.espressodev.gptmap.core.model.realm.RealmFavourite

data class FavouriteUiState(
    val favourites: List<RealmFavourite>
)
