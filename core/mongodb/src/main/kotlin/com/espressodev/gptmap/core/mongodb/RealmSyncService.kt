package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.realm.RealmLocation
import com.espressodev.gptmap.core.model.realm.RealmUser

interface RealmSyncService {
    suspend fun saveUser(realmUser: RealmUser): Result<Boolean>

    suspend fun saveLocation(realmLocation: RealmLocation): Result<Boolean>

    fun isUserInDatabase(): Boolean
}
