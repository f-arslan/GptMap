package com.espressodev.gptmap.core.data

import com.espressodev.gptmap.core.model.User

interface FirestoreService {
    fun saveUser(user: User)
    suspend fun isUserInDatabase(email: String): Result<Boolean>
    suspend fun getUser(userId: String): User
}
