package com.espressodev.gptmap.feature.profile

import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val accountService: AccountService,
    private val realmAccountService: RealmAccountService,
    firestoreService: FirestoreService,
    logService: LogService,
    ioDispatcher: CoroutineDispatcher,
) : GmViewModel(logService) {
    private val _userName = MutableStateFlow<Response<String>>(Response.Loading)
    val fullName = _userName.asStateFlow()

    init {
        launchCatching {
            val fullNameResult = withContext(ioDispatcher) { firestoreService.getUserFullName() }
            fullNameResult
                .onSuccess { fullName ->
                    _userName.update { Response.Success(fullName) }
                }
                .onFailure { throwable ->
                    _userName.update { Response.Failure(Exception(throwable.localizedMessage)) }
                    throw throwable
                }
        }
    }

    fun onLogoutClick(navigate: () -> Unit) = launchCatching {
        accountService.signOut()
        realmAccountService.logOut()
        navigate()
    }
}
