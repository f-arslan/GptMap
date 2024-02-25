package com.espressodev.gptmap.feature.login

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.data.repository.AuthenticationRepository
import com.espressodev.gptmap.core.model.Exceptions.FirebaseEmailVerificationIsFalseException
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.model.ext.isValidEmail
import com.espressodev.gptmap.core.model.google.GoogleResponse
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    val oneTapClient: SignInClient,
    logService: LogService
) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState = MutableStateFlow<LoginNavigationState>(LoginNavigationState.None)
    val navigationState = _navigationState.asStateFlow()

    private val email get() = uiState.value.email
    private val password get() = uiState.value.password

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChanged -> _uiState.update { it.copy(email = event.email) }
            LoginEvent.OnGoogleClicked -> oneTapSignIn()
            LoginEvent.OnLoginClicked -> onLoginClick()
            is LoginEvent.OnPasswordChanged -> _uiState.update { it.copy(password = event.password) }
            is LoginEvent.OnLoadingStateChanged -> _uiState.update { it.copy(loadingState = event.state) }
            LoginEvent.OnForgotPasswordClicked ->
                _navigationState.update { LoginNavigationState.NavigateToForgotPassword }
            LoginEvent.OnNotMemberClicked -> _navigationState.update { LoginNavigationState.NavigateToRegister }
        }
    }

    fun resetNavigation() {
        _navigationState.update { LoginNavigationState.None }
    }

    private fun onLoginClick() = launchCatching {
        if (!formValidation()) return@launchCatching
        _uiState.update { it.copy(loadingState = LoadingState.Loading) }

        authenticationRepository.signInWithEmailAndPassword(email, password)
            .onSuccess {
                _uiState.update { it.copy(loadingState = LoadingState.Idle) }
                _navigationState.update { LoginNavigationState.NavigateToMap }
            }.onFailure {
                if (it is FirebaseEmailVerificationIsFalseException) {
                    SnackbarManager.showMessage(AppText.please_verify_email)
                } else {
                    it.message?.let { message -> SnackbarManager.showMessage(message) }
                }
                _uiState.update { uiState -> uiState.copy(loadingState = LoadingState.Idle) }
            }
    }

    private fun formValidation(): Boolean =
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(AppText.email_error)
            false
        } else if (password.isBlank()) {
            SnackbarManager.showMessage(AppText.empty_password_error)
            false
        } else true

    private fun oneTapSignIn() = launchCatching {
        _uiState.update { it.copy(oneTapSignInResponse = GoogleResponse.Loading) }

        val oneTapSignInResponse = authenticationRepository.oneTapSignInWithGoogle()

        _uiState.update { it.copy(oneTapSignInResponse = oneTapSignInResponse) }
    }

    fun signInWithGoogle(googleCredential: AuthCredential) = launchCatching {
        _uiState.update { it.copy(signInWithGoogleResponse = GoogleResponse.Loading) }

        val signInWithGoogleResponse =
            authenticationRepository.firebaseSignInUpWithGoogle(googleCredential)

        _uiState.update { it.copy(signInWithGoogleResponse = signInWithGoogleResponse) }
    }
}
