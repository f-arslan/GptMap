package com.espressodev.gptmap.feature.forgot_password

import android.content.res.Resources
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.ext.isValidEmail
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.model.LoadingState
import com.espressodev.gptmap.core.model.Response
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.espressodev.gptmap.core.designsystem.R.string as AppText
class ForgotPasswordViewModel @Inject constructor(
    private val accountService: AccountService,
    private val resources: Resources,
    logService: LogService
): GmViewModel(logService) {
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.update { newEmail }
    }

    fun onLoadingStateChange(newLoadingState: LoadingState) {
        _loadingState.update { newLoadingState }
    }

    fun sendPasswordResetEmail(clearAndNavigateLogin: () -> Unit) {
        if (!email.value.isValidEmail()) {
            SnackbarManager.showMessage(resources.getString(AppText.email_error))
            return
        }
        launchCatching {
            onLoadingStateChange(LoadingState.Loading)
            val response = accountService.sendPasswordResetEmail(email.value.trim())
            if (response is Response.Success) {
                onLoadingStateChange(LoadingState.Idle)
                delay(50L)
                clearAndNavigateLogin()
            } else if (response is Response.Failure) {
                SnackbarManager.showMessage(
                    response.e.message ?: resources.getString(AppText.email_error)
                )
            }
            onLoadingStateChange(LoadingState.Idle)
        }
    }
}
