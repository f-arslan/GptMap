package com.espressodev.gptmap.core.mongodb.impl

import com.espressodev.gptmap.core.model.User
import com.espressodev.gptmap.core.mongodb.RealmDatabaseService
import io.realm.kotlin.mongodb.App

class RealmDatabaseServiceImpl(app: App) : RealmDatabaseService {
    override suspend fun saveUserToDatabase(user: User) {
        TODO("Not yet implemented")
    }
}