package com.espressodev.gptmap.core.data.repository

import android.content.Context
import com.espressodev.gptmap.core.model.google.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    fun signInUpWithGoogle(context: Context): Flow<AuthState<Unit>>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit>
    suspend fun signUpWithEmailAndPassword(email: String, password: String, fullName: String): Result<Unit>
}
