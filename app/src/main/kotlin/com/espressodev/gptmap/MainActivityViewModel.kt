package com.espressodev.gptmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

enum class AccountState {
    Loading,
    UserAlreadySignIn,
    UserNotSignIn,
}

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    accountService: AccountService,
    realmAccountService: RealmAccountService,
) : ViewModel() {
    private val _accountState = MutableStateFlow(AccountState.Loading)
    val accountService = _accountState.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching {
                if (accountService.isEmailVerified) {
                    accountService.firebaseUser?.getIdToken(true)?.await()?.token?.let {
                        realmAccountService.loginWithEmail(it).getOrElse {
                            _accountState.update { AccountState.UserNotSignIn }
                        }.run {
                            _accountState.update { AccountState.UserAlreadySignIn }
                        }
                    }
                } else {
                    _accountState.update { AccountState.UserNotSignIn }
                }
            }.onFailure {
                _accountState.update { AccountState.UserNotSignIn }
            }
        }
    }
}
