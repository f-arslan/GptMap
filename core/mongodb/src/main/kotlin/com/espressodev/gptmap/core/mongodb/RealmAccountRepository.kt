package com.espressodev.gptmap.core.mongodb

interface RealmAccountRepository {
    suspend fun loginWithEmail(token: String): Result<Unit>
    suspend fun logOut()
    suspend fun revokeAccess(): Result<Unit>
}
