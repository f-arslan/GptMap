package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.model.User
import com.espressodev.gptmap.core.model.realm.RealmUser

typealias SaveUserToDatabaseResponse = Response<Boolean>

interface RealmDatabaseService {
    suspend fun saveUserToDatabase(realmUser : RealmUser): SaveUserToDatabaseResponse
}