package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.Favourite
import com.espressodev.gptmap.core.model.realm.RealmFavourite
import com.espressodev.gptmap.core.model.realm.RealmUser
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow

interface RealmSyncService {
    suspend fun saveUser(realmUser: RealmUser): Result<Boolean>

    suspend fun saveLocation(realmLocation: RealmFavourite): Result<Boolean>

    fun isUserInDatabase(): Boolean

    fun getFavourites(): Flow<List<Favourite>>
}
