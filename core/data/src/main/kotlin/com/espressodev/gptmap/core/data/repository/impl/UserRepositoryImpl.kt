package com.espressodev.gptmap.core.data.repository.impl

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.espressodev.gptmap.core.data.repository.UserRepository
import com.espressodev.gptmap.core.data.util.runCatchingWithContext
import com.espressodev.gptmap.core.data.worker.DeleteUserFromRealmWorker
import com.espressodev.gptmap.core.datastore.DataStoreService
import com.espressodev.gptmap.core.firebase.AccountService
import com.espressodev.gptmap.core.firebase.FirestoreRepository
import com.espressodev.gptmap.core.model.di.Dispatcher
import com.espressodev.gptmap.core.model.di.GmDispatchers.IO
import com.espressodev.gptmap.core.model.firebase.toRealmUser
import com.espressodev.gptmap.core.mongodb.UserManagementRealmRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val firestoreRepository: FirestoreRepository,
    private val userManagementRealmRepository: UserManagementRealmRepository,
    private val accountService: AccountService,
    private val dataStoreService: DataStoreService
) : UserRepository {

    override suspend fun addIfNewUser() = runCatchingWithContext(ioDispatcher) {
        val isUserInRealmDb = userManagementRealmRepository.isUserInDatabase().getOrThrow()
        if (isUserInRealmDb) return@runCatchingWithContext
        val realmUser = firestoreRepository.getUser().toRealmUser()
        userManagementRealmRepository.saveUser(realmUser).getOrThrow()
    }

    override suspend fun deleteUser(): Result<Unit> = runCatchingWithContext(ioDispatcher) {
        launch {
            dataStoreService.clear()
        }
        val user = firestoreRepository.deleteUser().getOrThrow()
        accountService.revokeAccess().getOrElse { throwable ->
            firestoreRepository.saveUser(user)
            throw throwable
        }
        launch {
            deleteUserFromRealm()
        }
        Unit
    }

    override suspend fun getUserFirstChar(): Result<Char> = runCatchingWithContext(ioDispatcher) {
        val fullName = dataStoreService.getUserFullName().first()
        if (fullName.isEmpty()) {
            val fetchedFullName = fetchAndSetFullName().getOrThrow()
            fetchedFullName.first()
        } else {
            'U'
        }
    }

    override suspend fun getLatestImageId(): Result<String> = runCatchingWithContext(ioDispatcher) {
        dataStoreService.getLatestImageIdForChat().first()
    }

    private fun deleteUserFromRealm() {
        val workRequest = OneTimeWorkRequestBuilder<DeleteUserFromRealmWorker>()
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    private suspend fun fetchAndSetFullName() = runCatching {
        val fullName = firestoreRepository.getUserFullName().getOrThrow()
        dataStoreService.setUserFullName(fullName)
        fullName
    }.onFailure { throwable ->
        Log.e(TAG, "Failed to fetch and set full name", throwable)
    }


    private companion object {
        private const val TAG = "UserRepositoryImpl"
    }
}
