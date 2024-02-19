package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.Favourite
import com.espressodev.gptmap.core.model.realm.RealmFavourite
import kotlinx.coroutines.flow.Flow

interface FavouriteDataSource {
    suspend fun saveFavourite(realmFavourite: RealmFavourite): Result<Unit>
    fun getFavourites(): Flow<List<Favourite>>
    fun getFavourite(id: String): Favourite
    suspend fun deleteFavourite(favouriteId: String): Result<Unit>
    suspend fun updateFavouriteText(favouriteId: String, text: String): Result<Unit>
    suspend fun updateImageAnalysisId(favouriteId: String, messageId: String, imageAnalysisId: String): Result<Unit>
}
