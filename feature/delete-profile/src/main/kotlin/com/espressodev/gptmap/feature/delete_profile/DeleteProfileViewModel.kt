package com.espressodev.gptmap.feature.delete_profile

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.common.snackbar.SnackbarManager
import com.espressodev.gptmap.core.designsystem.Constants.GENERIC_ERROR_MSG
import com.espressodev.gptmap.core.domain.DeleteUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DeleteProfileViewModel @Inject constructor(
    logService: LogService,
    private val deleteUserUseCase: DeleteUserUseCase
) : GmViewModel(logService) {

    private val _isDialogOpened = MutableStateFlow(value = false)
    val isDialogOpened = _isDialogOpened.asStateFlow()

    private val _isLoading = MutableStateFlow(value = false)
    val isLoading = _isLoading.asStateFlow()

    fun onDeleteClick() {
        _isDialogOpened.update { true }
    }

    fun onDismissDialog() {
        _isDialogOpened.update { false }
    }

    fun onConfirmDialog(navigate: () -> Unit, popUp: () -> Unit) =
        launchCatching {
            _isLoading.update { true }
            deleteUserUseCase()
                .onSuccess {
                    _isLoading.update { false }
                    delay(25L)
                    navigate()
                }
                .onFailure { throwable ->
                    SnackbarManager.showMessage(
                        throwable.localizedMessage ?: GENERIC_ERROR_MSG
                    )
                    _isLoading.update { false }
                    delay(25L)
                    popUp()
                }
        }
}
