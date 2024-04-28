package com.espressodev.gptmap.feature.login

import android.content.Context
import com.espressodev.gptmap.core.model.LoadingState

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loadingState: LoadingState = LoadingState.Idle,
)

sealed class LoginEvent {
    data class OnEmailChanged(val email: String) : LoginEvent()
    data class OnPasswordChanged(val password: String) : LoginEvent()
    data class OnLoadingStateChanged(val state: LoadingState) : LoginEvent()
    data class OnGoogleClicked(val context: Context) : LoginEvent()
    data object OnLoginClicked : LoginEvent()
    data object OnForgotPasswordClicked : LoginEvent()
    data object OnNotMemberClicked : LoginEvent()
}

sealed class LoginNavigationState {
    data object None : LoginNavigationState()
    data object NavigateToMap : LoginNavigationState()
    data object NavigateToRegister : LoginNavigationState()
    data object NavigateToForgotPassword : LoginNavigationState()
}
