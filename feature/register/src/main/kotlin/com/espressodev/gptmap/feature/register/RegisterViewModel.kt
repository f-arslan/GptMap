package com.espressodev.gptmap.feature.register

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.ext.isValidEmail
import com.espressodev.gptmap.core.common.ext.isValidName
import com.espressodev.gptmap.core.common.ext.isValidPassword
import com.espressodev.gptmap.core.common.ext.passwordMatches
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.google_auth.GoogleAuthService
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.model.Response
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
class RegisterScreenViewModel @Inject constructor(
    private val accountService: AccountService,
    private val googleAuthService: GoogleAuthService,
    val oneTapClient: SignInClient,
    logService: LogService
) : GmViewModel(logService) {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val email
        get() = uiState.value.email

    private val password
        get() = uiState.value.password

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnFullNameChanged -> _uiState.update { it.copy(fullName = event.fullName) }
            is RegisterEvent.OnEmailChanged -> _uiState.update { it.copy(email = event.email) }
            is RegisterEvent.OnPasswordChanged -> _uiState.update { it.copy(password = event.password) }
            is RegisterEvent.OnConfirmPasswordChanged -> _uiState.update { it.copy(confirmPassword = event.confirmPassword) }
            is RegisterEvent.OnLoadingStateChanged -> _uiState.update { it.copy(loadingState = event.state) }
            is RegisterEvent.OnVerificationAlertStateChanged -> _uiState.update { it.copy(verificationAlertState = event.state) }
            RegisterEvent.OnGoogleClicked -> oneTapSignUp()
            RegisterEvent.OnRegisterClicked -> onRegisterClick()
        }
    }


    fun handleVerificationAndNavigate(clearAndNavigateLogin: () -> Unit) = launchCatching {
        onEvent(RegisterEvent.OnVerificationAlertStateChanged(LoadingState.Idle))
        delay(200L)
        clearAndNavigateLogin()
    }


    private fun onRegisterClick() = launchCatching {
        onEvent(RegisterEvent.OnLoadingStateChanged(LoadingState.Loading))

        if (!formValidation()) return@launchCatching

        accountService.firebaseSignUpWithEmailAndPassword(email.trim(), password).apply {
            when (this) {
                is Response.Failure -> {
                    e.message?.let { SnackbarManager.showMessage(it) }
                }
                Response.Loading -> {}
                is Response.Success -> {
                    accountService.sendEmailVerification()
                    onEvent(RegisterEvent.OnLoadingStateChanged(LoadingState.Idle))
                    onEvent(RegisterEvent.OnVerificationAlertStateChanged(LoadingState.Loading))
                    saveUserToDb()
                }
            }
        }

        onEvent(RegisterEvent.OnLoadingStateChanged(LoadingState.Idle))
    }

    private suspend fun saveUserToDb() {

    }

    fun oneTapSignUp() = launchCatching {
        _uiState.update { it.copy(oneTapSignUpResponse = GoogleResponse.Loading) }
        _uiState.update { it.copy(oneTapSignUpResponse = googleAuthService.oneTapSignUpWithGoogle()) }
    }

    fun signUpWithGoogle(googleCredential: AuthCredential, token: String?) = launchCatching {
        _uiState.update { it.copy(signUpWithGoogleResponse = GoogleResponse.Loading) }
        _uiState.update {
            it.copy(
                signUpWithGoogleResponse = googleAuthService.firebaseSignInWithGoogle(googleCredential)
            )
        }
    }

    private fun formValidation(): Boolean =
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(AppText.email_error)
            false
        } else if (!password.isValidPassword()) {
            SnackbarManager.showMessage(AppText.password_error)
            false
        } else if (!password.passwordMatches(uiState.value.confirmPassword)) {
            SnackbarManager.showMessage(AppText.password_match_error)
            false
        } else if (!uiState.value.fullName.isValidName()) {
            SnackbarManager.showMessage(AppText.not_valid_name)
            false
        } else {
            true
        }
}
