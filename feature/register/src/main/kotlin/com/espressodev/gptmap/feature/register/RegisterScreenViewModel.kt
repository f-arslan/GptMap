package com.espressodev.gptmap.feature.register

import android.content.Context
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.data.repository.AuthenticationRepository
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.model.ext.isValidEmail
import com.espressodev.gptmap.core.model.ext.isValidName
import com.espressodev.gptmap.core.model.ext.isValidPassword
import com.espressodev.gptmap.core.model.ext.passwordMatches
import com.espressodev.gptmap.core.model.google.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    logService: LogService
) : GmViewModel(logService) {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationState = MutableStateFlow(NavigationState.Idle)
    val navigationState = _navigationState.asStateFlow()

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

            is RegisterEvent.OnGoogleClicked -> signUpWithGoogle(event.context)
            RegisterEvent.OnRegisterClicked -> onRegisterClick()
            RegisterEvent.OnAlreadyHaveAccountClick -> {
                _navigationState.update { NavigationState.Login }
            }
        }
    }

    fun handleVerificationAndNavigate() = launchCatching {
        onEvent(RegisterEvent.OnVerificationAlertStateChanged(LoadingState.Idle))
        delay(150L)
        _navigationState.update { NavigationState.Login }
    }

    private fun onRegisterClick() = launchCatching {
        if (!formValidation()) return@launchCatching
        onEvent(RegisterEvent.OnLoadingStateChanged(LoadingState.Loading))

        authenticationRepository.signUpWithEmailAndPassword(email.trim(), password, fullName)
            .onSuccess {
                onEvent(RegisterEvent.OnLoadingStateChanged(LoadingState.Idle))
                onEvent(RegisterEvent.OnVerificationAlertStateChanged(LoadingState.Loading))
            }
            .onFailure {
                it.message?.let { error -> SnackbarManager.showMessage(error) }
            }

        onEvent(RegisterEvent.OnLoadingStateChanged(LoadingState.Idle))
    }

    private fun signUpWithGoogle(context: Context) = launchCatching {
        authenticationRepository.signInUpWithGoogle(context).collect { authState ->
            when (authState) {
                is AuthState.Error -> println(authState.e)
                AuthState.Idle -> Unit
                AuthState.Loading -> _uiState.update { it.copy(loadingState = LoadingState.Loading) }
                is AuthState.Success -> {
                    _uiState.update { it.copy(loadingState = LoadingState.Idle) }
                    delay(100)
                    _navigationState.update { NavigationState.Map }
                }
            }
        }
    }

    fun resetNavigation() {
        _navigationState.update { NavigationState.Idle }
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
