package com.espressodev.gptmap.core.data

import com.espressodev.gptmap.core.model.User
import kotlinx.coroutines.flow.Flow

interface FirestoreService {
    fun saveUser(user: User)
    suspend fun isUserInDatabase(email: String): Result<Boolean>
    suspend fun getUser(): User
    fun getUserFlow(): Flow<User?>
}
