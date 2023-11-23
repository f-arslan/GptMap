package com.espressodev.gptmap.core.mongodb

import com.espressodev.gptmap.core.model.User

interface RealmDatabaseService {
    suspend fun saveUserToDatabase(user: User)
}