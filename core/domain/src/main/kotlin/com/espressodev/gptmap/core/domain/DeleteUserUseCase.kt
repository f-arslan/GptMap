package com.espressodev.gptmap.core.domain

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.espressodev.gptmap.core.common.DataStoreService
import com.espressodev.gptmap.core.data.AccountService
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.ext.runCatchingWithContext
import com.espressodev.gptmap.core.worker.DeleteUserFromRealmWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val accountService: AccountService,
    private val firestoreService: FirestoreService,
    @ApplicationContext private val context: Context,
    private val dataStoreService: DataStoreService,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke() = runCatchingWithContext(ioDispatcher) {
        launch {
            dataStoreService.clear()
        }
        val user = firestoreService.deleteUser().getOrThrow()
        accountService.revokeAccess().getOrElse { throwable ->
            firestoreService.saveUser(user)
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
