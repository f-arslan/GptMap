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

sealed class NavigationState {
    data object None : NavigationState()
    data object NavigateToMap : NavigationState()
    data object NavigateToRegister : NavigationState()
    data object NavigateToForgotPassword : NavigationState()
}
