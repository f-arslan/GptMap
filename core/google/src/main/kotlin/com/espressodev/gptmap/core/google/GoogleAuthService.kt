package com.espressodev.gptmap.core.google

import android.content.Context
import com.espressodev.gptmap.core.model.google.AuthState
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface GoogleAuthService {
    fun googleSignInUp(context: Context): Flow<AuthState<AuthResult>>
}
