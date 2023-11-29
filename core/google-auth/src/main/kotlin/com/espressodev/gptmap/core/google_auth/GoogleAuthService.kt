package com.espressodev.gptmap.core.google_auth

import com.espressodev.gptmap.core.model.google.GoogleResponse
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.AuthCredential

typealias OneTapSignInUpResponse = GoogleResponse<BeginSignInResult>
typealias SignInUpWithGoogleResponse = GoogleResponse<Boolean>

interface GoogleAuthService {
    suspend fun oneTapSignInWithGoogle(): OneTapSignInUpResponse
    suspend fun oneTapSignUpWithGoogle(): OneTapSignInUpResponse
    suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): SignInUpWithGoogleResponse
}