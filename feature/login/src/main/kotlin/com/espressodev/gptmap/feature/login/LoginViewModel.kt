package com.espressodev.gptmap.feature.login

import com.espressodev.gptmap.core.model.Exceptions
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.model.ext.isValidEmail
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.domain.SignInUpWithGoogleUseCase
import com.espressodev.gptmap.core.domain.SignInWithEmailAndPasswordUseCase
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.model.google.GoogleResponse
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUpWithGoogleUseCase: SignInUpWithGoogleUseCase,
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    val oneTapClient: SignInClient,
    logService: LogService
) : GmViewModel(logService) {
    private val _uiState =
        MutableStateFlow(LoginUiState(email = "jogek78962@visignal.com", password = "Gptmap123"))
    val uiState = _uiState.asStateFlow()

    private val email get() = uiState.value.email
    private val password get() = uiState.value.password

    fun onEvent(event: LoginEvent, navigateToMap: () -> Unit = {}) {
        when (event) {
            is LoginEvent.OnEmailChanged -> _uiState.update { it.copy(email = event.email) }
            LoginEvent.OnGoogleClicked -> oneTapSignIn()
            LoginEvent.OnLoginClicked -> onLoginClick(navigateToMap)
            is LoginEvent.OnPasswordChanged -> _uiState.update { it.copy(password = event.password) }
            is LoginEvent.OnLoadingStateChanged -> _uiState.update { it.copy(loadingState = event.state) }
        }
    }

    private fun onLoginClick(navigateToMap: () -> Unit) = launchCatching {
        if (!formValidation()) return@launchCatching
        onEvent(LoginEvent.OnLoadingStateChanged(LoadingState.Loading))

        signInWithEmailAndPasswordUseCase(email, password)
            .onSuccess {
                onEvent(LoginEvent.OnLoadingStateChanged(LoadingState.Idle))
                delay(25L)
                navigateToMap()
            }.onFailure {
                if (it == Exceptions.FirebaseEmailVerificationIsFalseException()) {
                    SnackbarManager.showMessage(AppText.please_verify_email)
                } else {
                    it.message?.let { message -> SnackbarManager.showMessage(message) }
                }
                onEvent(LoginEvent.OnLoadingStateChanged(LoadingState.Idle))
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

        val oneTapSignInResponse = signInUpWithGoogleUseCase.oneTapSignInWithGoogle()

        _uiState.update { it.copy(oneTapSignInResponse = oneTapSignInResponse) }
    }

    fun signInWithGoogle(googleCredential: AuthCredential) = launchCatching {
        _uiState.update { it.copy(signInWithGoogleResponse = GoogleResponse.Loading) }

        val signInWithGoogleResponse =
            signInUpWithGoogleUseCase.firebaseSignInUpWithGoogle(googleCredential)

        _uiState.update { it.copy(signInWithGoogleResponse = signInWithGoogleResponse) }
    }
}
