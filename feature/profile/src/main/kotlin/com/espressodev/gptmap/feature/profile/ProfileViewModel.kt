package com.espressodev.gptmap.feature.profile

import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.splash_navigation.SplashNavigationManager
import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.data.LogService
import com.espressodev.gptmap.core.model.Exceptions.FirebaseUserIsNullException
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.model.User
import com.espressodev.gptmap.core.mongodb.RealmAccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val accountService: AccountService,
    private val realmAccountService: RealmAccountService,
    private val firestoreService: FirestoreService,
    ioDispatcher: CoroutineDispatcher,
    logService: LogService
) : GmViewModel(logService) {
    val user = firestoreService
        .getUserFlow()
        .retryWhen { cause, attempt ->
            if (cause is CancellationException && attempt < MAX_RETRY_ATTEMPTS) {
                delay(RETRY_DELAY_MS)
                true
            } else {
                false
            }
        }
        .mapNotNull { it }
        .map<User, Response<User>> { Response.Success(it) }
        .catch { exception ->
            logService.logNonFatalCrash(exception)
            emit(Response.Failure(FirebaseUserIsNullException()))
        }
        .flowOn(ioDispatcher)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            Response.Loading
        )


    fun onLogoutClick(navigate: () -> Unit) = launchCatching {
        accountService.signOut()
        realmAccountService.logOut()
        SplashNavigationManager.onLogout()
        navigate()
    }

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 3L
        private const val RETRY_DELAY_MS = 2000L
    }
}