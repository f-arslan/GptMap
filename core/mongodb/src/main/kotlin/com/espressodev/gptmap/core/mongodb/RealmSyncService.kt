package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.realm.RealmUser

interface RealmSyncService {
    suspend fun saveUser(realmUser: RealmUser): Result<Boolean>
}
