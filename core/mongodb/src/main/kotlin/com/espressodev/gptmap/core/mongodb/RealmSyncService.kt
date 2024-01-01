package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.Favourite
import com.espressodev.gptmap.core.model.RealmImageAnalysis
import com.espressodev.gptmap.core.model.realm.RealmFavourite
import com.espressodev.gptmap.core.model.realm.RealmUser
import kotlinx.coroutines.flow.Flow

interface RealmSyncService {
    suspend fun saveUser(realmUser: RealmUser): Result<Boolean>

    suspend fun saveFavourite(realmFavourite: RealmFavourite): Result<Boolean>

    suspend fun saveImageAnalysis(realmImageAnalysis: RealmImageAnalysis): Result<Boolean>

    fun isUserInDatabase(): Boolean

    fun getFavourites(): Flow<List<Favourite>>

    fun getFavourite(id: String): Favourite
}
