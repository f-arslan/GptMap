package com.espressodev.gptmap.feature.profile

import androidx.lifecycle.viewModelScope
import com.espressodev.gptmap.core.common.GmViewModel
import com.espressodev.gptmap.core.common.LogService
import com.espressodev.gptmap.core.datastore.DataStoreService
import com.espressodev.gptmap.core.firebase.AccountService
import com.espressodev.gptmap.core.firebase.FirestoreRepository
import com.espressodev.gptmap.core.model.Exceptions.FirestoreUserNotExistsException
import com.espressodev.gptmap.core.model.Response
import com.espressodev.gptmap.core.model.firebase.User
import com.espressodev.gptmap.core.model.di.Dispatcher
import com.espressodev.gptmap.core.model.di.GmDispatchers.IO
import com.espressodev.gptmap.core.mongodb.RealmAccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val accountService: AccountService,
    private val realmAccountRepository: RealmAccountRepository,
    private val dataStoreService: DataStoreService,
    firestoreRepository: FirestoreRepository,
    logService: LogService,
    @Dispatcher(IO) ioDispatcher: CoroutineDispatcher,
) : GmViewModel(logService) {
    val user: StateFlow<Response<User>> = firestoreRepository
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
        .map<User, Response<User>> {
            Response.Success(it)
        }
        .catch { throwable: Throwable ->
            logService.logNonFatalCrash(throwable)
            emit(Response.Failure(FirestoreUserNotExistsException()))
        }
        .flowOn(ioDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Response.Loading
        )

    fun onLogoutClick(navigate: () -> Unit) = launchCatching {
        dataStoreService.clear()
        accountService.signOut()
        realmAccountRepository.logOut()
        navigate()
    }

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 3L
        private const val RETRY_DELAY_MS = 2000L
    }
}
