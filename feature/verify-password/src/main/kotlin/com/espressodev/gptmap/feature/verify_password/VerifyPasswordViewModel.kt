package com.espressodev.gptmap.feature.verify_password

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.designsystem.Constants.GENERIC_ERROR_MSG
import com.espressodev.gptmap.core.model.ext.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.espressodev.gptmap.core.designsystem.R.string as AppText

data class VerifyPasswordUiState(val password: String = "", val isLoading: Boolean = false)

@HiltViewModel
class VerifyPasswordViewModel @Inject constructor(
    private val accountService: AccountService,
    private val ioDispatcher: CoroutineDispatcher,
    logService: LogService,
) :
    GmViewModel(logService) {
    private val _uiState = MutableStateFlow(VerifyPasswordUiState())
    val uiState = _uiState.asStateFlow()

    private val password
        get() = uiState.value.password

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }


    fun onDone(navigate: () -> Unit) = launchCatching {
        if (!uiState.value.password.isValidPassword()) {
            SnackbarManager.showMessage(AppText.password_error)
            return@launchCatching
        }

        _uiState.update { it.copy(isLoading = true) }
        runCatching {
            withContext(ioDispatcher) {
                accountService.email?.let { email ->
                    accountService.firebaseSignInWithEmailAndPassword(
                        email = email,
                        password = password
                    )
                }
            }
        }.onSuccess {
            _uiState.update { it.copy(isLoading = false) }
            navigate()
        }.onFailure { throwable ->
            _uiState.update { it.copy(isLoading = false) }
            SnackbarManager.showMessage(throwable.localizedMessage ?: GENERIC_ERROR_MSG)
        }
    }
}