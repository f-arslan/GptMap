package com.espressodev.gptmap.core.data.repository

import com.espressodev.gptmap.core.google.OneTapSignInUpResponse
import com.espressodev.gptmap.core.google.SignInUpWithGoogleResponse
import com.google.firebase.auth.AuthCredential

interface AuthenticationRepository {
    suspend fun oneTapSignUpWithGoogle(): OneTapSignInUpResponse
    suspend fun oneTapSignInWithGoogle(): OneTapSignInUpResponse
    suspend fun firebaseSignInUpWithGoogle(googleCredential: AuthCredential): SignInUpWithGoogleResponse
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit>
    suspend fun signUpWithEmailAndPassword(email: String, password: String, fullName: String): Result<Unit>
}
