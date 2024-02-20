package com.espressodev.gptmap.core.data.repository

interface UserRepository {
    suspend fun addIfNewUser(): Result<Unit>
    suspend fun deleteUser(): Result<Unit>
    suspend fun getUserFirstChar(): Result<Char>
    suspend fun getLatestImageId(): Result<String>
}
