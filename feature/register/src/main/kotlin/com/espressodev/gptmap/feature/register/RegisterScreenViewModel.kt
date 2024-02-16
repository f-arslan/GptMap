package com.espressodev.gptmap.feature.register

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.domain.SignInUpWithGoogleUseCase
import com.espressodev.gptmap.core.domain.SignUpWithEmailAndPasswordUseCase
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.model.ext.isValidEmail
import com.espressodev.gptmap.core.model.ext.isValidName
import com.espressodev.gptmap.core.model.ext.isValidPassword
import com.espressodev.gptmap.core.model.ext.passwordMatches
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
    private val signUpWithEmailAndPasswordUseCase: SignUpWithEmailAndPasswordUseCase,
    private val signInUpWithGoogleUseCase: SignInUpWithGoogleUseCase,
    val oneTapClient: SignInClient,
    logService: LogService
) : GmViewModel(logService) {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val email
        get() = uiState.value.email

    private val password
        get() = uiState.value.password

    private val fullName
        get() = uiState.value.fullName


    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.OnFullNameChanged ->
                _uiState.update { it.copy(fullName = event.fullName) }
            is RegisterEvent.OnEmailChanged ->
                _uiState.update { it.copy(email = event.email) }
            is RegisterEvent.OnPasswordChanged ->
                _uiState.update { it.copy(password = event.password) }
            is RegisterEvent.OnConfirmPasswordChanged ->
                _uiState.update { it.copy(confirmPassword = event.confirmPassword) }
            is RegisterEvent.OnLoadingStateChanged ->
                _uiState.update { it.copy(loadingState = event.state) }
            is RegisterEvent.OnVerificationAlertStateChanged ->
                _uiState.update { it.copy(verificationAlertState = event.state) }

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
        if (!formValidation()) return@launchCatching
        onEvent(RegisterEvent.OnLoadingStateChanged(LoadingState.Loading))

        signUpWithEmailAndPasswordUseCase(email.trim(), password, fullName)
            .onSuccess {
                onEvent(RegisterEvent.OnLoadingStateChanged(LoadingState.Idle))
                onEvent(RegisterEvent.OnVerificationAlertStateChanged(LoadingState.Loading))
            }
            .onFailure {
                it.message?.let { error -> SnackbarManager.showMessage(error) }
            }

        onEvent(RegisterEvent.OnLoadingStateChanged(LoadingState.Idle))
    }

    private fun oneTapSignUp() = launchCatching {
        _uiState.update { it.copy(oneTapSignUpResponse = GoogleResponse.Loading) }

        val oneTapSignUpResponse = signInUpWithGoogleUseCase.oneTapSignUpWithGoogle()

        _uiState.update { it.copy(oneTapSignUpResponse = oneTapSignUpResponse) }
    }

    fun signUpWithGoogle(googleCredential: AuthCredential) = launchCatching {
        _uiState.update { it.copy(signUpWithGoogleResponse = GoogleResponse.Loading) }

        val signInUpWithGoogleResponse =
            signInUpWithGoogleUseCase.firebaseSignInUpWithGoogle(googleCredential)

        _uiState.update { it.copy(signUpWithGoogleResponse = signInUpWithGoogleResponse) }
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
