package com.espressodev.gptmap.core.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.espressodev.gptmap.core.mongodb.RealmAccountRepository
import com.espressodev.gptmap.core.mongodb.UserManagementRealmRepository

@HiltWorker
class DeleteUserFromRealmWorker(
    private val userManagementRealmRepository: UserManagementRealmRepository,
    private val realmAccountRepository: RealmAccountRepository,
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = try {
        userManagementRealmRepository.deleteUser().getOrThrow()
        realmAccountRepository.revokeAccess()
        Result.success()
    } catch (e: Exception) {
        Log.e("DeleteUserFromRealmWorker", "doWork: failure $e")
        Result.failure()
    }

    class Factory(
        private val userManagementRealmRepository: UserManagementRealmRepository,
        private val realmAccountRepository: RealmAccountRepository
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ) = if (workerClassName == DeleteUserFromRealmWorker::class.java.name) {
            DeleteUserFromRealmWorker(
                userManagementRealmRepository,
                realmAccountRepository,
                appContext,
                workerParameters
            )
        } else {
            null
        }
    }
}
