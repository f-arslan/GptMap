package com.espressodev.gptmap.feature.login

import android.util.Log
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.ext.isValidEmail
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.google_auth.GoogleAuthService
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.model.Response
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
    private val accountService: AccountService,
    private val firestoreService: FirestoreService,
    private val googleAuthService: GoogleAuthService,
    val oneTapClient: SignInClient,
    logService: LogService
) : GmViewModel(logService) {
    private val _uiState = MutableStateFlow(LoginUiState())
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
        accountService.firebaseSignInWithEmailAndPassword(email.trim(), password).apply {
            when (this) {
                is Response.Failure -> SnackbarManager.showMessage(AppText.email_or_password_error)
                Response.Loading -> {}
                is Response.Success -> {
                    accountService.reloadFirebaseUser()
                    if (accountService.isEmailVerified) {
                        // TODO: ADD EMAIL VERIFICATION STATUS TO DB
                        navigateToMap()
                    } else {
                        SnackbarManager.showMessage(AppText.please_verify_email)
                    }
                }
            }
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
        _uiState.update { it.copy(oneTapSignInResponse = googleAuthService.oneTapSignInWithGoogle()) }
    }

    fun signInWithGoogle(googleCredential: AuthCredential, token: String?) = launchCatching {
        _uiState.update { it.copy(signInWithGoogleResponse = GoogleResponse.Loading) }
        _uiState.update {
            it.copy(
                signInWithGoogleResponse = googleAuthService.firebaseSignInWithGoogle(
                    googleCredential)
            )
        }
    }
}