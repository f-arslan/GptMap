package com.espressodev.gptmap.feature.register

import android.content.Context
import com.espressodev.gptmap.core.model.LoadingState

data class RegisterUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val verificationAlertState: LoadingState = LoadingState.Idle,
    val loadingState: LoadingState = LoadingState.Idle,
)

sealed class RegisterEvent {
    data class OnFullNameChanged(val fullName: String) : RegisterEvent()
    data class OnEmailChanged(val email: String) : RegisterEvent()
    data class OnPasswordChanged(val password: String) : RegisterEvent()
    data class OnConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent()
    data class OnLoadingStateChanged(val state: LoadingState) : RegisterEvent()
    data class OnVerificationAlertStateChanged(val state: LoadingState) : RegisterEvent()
    data class OnGoogleClicked(val context: Context) : RegisterEvent()
    data object OnRegisterClicked : RegisterEvent()
}
