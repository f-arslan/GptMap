package com.espressodev.gptmap

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.espressodev.gptmap.core.data.FirestoreService
import com.espressodev.gptmap.core.domain.worker.UpdateDatabaseIfUserEmailVerificationIsFalseWorker
import com.espressodev.gptmap.core.mongodb.RealmSyncService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GmHiltApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: UpdateDatabaseIfUserEmailVerificationIsFalseWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
}


class UpdateDatabaseIfUserEmailVerificationIsFalseWorkerFactory @Inject constructor(
    private val firestoreService: FirestoreService,
    private val realmSyncService: RealmSyncService
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker =
        UpdateDatabaseIfUserEmailVerificationIsFalseWorker(
            firestoreService = firestoreService,
            realmSyncService = realmSyncService,
            context = appContext,
            workerParameters = workerParameters
        )
}