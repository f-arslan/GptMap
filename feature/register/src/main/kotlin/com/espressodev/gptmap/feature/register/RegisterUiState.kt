package com.espressodev.gptmap.feature.register

import com.espressodev.gptmap.core.google.OneTapSignInUpResponse
import com.espressodev.gptmap.core.google.SignInUpWithGoogleResponse
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.model.google.GoogleResponse

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val verificationAlertState: LoadingState = LoadingState.Idle,
    val loadingState: LoadingState = LoadingState.Idle,
    val oneTapSignUpResponse: OneTapSignInUpResponse = GoogleResponse.Success(null),
    val signUpWithGoogleResponse: SignInUpWithGoogleResponse = GoogleResponse.Success(data = false)
)

sealed class RegisterEvent {
    data class OnFullNameChanged(val fullName: String) : RegisterEvent()
    data class OnEmailChanged(val email: String) : RegisterEvent()
    data class OnPasswordChanged(val password: String) : RegisterEvent()
    data class OnConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent()
    data class OnLoadingStateChanged(val state: LoadingState) : RegisterEvent()
    data class OnVerificationAlertStateChanged(val state: LoadingState) : RegisterEvent()
    data object OnGoogleClicked : RegisterEvent()
    data object OnRegisterClicked : RegisterEvent()
}
