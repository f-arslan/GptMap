package com.espressodev.gptmap.core.data

import com.espressodev.gptmap.core.model.User
import kotlinx.coroutines.flow.Flow

interface FirestoreService {
    val user: Flow<User?>
    suspend fun saveUser(user: User)
    suspend fun updateUserProfilePictureUrl(userId: String, profilePictureUrl: String)

    suspend fun updateUserEmailVerification(userId: String)
}