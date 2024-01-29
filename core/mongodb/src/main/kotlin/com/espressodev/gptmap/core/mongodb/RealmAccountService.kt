package com.espressodev.gptmap.core.mongodb

interface RealmAccountService {
    suspend fun loginWithEmail(token: String): Result<Unit>
    suspend fun logOut()

}
