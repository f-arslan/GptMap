package com.espressodev.gptmap.core.data.repository.impl

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.espressodev.gptmap.core.data.di.Dispatcher
import com.espressodev.gptmap.core.data.di.GmDispatchers.IO
import com.espressodev.gptmap.core.data.repository.UserRepository
import com.espressodev.gptmap.core.data.util.runCatchingWithContext
import com.espressodev.gptmap.core.data.worker.DeleteUserFromRealmWorker
import com.espressodev.gptmap.core.datastore.DataStoreService
import com.espressodev.gptmap.core.firebase.AccountService
import com.espressodev.gptmap.core.firebase.FirestoreDataStore
import com.espressodev.gptmap.core.mongodb.UserManagementDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val firestoreDataStore: FirestoreDataStore,
    private val userManagementDataSource: UserManagementDataSource,
    private val accountService: AccountService,
    private val dataStoreService: DataStoreService
) : UserRepository {

    override suspend fun addIfNewUser() = runCatchingWithContext(ioDispatcher) {
        val isUserInRealmDb = userManagementDataSource.isUserInDatabase().getOrThrow()
        if (isUserInRealmDb) return@runCatchingWithContext
        val realmUser = firestoreDataStore.getUser().toRealmUser()
        userManagementDataSource.saveUser(realmUser).getOrThrow()
    }

    override suspend fun deleteUser(): Result<Unit> = runCatchingWithContext(ioDispatcher) {
        launch {
            dataStoreService.clear()
        }
        val user = firestoreDataStore.deleteUser().getOrThrow()
        accountService.revokeAccess().getOrElse { throwable ->
            firestoreDataStore.saveUser(user)
            throw throwable
        }
        launch {
            deleteUserFromRealm()
        }
        Unit
    }

    private fun deleteUserFromRealm() {
        val workRequest = OneTimeWorkRequestBuilder<DeleteUserFromRealmWorker>()
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
