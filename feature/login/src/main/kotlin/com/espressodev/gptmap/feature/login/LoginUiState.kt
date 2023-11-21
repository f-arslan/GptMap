package com.espressodev.gptmap.feature.login

import com.espressodev.gptmap.core.google_auth.OneTapSignInUpResponse
import com.espressodev.gptmap.core.google_auth.SignInUpWithGoogleResponse
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.model.google.GoogleResponse


data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loadingState: LoadingState = LoadingState.Idle,
    val oneTapSignInResponse: OneTapSignInUpResponse = GoogleResponse.Success(null),
    val signInWithGoogleResponse: SignInUpWithGoogleResponse = GoogleResponse.Success(false)
)

sealed class LoginEvent {
    data class OnEmailChanged(val email: String) : LoginEvent()
    data class OnPasswordChanged(val password: String) : LoginEvent()
    data class OnLoadingStateChanged (val state: LoadingState): LoginEvent()
    data object OnGoogleClicked : LoginEvent()
    data object OnFacebookClicked : LoginEvent()
    data object OnLoginClicked : LoginEvent()
}